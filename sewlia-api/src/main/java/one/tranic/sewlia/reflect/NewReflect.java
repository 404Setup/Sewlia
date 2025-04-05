package one.tranic.sewlia.reflect;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Set;

@SuppressWarnings("unused")
public class NewReflect {
    private static final String INSTANCE_FIELD_NAME = "INSTANCE";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @SuppressWarnings("unchecked")
    public static <T extends Class<?>> @Nullable T[] scanPackage(@Nullable T filterClass, @NotNull String packageName) {
        @Nullable String[] classNameArrays = scanPackageString(filterClass, packageName);
        if (classNameArrays == null || classNameArrays.length < 1) return null;
        try {
            T[] result = (T[]) new Class<?>[classNameArrays.length];
            for (int i = 0; i < classNameArrays.length; i++) result[i] = (T) Class.forName(classNameArrays[i]);
            return result;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T extends Class<?>> @Nullable T[] scanPackage(@NotNull String packageName) {
        return scanPackage(null, packageName);
    }

    @SuppressWarnings("all")
    public static <T extends Class<?>> @NotNull String[] scanPackageString(@Nullable T filterClass, @NotNull String packageName) {
        try {
            ClassPath classPath = getClassPath();
            ImmutableSet<ClassPath.ClassInfo> classInfoSet = classPath.getTopLevelClassesRecursive(packageName);
            if (classInfoSet.isEmpty()) return EMPTY_STRING_ARRAY;

            String[] resultArray;
            if (filterClass == null) {
                resultArray = new String[classInfoSet.size()];
                int i = 0;
                for (ClassPath.ClassInfo info : classInfoSet) {
                    resultArray[i++] = info.getName();
                }
            } else {
                Set<String> result = new ObjectArraySet<>();
                for (ClassPath.ClassInfo info : classInfoSet) {
                    if (filterClass.isAssignableFrom(info.load()))
                        result.add(info.getName());
                }
                resultArray = (String[]) result.toArray();
            }

            return resultArray;
        } catch (Exception e) {
            return EMPTY_STRING_ARRAY;
        }
    }

    public static @Nullable String[] scanPackageString(@NotNull String packageName) {
        return scanPackageString(null, packageName);
    }

    public static <T> @NotNull T[] findObjectClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, true, false);
    }

    public static <T> @NotNull T[] findAllObjectClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, true, true);
    }

    public static <T> @NotNull T[] findClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, false, false);
    }

    public static <T> @NotNull T[] findAllClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, false, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> @NotNull T[] findClass(String packageName, Class<T> type, boolean useSingletonInstance, boolean includeAllClasses) throws IOException {
        int estimatedSize = 16;
        Set<T> resultSet = new ObjectArraySet<>(estimatedSize);

        ClassPath classPath = getClassPath();
        Iterable<ClassPath.ClassInfo> classes = includeAllClasses
                ? classPath.getTopLevelClassesRecursive(packageName)
                : classPath.getTopLevelClasses(packageName);
        for (ClassPath.ClassInfo classInfo : classes) {
            try {
                Class<?> clazz = classInfo.load();

                if (!type.isAssignableFrom(clazz)) continue;

                T instance = null;
                if (useSingletonInstance) {
                    instance = getObjectInstance(clazz);
                } else if (!clazz.isAnnotation() && !clazz.isEnum() && !clazz.isInterface()) {
                    instance = (T) clazz.getDeclaredConstructor().newInstance();
                }

                if (instance != null) resultSet.add(instance);
            } catch (Exception ignored) {
            }
        }

        T[] array = (T[]) Array.newInstance(type, resultSet.size());
        return resultSet.toArray(array);

    }

    @SuppressWarnings("unchecked")
    private static <T> T getObjectInstance(Class<?> clazz) {
        try {
            return (T) clazz.getField(INSTANCE_FIELD_NAME).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static ClassPath getClassPath() throws IOException {
        return ClassPath.from(Thread.currentThread().getContextClassLoader());
    }
}