package one.tranic.sewlia.config.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.config.InlineComment;
import one.tranic.sewlia.annotation.config.InlineComments;
import one.tranic.sewlia.annotation.loader.ReadAction;
import one.tranic.sewlia.annotation.loader.WriteAction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {
    public static final org.slf4j.Logger logger = LoggerFactory.getLogger("SewliaConfig");

    /**
     * Processes the annotations on a field and assigns corresponding comments or inline comments
     * in a YamlConfiguration object based on the annotations present.
     *
     * @param field         The field being inspected for annotations.
     * @param key           The key in the configuration that the comments should be associated with.
     * @param configuration The YamlConfiguration object where the comments or inline comments will be set.
     */
    public static void processCommentAnnotations(Field field, String key, YamlConfiguration configuration) {
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

    /**
     * Finds and returns the first public static method in a given class that is annotated with a specific annotation.
     *
     * @param clazz           The class to search for the annotated static method.
     * @param annotationClass The annotation class to look for on the methods.
     * @return The first public static method annotated with the specified annotation, or null if no such method is found.
     */
    static Method findAnnotatedStaticMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
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
     * Caches methods annotated with {@code ReadAction} and {@code WriteAction} for a given class.
     * <p>
     * The method searches for public static methods in the specified class that are annotated
     * with {@code ReadAction} or {@code WriteAction}, and adds them to the respective maps in
     * the {@code ConfigAction} class for further invocation.
     *
     * @param clazz The class to search for annotated static methods.
     */
    static void cacheAnnotatedMethods(Class<?> clazz) {
        Method readMethod = ConfigUtils.findAnnotatedStaticMethod(clazz, ReadAction.class);
        if (readMethod != null) ConfigAction.readActionMethodMap.put(clazz, readMethod);

        Method writeMethod = ConfigUtils.findAnnotatedStaticMethod(clazz, WriteAction.class);
        if (writeMethod != null) ConfigAction.writeActionMethodMap.put(clazz, writeMethod);
    }

    /**
     * Performs topological sorting.
     *
     * @param graph The dependency graph where keys are nodes and values are the nodes they depend on.
     * @param nodes All nodes that need to be sorted.
     * @return A list of nodes in topologically sorted order.
     * @throws IllegalStateException If a cyclic dependency is detected in the graph.
     */
    public static List<Class<?>> topologicalSort(Map<Class<?>, Set<Class<?>>> graph, Set<Class<?>> nodes) {
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
     * @param current  The current node being processed in the graph.
     * @param graph    The dependency graph where keys are nodes and values are the nodes they depend on.
     * @param visited  A set tracking nodes that have already been fully processed.
     * @param visiting A set tracking nodes currently being visited, used to detect cycles in the graph.
     * @param result   A list where the nodes are appended in topologically sorted order.
     * @throws IllegalStateException If a cyclic dependency is detected in the graph.
     */
    public static void topologicalSortUtil(Class<?> current, Map<Class<?>, Set<Class<?>>> graph,
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

}
