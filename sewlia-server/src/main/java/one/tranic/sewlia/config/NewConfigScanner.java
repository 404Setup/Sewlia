package one.tranic.sewlia.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.config.InlineComment;
import one.tranic.sewlia.annotation.config.InlineComments;
import one.tranic.sewlia.annotation.loader.After;
import one.tranic.sewlia.annotation.loader.DisableReload;
import one.tranic.sewlia.annotation.loader.ReadAction;
import one.tranic.sewlia.annotation.loader.WriteAction;
import one.tranic.sewlia.reflect.NewReflect;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewConfigScanner {
    private static final Map<Class<?>, Method> readActionMethodMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Method> writeActionMethodMap = new Object2ObjectOpenHashMap<>();

    public static @Nullable Map<Class<?>, String> getClasses() {
        Map<Class<?>, String> unsortedConfigClasses = new Object2ObjectOpenHashMap<>();

        @Nullable Set<Class<?>> classes = NewReflect.scanPackage("one.tranic.sewlia.config.mod");
        if (classes == null || classes.isEmpty()) return null;

        boolean canWriteActionMap = readActionMethodMap.isEmpty() && writeActionMethodMap.isEmpty();

        for (Class<?> clazz : classes) {
            unsortedConfigClasses.put(clazz, generateKey(clazz.getName()));
            if (!canWriteActionMap) continue;

            Method readMethod = findAnnotatedStaticMethod(clazz, ReadAction.class);
            if (readMethod != null) {
                readActionMethodMap.put(clazz, readMethod);
            }

            Method writeMethod = findAnnotatedStaticMethod(clazz, WriteAction.class);
            if (writeMethod != null) {
                writeActionMethodMap.put(clazz, writeMethod);
            }
        }

        // Create a dependency graph for sorting
        Map<Class<?>, Set<Class<?>>> dependencyGraph = new Object2ObjectOpenHashMap<>();

        // Build dependencies between classes
        for (Class<?> clazz : unsortedConfigClasses.keySet()) {
            After classAfterAnnotation = clazz.getAnnotation(After.class);
            if (classAfterAnnotation != null) {
                Class<?> dependsOn = classAfterAnnotation.value();
                // If the dependent class is also in the configuration class collection, 
                // establish a dependency relationship.
                if (unsortedConfigClasses.containsKey(dependsOn)) {
                    // The relationship here is: the dependsOn class should be processed before the current class.
                    // So the current class depends on the dependsOn class
                    dependencyGraph.computeIfAbsent(dependsOn, k -> new ObjectOpenHashSet<>()).add(clazz);
                }
            }
        }

        // Perform topological sorting
        List<Class<?>> sortedClasses = topologicalSort(dependencyGraph, new ObjectOpenHashSet<>(unsortedConfigClasses.keySet()));

        Map<Class<?>, String> sortedConfigClasses = new Object2ObjectLinkedOpenHashMap<>();
        for (Class<?> clazz : sortedClasses) sortedConfigClasses.put(clazz, unsortedConfigClasses.get(clazz));

        for (Map.Entry<Class<?>, String> entry : unsortedConfigClasses.entrySet()) {
            if (!sortedConfigClasses.containsKey(entry.getKey())) {
                sortedConfigClasses.put(entry.getKey(), entry.getValue());
            }
        }

        return sortedConfigClasses;
    }

    public static String generateKey(String fullClassName) {
        String cleanedName = fullClassName.replaceFirst("^one\\.tranic\\.sewlia\\.config\\.mod\\.", "");
        cleanedName = cleanedName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        cleanedName = cleanedName.replaceAll("_", "-");
        return cleanedName;
    }

    public static void processStaticValueFieldWithWrite(Class<?> clazz, String key) {
        try {
            Field valueField = clazz.getDeclaredField("value");
            if (!Modifier.isStatic(valueField.getModifiers())) {
                return;
            }
            valueField.setAccessible(true);
            Object value = valueField.get(null);

            if (!applyDefaultIfSupported(value, key)) return;

            processCommentAnnotations(valueField, key);

            invokeStaticMethod(clazz, MethodType.WRITE);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    private static boolean applyDefaultIfSupported(Object staticFieldValue, String key) {
        if (staticFieldValue instanceof Boolean || staticFieldValue instanceof Integer ||
                staticFieldValue instanceof Double || staticFieldValue instanceof String ||
                staticFieldValue instanceof List<?>) {
            ConfigUtils.getConfiguration().addDefault(key, staticFieldValue);
            return true;
        }
        return false;
    }

    public static void processStaticValueFieldWithRead(Class<?> clazz, String key, boolean isReload) {
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

            var configuration = ConfigUtils.getConfiguration();
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

            invokeStaticMethod(clazz, MethodType.READ);
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

    /**
     * Performs topological sorting.
     *
     * @param graph The dependency graph where keys are nodes and values are the nodes they depend on.
     * @param nodes All nodes that need to be sorted.
     * @return A list of nodes in topologically sorted order.
     * @throws IllegalStateException If a cyclic dependency is detected in the graph.
     */
    private static List<Class<?>> topologicalSort(Map<Class<?>, Set<Class<?>>> graph, Set<Class<?>> nodes) {
        List<Class<?>> result = new ObjectArrayList<>();
        Set<Class<?>> visited = new ObjectOpenHashSet<>();
        Set<Class<?>> visiting = new ObjectOpenHashSet<>();

        for (Class<?> node : nodes) {
            if (!visited.contains(node)) {
                topologicalSortUtil(node, graph, visited, visiting, result);
            }
        }

        Collections.reverse(result); // Reverse the result to obtain the correct dependency order.
        return result;
    }

    /**
     * Recursive utility method to perform depth-first search for topological sorting of a dependency graph.
     *
     * @param current The current node being processed in the graph.
     * @param graph The dependency graph where keys are nodes and values are the nodes they depend on.
     * @param visited A set tracking nodes that have already been fully processed.
     * @param visiting A set tracking nodes currently being visited, used to detect cycles in the graph.
     * @param result A list where the nodes are appended in topologically sorted order.
     * @throws IllegalStateException If a cyclic dependency is detected in the graph.
     */
    private static void topologicalSortUtil(Class<?> current, Map<Class<?>, Set<Class<?>>> graph,
                                            Set<Class<?>> visited, Set<Class<?>> visiting,
                                            List<Class<?>> result) {
        visiting.add(current);

        Set<Class<?>> dependencies = graph.getOrDefault(current, Collections.emptySet());
        for (Class<?> dependency : dependencies) {
            if (visiting.contains(dependency))
                throw new IllegalStateException("Found a loop dependency between " + current + " and " + dependency);

            if (!visited.contains(dependency)) {
                topologicalSortUtil(dependency, graph, visited, visiting, result);
            }
        }

        visiting.remove(current);
        visited.add(current);
        result.add(current);
    }


    private static void processCommentAnnotations(Field field, String key) {
        var configuration = ConfigUtils.getConfiguration();
        if (field.getAnnotation(Comment.class) != null) {
            configuration.setComments(key, List.of(field.getAnnotation(Comment.class).value()));
        } else if (field.getAnnotation(Comments.class) != null) {
            configuration.setComments(key, List.of(field.getAnnotation(Comments.class).value()));
        } else if (field.getAnnotation(InlineComment.class) != null) {
            configuration.setInlineComments(key, List.of(field.getAnnotation(InlineComment.class).value()));
        } else if (field.getAnnotation(InlineComments.class) != null) {
            configuration.setInlineComments(key, List.of(field.getAnnotation(InlineComments.class).value()));
        }
    }

    private static void invokeStaticMethod(Class<?> clazz, MethodType type) {
        try {
            Method method = type == MethodType.READ ? readActionMethodMap.get(clazz) : writeActionMethodMap.get(clazz);
            if (method == null || !Modifier.isStatic(method.getModifiers())) return;

            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception ignored) {
        }
    }

    public enum MethodType {
        READ, WRITE
    }
}