package one.tranic.sewlia.config.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import one.tranic.sewlia.annotation.loader.After;
import one.tranic.sewlia.annotation.loader.DisableReload;
import one.tranic.sewlia.reflect.NewReflect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewConfigScanner {
    /**
     * Retrieves a map of classes within a specified package and their corresponding configuration keys,
     * sorted by dependency order.
     * <p>
     * This method scans the provided package to identify classes, maps them to their configuration keys,
     * and arranges them in a sorted order based on their dependencies.
     *
     * @param packageName the name of the package to scan for classes
     * @return a map containing classes as keys and their corresponding configuration keys as values,
     * or null if no classes are found in the specified package
     */
    public static @Nullable Map<Class<?>, String> getClasses(String packageName) {
        @Nullable Class<?>[] classes = NewReflect.scanPackage(packageName);
        if (classes == null || classes.length < 1) return null;

        Map<Class<?>, String> unsortedConfigClasses = mapClassesToConfigKeys(classes, packageName);
        return sortConfigClassesByDependency(unsortedConfigClasses);
    }

    /**
     * Maps an array of classes to their corresponding configuration keys based on a specified package name.
     * <p>
     * Generates unique keys for each class and caches annotated methods related to configuration actions if required.
     *
     * @param classes     An array of classes to be mapped to configuration keys.
     * @param packageName The base package name to derive the configuration keys.
     * @return A map where the keys are classes and the values are their respective configuration keys.
     */
    private static Map<Class<?>, String> mapClassesToConfigKeys(Class<?>[] classes, String packageName) {
        Map<Class<?>, String> configClassMap = new Object2ObjectOpenHashMap<>();
        boolean shouldInitializeActionMaps = ConfigAction.readActionMethodMap.isEmpty() && ConfigAction.writeActionMethodMap.isEmpty();

        for (Class<?> clazz : classes) {
            configClassMap.put(clazz, generateKey(clazz.getName(), packageName));

            if (shouldInitializeActionMaps) {
                ConfigUtils.cacheAnnotatedMethods(clazz);
            }
        }

        return configClassMap;
    }

    /**
     * Sorts a map of configuration classes by their dependencies using a topological sort.
     * <p>
     * The method ensures that dependencies are resolved in the correct order based on
     * the dependency graph derived from the provided classes.
     *
     * @param unsortedClasses A map where keys represent configuration classes and values
     *                        represent associated configuration keys.
     *                        <p>
     *                        These classes may have dependencies on each other.
     * @return A map containing the same entries as the input map but sorted such that
     * classes appear in the order of their dependencies.
     * <p>
     * Classes without dependencies or unspecified relationships are appended without reordering their relative positions.
     */
    private static Map<Class<?>, String> sortConfigClassesByDependency(Map<Class<?>, String> unsortedClasses) {
        Map<Class<?>, Set<Class<?>>> dependencyGraph = buildDependencyGraph(unsortedClasses.keySet());
        List<Class<?>> sortedClasses = ConfigUtils.topologicalSort(
                dependencyGraph,
                new ObjectOpenHashSet<>(unsortedClasses.keySet())
        );

        Map<Class<?>, String> sortedConfigClasses = new Object2ObjectLinkedOpenHashMap<>();

        for (Class<?> clazz : sortedClasses) {
            sortedConfigClasses.put(clazz, unsortedClasses.get(clazz));
        }

        for (Map.Entry<Class<?>, String> entry : unsortedClasses.entrySet()) {
            if (!sortedConfigClasses.containsKey(entry.getKey())) {
                sortedConfigClasses.put(entry.getKey(), entry.getValue());
            }
        }

        return sortedConfigClasses;
    }

    /**
     * Builds a dependency graph based on the relationships defined by the {@code @After} annotation
     * in the provided set of classes.
     * <p>
     * Each class that has an {@code @After} annotation specifying a dependency will be added to the graph,
     * where the dependency is represented as a directed edge.
     *
     * @param classes A set of classes that may contain the {@code @After} annotation to establish dependencies.
     * @return A map representing the dependency graph where keys are classes and values are sets of
     * classes that depend on the key.
     */
    private static Map<Class<?>, Set<Class<?>>> buildDependencyGraph(Set<Class<?>> classes) {
        Map<Class<?>, Set<Class<?>>> dependencyGraph = new Object2ObjectOpenHashMap<>();

        for (Class<?> clazz : classes) {
            After classAfterAnnotation = clazz.getAnnotation(After.class);
            if (classAfterAnnotation != null) {
                Class<?> dependsOn = classAfterAnnotation.value();
                if (classes.contains(dependsOn)) {
                    dependencyGraph.computeIfAbsent(dependsOn, k -> new ObjectOpenHashSet<>()).add(clazz);
                }
            }
        }

        return dependencyGraph;
    }

    /**
     * Generates a regular expression pattern for matching classes within a specific package.
     * <p>
     * The generated pattern starts with a caret (^) for matching the beginning of the string,
     * replaces all dots (.) in the package name with escaped dots (\\.), and ends with a literal dot (.).
     *
     * @param packageName The name of the package to generate the pattern for.
     *                    <p>
     *                    Must not be null or empty.
     * @return A String representing the regular expression pattern for classes within the specified package.
     */
    public static String generatePackage(String packageName) {
        return "^" + packageName.replace(".", "\\.") + "\\.";
    }

    /**
     * Generates a configuration key from a fully qualified class name and a package name.
     * <p>
     * The method removes the package name from the full class name,
     * converts camel case to kebab case, and replaces underscores with dashes.
     *
     * @param fullClassName The fully qualified name of the class.
     * @param packageName   The package name to be removed from the class name.
     * @return A formatted string representing the configuration key.
     */
    public static String generateKey(String fullClassName, String packageName) {
        String cleanedName = fullClassName.replaceFirst(generatePackage(packageName), "");
        cleanedName = cleanedName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        cleanedName = cleanedName.replaceAll("_", "-");
        return cleanedName;
    }

    /**
     * Processes a static field named "value" in the specified class,
     * writing its content to the given YAML configuration.
     * <p>
     * If the "value" field is not static, the method exits without performing any operation.
     *
     * @param clazz         the Class object representing the class containing the "value" field to be processed
     * @param key           the key to associate with the field's value in the YAML configuration
     * @param configuration the YamlConfiguration object to write the field's value to
     */
    public static void processStaticValueFieldWithWrite(Class<?> clazz, String key, YamlConfiguration configuration) {
        try {
            Field valueField = clazz.getDeclaredField("value");
            if (!Modifier.isStatic(valueField.getModifiers())) {
                return;
            }
            valueField.setAccessible(true);
            Object value = valueField.get(null);

            if (!applyDefaultIfSupported(value, key, configuration)) return;

            ConfigUtils.processCommentAnnotations(valueField, key, configuration);

            ConfigAction.invokeStaticMethod(clazz, ConfigAction.MethodType.WRITE);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    /**
     * Applies a default value to the provided configuration if the given static field value is of a supported type.
     *
     * @param staticFieldValue the value of the static field to be checked and possibly applied as a default
     * @param key              the configuration key associated with the default value
     * @param configuration    the YamlConfiguration object where the default value will be set
     * @return true if the default value is successfully applied and supported, otherwise false
     */
    private static boolean applyDefaultIfSupported(Object staticFieldValue, String key, YamlConfiguration configuration) {
        if (staticFieldValue instanceof Boolean || staticFieldValue instanceof Integer ||
                staticFieldValue instanceof Double || staticFieldValue instanceof String ||
                staticFieldValue instanceof Long || staticFieldValue instanceof Float ||
                staticFieldValue instanceof List) {
            configuration.addDefault(key, staticFieldValue);
            return true;
        }
        return false;
    }

    /**
     * Processes a static field named "value" within a specified class,
     * updating its value based on a YAML configuration.
     *
     * @param clazz         the class containing the static field "value"
     * @param key           the key in the YAML configuration to retrieve the value
     * @param configuration the YAML configuration used to fetch values
     * @param isReload      indicates whether the method is invoked during a reload operation
     */
    public static void processStaticValueFieldWithRead(Class<?> clazz, String key, YamlConfiguration configuration, boolean isReload) {
        try {
            Field valueField = clazz.getDeclaredField("value");
            if (!Modifier.isStatic(valueField.getModifiers())) {
                return;
            }
            valueField.setAccessible(true);
            Object value = valueField.get(null);
            if (isReload && valueField.getAnnotation(DisableReload.class) != null) {
                return;
            }

            switch (value) {
                case Boolean ignored -> valueField.set(null, configuration.getBoolean(key));
                case String ignored -> valueField.set(null, configuration.getString(key));
                case Integer ignored -> valueField.set(null, configuration.getInt(key));
                case Double ignored -> valueField.set(null, configuration.getDouble(key));
                case Long ignored -> valueField.set(null, configuration.getLong(key));
                case Float ignored -> valueField.set(null, configuration.getDouble(key));
                case List ignored -> {
                    List<?> target = configuration.getList(key);
                    if (target == null || target.isEmpty()) {
                        return;
                    }
                    valueField.set(null, new ObjectArrayList<>(target));
                }
                case null, default -> {
                }
            }

            ConfigAction.invokeStaticMethod(clazz, ConfigAction.MethodType.READ);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }
}