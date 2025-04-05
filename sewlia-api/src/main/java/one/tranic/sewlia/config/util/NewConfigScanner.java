package one.tranic.sewlia.config.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import one.tranic.sewlia.annotation.loader.After;
import one.tranic.sewlia.annotation.loader.DisableReload;
import one.tranic.sewlia.annotation.loader.ReadAction;
import one.tranic.sewlia.annotation.loader.WriteAction;
import one.tranic.sewlia.reflect.NewReflect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewConfigScanner {
    public static @Nullable Map<Class<?>, String> getClasses(String packageName) {
        @Nullable Class<?>[] classes = NewReflect.scanPackage(packageName);
        if (classes == null || classes.length < 1) return null;

        Map<Class<?>, String> unsortedConfigClasses = mapClassesToConfigKeys(classes, packageName);
        return sortConfigClassesByDependency(unsortedConfigClasses);
    }

    private static Map<Class<?>, String> mapClassesToConfigKeys(Class<?>[] classes, String packageName) {
        Map<Class<?>, String> configClassMap = new Object2ObjectOpenHashMap<>();
        boolean shouldInitializeActionMaps = ConfigAction.readActionMethodMap.isEmpty() && ConfigAction.writeActionMethodMap.isEmpty();

        for (Class<?> clazz : classes) {
            configClassMap.put(clazz, generateKey(clazz.getName(), packageName));

            if (shouldInitializeActionMaps) {
                cacheAnnotatedMethods(clazz);
            }
        }

        return configClassMap;
    }

    private static void cacheAnnotatedMethods(Class<?> clazz) {
        Method readMethod = findAnnotatedStaticMethod(clazz, ReadAction.class);
        if (readMethod != null) ConfigAction.readActionMethodMap.put(clazz, readMethod);

        Method writeMethod = findAnnotatedStaticMethod(clazz, WriteAction.class);
        if (writeMethod != null) ConfigAction.writeActionMethodMap.put(clazz, writeMethod);
    }

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

    public static String generatePackage(String packageName) {
        return "^" + packageName.replace(".", "\\.") + "\\.";
    }

    public static String generateKey(String fullClassName, String packageName) {
        String cleanedName = fullClassName.replaceFirst(generatePackage(packageName), "");
        cleanedName = cleanedName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        cleanedName = cleanedName.replaceAll("_", "-");
        return cleanedName;
    }

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

    private static boolean applyDefaultIfSupported(Object staticFieldValue, String key, YamlConfiguration configuration) {
        if (staticFieldValue instanceof Boolean || staticFieldValue instanceof Integer ||
                staticFieldValue instanceof Double || staticFieldValue instanceof String ||
                staticFieldValue instanceof List<?>) {
            configuration.addDefault(key, staticFieldValue);
            return true;
        }
        return false;
    }

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

    private static Method findAnnotatedStaticMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) &&
                    Modifier.isPublic(method.getModifiers()) &&
                    method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }
        return null;
    }
}