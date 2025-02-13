package one.tranic.sewlia.reflect;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.Set;

public class NewReflect {

    private static final String INSTANCE_FIELD_NAME = "INSTANCE";

    @SuppressWarnings("unchecked")
    public static <T extends Class<?>> @Nullable Set<T> scanPackage(@Nullable T filterClass, @NotNull String packageName) {
        @Nullable Set<String> classNameSet = scanPackageString(filterClass, packageName);
        if (classNameSet == null || classNameSet.isEmpty()) return null;
        try {
            Set<T> result = new ObjectArraySet<>();
            for (String className : classNameSet) result.add((T) Class.forName(className));
            return result;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T extends Class<?>> @Nullable Set<T> scanPackage(@NotNull String packageName) {
        return scanPackage(null, packageName);
    }

    public static <T extends Class<?>> @Nullable Set<String> scanPackageString(@Nullable T filterClass, @NotNull String packageName) {
        try {
            ClassPath classPath = getClassPath();
            ImmutableSet<ClassPath.ClassInfo> classInfoSet = classPath.getTopLevelClassesRecursive(packageName);
            if (classInfoSet.isEmpty()) return null;
            Set<String> result = new ObjectArraySet<>();
            if (filterClass == null) {
                for (ClassPath.ClassInfo info : classInfoSet) result.add(info.getName());
            } else {
                for (ClassPath.ClassInfo info : classInfoSet)
                    if (info.getClass().isAssignableFrom(filterClass)) result.add(info.getName());
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static @Nullable Set<String> scanPackageString(@NotNull String packageName) {
        return scanPackageString(null, packageName);
    }

    public static <T> @NotNull Set<T> findObjectClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, true, false);
    }

    public static <T> @NotNull Set<T> findAllObjectClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, true, true);
    }

    public static <T> @NotNull Set<T> findClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, false, false);
    }

    public static <T> @NotNull Set<T> findAllClass(String packageName, Class<T> type) throws IOException {
        return findClass(packageName, type, false, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> @NotNull Set<T> findClass(String packageName, Class<T> type, boolean useSingletonInstance, boolean includeAllClasses) throws IOException {
        Set<T> resultSet = new ObjectArraySet<>();
        ClassPath classPath = getClassPath();
        Iterable<ClassPath.ClassInfo> classes = includeAllClasses
                ? classPath.getTopLevelClassesRecursive(packageName)
                : classPath.getTopLevelClasses(packageName);
        for (ClassPath.ClassInfo classInfo : classes) {
            try {
                Class<?> clazz = Class.forName(classInfo.getName());
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
        return resultSet;
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