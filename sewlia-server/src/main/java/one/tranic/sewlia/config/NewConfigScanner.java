package one.tranic.sewlia.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import one.tranic.sewlia.config.annotation.*;
import one.tranic.sewlia.reflect.NewReflect;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewConfigScanner {

    public static @Nullable Map<Class<?>, String> getClasses() {
        Map<Class<?>, String> builder = new Object2ObjectOpenHashMap<>();
        @Nullable Set<Class<?>> classes = NewReflect.scanPackage("one.tranic.sewlia.config.mod");
        if (classes == null || classes.isEmpty()) return null;
        for (Class<?> clazz : classes) builder.put(clazz, generateKey(clazz.getName()));
        return builder;
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

            invokeStaticMethod(clazz, "WriteDo");
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

            invokeStaticMethod(clazz, "ReadDo");
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
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

    private static void invokeStaticMethod(Class<?> clazz, String methodName) {
        try {
            Method method = clazz.getMethod(methodName);
            if (!Modifier.isStatic(method.getModifiers())) {
                return;
            }
            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception ignored) {
        }
    }
}