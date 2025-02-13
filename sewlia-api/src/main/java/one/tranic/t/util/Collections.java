package one.tranic.t.util;

import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The Collections class provides utility methods for creating optimized data structures
 * such as maps, sets, and lists.
 */
public class Collections {
    /**
     * Creates a new hash map that maps keys of type {@code K} to integer values.
     *
     * @param <K> the type of keys maintained by this map
     * @return a new map that maps keys of type {@code K} to integers
     */
    public static <K> Map<K, Integer> newIntHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>();
    }

    /**
     * Creates a new hash map where keys are of type K and values are of type Integer.
     *
     * @param initialCapacity the initial capacity of the hash map. Must be a non-negative integer.
     * @return a new map instance with keys of type K and values of type Integer.
     */
    public static <K> Map<K, Integer> newIntHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates and returns a new map instance where the keys are of generic type K,
     * and the values are of type Long.
     * <p>
     *
     * @param <K> the type of keys maintained by the map
     * @return a newly created map with generic key type K and Long values
     */
    public static <K> Map<K, Long> newLongHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<>();
    }

    /**
     * Creates a new hash map with keys of type {@code K} and values of type {@code Long}.
     *
     * @param <K>             the type of keys to be used in the map
     * @param initialCapacity the initial capacity of the hash map; must be greater than or equal to 0
     * @return a new hash map instance with the specified initial capacity, either using a specialized
     * implementation or a standard {@code HashMap}
     */
    public static <K> Map<K, Long> newLongHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates a new map with keys of type {@code K} and values of type {@link Float}.
     *
     * @param <K> the type of keys in the map
     * @return a new map capable of storing keys of type {@code K} and values of type {@link Float}
     */
    public static <K> Map<K, Float> newFloatHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap<>();
    }

    /**
     * Creates a new map with keys of generic type {@code K} and {@code float} values.
     *
     * @param <K>             the type of keys maintained by the map
     * @param initialCapacity the initial capacity of the map; must be non-negative
     * @return a new map instance with the respective initial capacity
     */
    public static <K> Map<K, Float> newFloatHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates a new map with generic keys and double values.
     *
     * @param <K> the type of keys maintained by this map
     * @return a new map with keys of type K and values of type Double
     */
    public static <K> Map<K, Double> newDoubleHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap<>();
    }

    /**
     * Creates a new hash map with keys of type {@code K} and values of type {@code Double}.
     *
     * @param initialCapacity the initial capacity of the map. Must be a non-negative integer.
     * @return a new map of type {@code Map<K, Double>} with the specified initial capacity.
     */
    public static <K> Map<K, Double> newDoubleHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates and returns a new Map instance where the values are of type Boolean.
     *
     * @param <K> the type of the keys in the map
     * @return a new Map instance with Boolean values
     */
    public static <K> Map<K, Boolean> newBooleanHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap<>();
    }

    /**
     * Creates a new {@link Map} instance with keys of type {@code K} and boolean values.
     *
     * @param initialCapacity the initial capacity of the map; must be a non-negative integer.
     * @return a new {@link Map} instance with the specified initial capacity.
     */
    public static <K> Map<K, Boolean> newBooleanHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates and returns a new hash map with default settings.
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     * @return a new instance of a hash map
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>();
    }

    /**
     * Creates a new hash map with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the hash map; must be a non-negative integer
     * @return a new instance of a hash map with the given initial capacity
     */
    public static <K, V> Map<K, V> newHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates a new HashMap instance and populates it with the entries from the provided map.
     *
     * @param map the map whose entries are to be added to the newly created map; must not be null
     * @return a new map containing all entries from the provided map
     */
    public static <K, V> Map<K, V> newHashMap(@NotNull Map<K, V> map) {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(map);
    }

    /**
     * Creates and returns a new instance of a Map with predictable iteration order.
     *
     * @param <K> the type of keys the map will hold
     * @param <V> the type of values the map will hold
     * @return a new, empty map that preserves insertion order
     */
    public static <K, V> Map<K, V> newLinkedHashMap() {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>();
    }

    /**
     * Creates a new LinkedHashMap or a fastutil Object2ObjectLinkedOpenHashMap based on the specified map.
     *
     * @param map the input map whose entries are to be copied to the newly created map; must not be null
     * @return a new map containing the entries from the given map, maintaining insertion order
     */
    public static <K, V> Map<K, V> newLinkedHashMap(@NotNull Map<K, V> map) {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(map);
    }

    /**
     * Creates a new LinkedHashMap with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the map, must be non-negative and within the valid range.
     * @return a new instance of a map implementing LinkedHashMap with the specified initial capacity.
     */
    public static <K, V> Map<K, V> newLinkedHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(initialCapacity);
    }

    /**
     * Creates a new empty HashSet instance.
     *
     * @param <T> the type of elements maintained by the set.
     * @return a new empty {@code Set} instance.
     */
    public static <T> Set<T> newHashSet() {
        return new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>();
    }

    /**
     * Creates a new unmodifiable empty set.
     *
     * @param <T> the type of elements that the set can hold
     * @return a new unmodifiable empty set
     */
    public static <T> Set<T> newUnmodifiableHashSet() {
        return it.unimi.dsi.fastutil.objects.ObjectSet.of();
    }

    /**
     * Creates a new unmodifiable hash set containing the elements of the provided set.
     *
     * @param set the input set whose elements will be included in the new unmodifiable set; must not be null
     * @return an unmodifiable set containing the elements of the input set
     */
    public static <T> Set<T> newUnmodifiableHashSet(@NotNull Set<T> set) {
        return it.unimi.dsi.fastutil.objects.ObjectSet.of((T[]) set.toArray());
    }

    /**
     * Creates a new unmodifiable {@link Set} that contains the specified elements.
     *
     * @param elements the elements to be included in the set; cannot be null.
     * @return an unmodifiable set containing the specified elements.
     */
    @SafeVarargs
    public static <T> Set<T> newUnmodifiableHashSet(@NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) T... elements) {
        return it.unimi.dsi.fastutil.objects.ObjectSet.of(elements);
    }

    /**
     * Creates a new hash set with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the hash set; must be non-negative
     * @return a newly created hash set instance with the specified initial capacity
     */
    public static <T> Set<T> newHashSet(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(initialCapacity);
    }

    /**
     * Creates a new TreeSet with the elements provided in the specified collection.
     *
     * @param <T> the type of elements maintained by the set
     * @param c   the collection whose elements are to be placed into the new set
     * @return a newly created TreeSet containing the elements from the provided collection
     */
    public static <T> Set<T> newTreeSet(Collection<? extends T> c) {
        return new it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet<>(c);
    }

    /**
     * Creates a new TreeSet instance with the elements provided in the specified collection.
     *
     * @param <T> the type of elements maintained by the set
     * @param c   the collection whose elements are to be placed into the new set; must not be null
     * @return a newly created TreeSet containing all elements of the specified collection
     */
    public static <T> Set<T> newTreeSetRB(@NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) Collection<? extends T> c) {
        return new it.unimi.dsi.fastutil.objects.ObjectRBTreeSet<>(c);
    }

    /**
     * Creates a new empty list instance.
     *
     * @param <T> The type of elements that the list will hold.
     * @return A new instance of an empty {@link List}.
     */
    public static <T> List<T> newArrayList() {
        return new it.unimi.dsi.fastutil.objects.ObjectArrayList<>();
    }

    /**
     * Creates a new unmodifiable list.
     *
     * @param <T> the type of elements in the list
     * @return a new unmodifiable list instance
     */
    public static <T> List<T> newUnmodifiableList() {
        return it.unimi.dsi.fastutil.objects.ObjectList.of();
    }

    /**
     * Creates a new unmodifiable list from the provided list.
     *
     * @param list the list from which the unmodifiable list is to be created; must not be null
     * @return an unmodifiable list containing the same elements as the provided list
     */
    public static <T> List<T> newUnmodifiableList(@NotNull List<T> list) {
        return it.unimi.dsi.fastutil.objects.ObjectList.of((T[]) list.toArray());
    }

    /**
     * Creates a new unmodifiable list containing the specified elements.
     *
     * @param <T>      the type of elements in the list
     * @param elements the elements to include in the unmodifiable list; must not be null
     * @return an unmodifiable list containing the provided elements
     */
    @SafeVarargs
    public static <T> List<T> newUnmodifiableList(@NotNull T... elements) {
        return it.unimi.dsi.fastutil.objects.ObjectList.of(elements);
    }

    /**
     * Creates a new {@link List} instance with the specified initial size.
     *
     * @param <T>  The type of elements the list will contain.
     * @param size The initial size of the list. Must be greater than or equal to 0.
     * @return A new list instance with the specified size.
     */
    public static <T> List<T> newArrayList(@Range(from = 0, to = Integer.MAX_VALUE) int size) {
        return new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(size);
    }

    /**
     * Creates a new array-backed {@link List} containing the provided elements.
     *
     * @param <T>      the type of elements in the list
     * @param elements the elements to include in the new list; must not be null
     * @return a new {@link List} containing the specified elements
     */
    @SafeVarargs
    public static <T> List<T> newArrayList(@NotNull T... elements) {
        return new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(elements);
    }

    /**
     * Creates a new list containing the elements from the specified collection.
     *
     * @param elements the collection whose elements are to be placed into the new list, must not be null
     * @return a new list containing the elements from the specified collection
     */
    public static <T> List<T> newArrayList(@NotNull Collection<? extends T> elements) {
        return new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(elements);
    }

    /**
     * Iterates over each entry in the provided map and applies the given consumer action to each entry.
     *
     * @param <K>      the type of keys maintained by the map
     * @param <V>      the type of mapped values
     * @param map      the map whose entries are to be processed
     * @param consumer the action to be performed for each map entry
     */
    public static <K, V> void entryForEach(Map<K, V> map, final Consumer<? super Map.Entry<K, V>> consumer) {
        if (map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<K, V> fastMap) {
            fastMap.object2ObjectEntrySet().fastForEach(consumer);
        } else map.entrySet().forEach(consumer);
    }

    /**
     * Removes all entries from the specified map that satisfy the provided predicate.
     *
     * @param map    the map from which entries are to be removed based on the given predicate
     * @param filter the predicate that tests each entry; entries that satisfy this predicate are removed
     * @return {@code true} if any entries were removed from the map, otherwise {@code false}
     */
    public static <K, V> boolean removeIf(Map<K, V> map, Predicate<? super Map.Entry<K, V>> filter) {
        return (map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<K, V> fastMap) ?
                fastMap.object2ObjectEntrySet().removeIf(filter) :
                map.entrySet().removeIf(filter);
    }
}
