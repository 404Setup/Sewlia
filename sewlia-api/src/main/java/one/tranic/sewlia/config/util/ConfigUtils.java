package one.tranic.sewlia.config.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.config.InlineComment;
import one.tranic.sewlia.annotation.config.InlineComments;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {
    public static final org.slf4j.Logger logger = LoggerFactory.getLogger("SewliaConfig");

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
