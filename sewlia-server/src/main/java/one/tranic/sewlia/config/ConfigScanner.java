package one.tranic.sewlia.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import one.tranic.sewlia.config.annotation.*;
import one.tranic.sewlia.reflect.Reflect;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigScanner {
    private static final Logger logger = LoggerFactory.getLogger("ConfigScanner");
    private static final String CONFIG_PACKAGE = "one.tranic.sewlia.config.mod";
    private static final String VALUE_FIELD = "value";
    private static final String WRITE_METHOD = "WriteDo";
    private static final String READ_METHOD = "ReadDo";
    private static final String PACKAGE_PREFIX = "^one\\.tranic\\.sewlia\\.config\\.mod\\.";

    public static @Nullable Map<Class<?>, String> getClassConfigurations() {
        Set<Class<?>> classes = Reflect.scanPackage(CONFIG_PACKAGE);
        if (classes == null || classes.isEmpty()) {
            return null;
        }

        Map<Class<?>, String> configMap = new Object2ObjectOpenHashMap<>();
        for (Class<?> clazz : classes) {
            configMap.put(clazz, generateKey(clazz.getName()));
        }
        return configMap;
    }

    public static String generateKey(String fullClassName) {
        String cleanedName = fullClassName.replaceFirst(PACKAGE_PREFIX, "");
        cleanedName = cleanedName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        cleanedName = cleanedName.replaceAll("_", "-");
        return cleanedName;
    }


    public static void processStaticValueFieldWithWrite(Class<?> clazz, String key) {
        try {
            Field valueField = getStaticValueField(clazz);
            if (valueField == null) return;

            Object value = valueField.get(null);
            if (!writeConfigValue(key, value)) return;

            processFieldAnnotations(valueField, key);
            invokeStaticMethod(clazz, WRITE_METHOD);
        } catch (Exception e) {
            logger.error("Error processing write field for class: {}", clazz.getName(), e);
        }
    }

    public static void processStaticValueFieldWithRead(Class<?> clazz, String key, boolean isReload) {
        try {
            Field valueField = getStaticValueField(clazz);
            if (valueField == null) return;

            if (isReload && valueField.isAnnotationPresent(DisableReload.class)) {
                return;
            }

            Object currentValue = valueField.get(null);
            readConfigValue(valueField, key, currentValue);
            invokeStaticMethod(clazz, READ_METHOD);
        } catch (Exception e) {
            logger.error("Error processing read field for class: {}", clazz.getName(), e);
        }
    }

    private static Field getStaticValueField(Class<?> clazz) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(VALUE_FIELD);
        if (Modifier.isStatic(field.getModifiers())) {
            field.setAccessible(true);
            return field;
        }
        return null;
    }

    private static boolean writeConfigValue(String key, Object value) {
        return switch (value) {
            case Boolean v -> {
                ConfigUtils.getConfiguration().addDefault(key, v);
                yield true;
            }
            case String v -> {
                ConfigUtils.getConfiguration().addDefault(key, v);
                yield true;
            }
            case Integer v -> {
                ConfigUtils.getConfiguration().addDefault(key, v);
                yield true;
            }
            case Double v -> {
                ConfigUtils.getConfiguration().addDefault(key, v);
                yield true;
            }
            case List v -> {
                ConfigUtils.getConfiguration().addDefault(key, v);
                yield true;
            }
            default -> false;
        };
    }

    private static void readConfigValue(Field field, String key, Object currentValue) throws IllegalAccessException {
        switch (currentValue) {
            case Boolean ignored -> field.set(null, ConfigUtils.getConfiguration().getBoolean(key));
            case String ignored -> field.set(null, ConfigUtils.getConfiguration().getString(key));
            case Integer ignored -> field.set(null, ConfigUtils.getConfiguration().getInt(key));
            case Double ignored -> field.set(null, ConfigUtils.getConfiguration().getDouble(key));
            case List ignored -> {
                List<?> target = ConfigUtils.getConfiguration().getList(key);
                if (target != null && !target.isEmpty()) {
                    field.set(null, new ObjectArrayList<>(target));
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentValue);
        }
    }

    private static void processFieldAnnotations(Field field, String key) {
        if (field.isAnnotationPresent(Comment.class)) {
            ConfigUtils.getConfiguration().setComments(key, List.of(field.getAnnotation(Comment.class).value()));
        } else if (field.isAnnotationPresent(Comments.class)) {
            ConfigUtils.getConfiguration().setComments(key, List.of(field.getAnnotation(Comments.class).value()));
        } else if (field.isAnnotationPresent(InlineComment.class)) {
            ConfigUtils.getConfiguration().setInlineComments(key, List.of(field.getAnnotation(InlineComment.class).value()));
        } else if (field.isAnnotationPresent(InlineComments.class)) {
            ConfigUtils.getConfiguration().setInlineComments(key, List.of(field.getAnnotation(InlineComments.class).value()));
        }
    }

    private static void invokeStaticMethod(Class<?> clazz, String methodName) throws Exception {
        Method method = clazz.getMethod(methodName);
        if (Modifier.isStatic(method.getModifiers())) {
            method.setAccessible(true);
            method.invoke(null);
        }
    }
}