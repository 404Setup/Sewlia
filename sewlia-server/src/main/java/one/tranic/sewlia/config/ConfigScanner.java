package one.tranic.sewlia.config;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import one.tranic.sewlia.config.annotation.*;
import one.tranic.sewlia.reflect.Reflect;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class ConfigScanner {
    public static @Nullable Map<Class<?>, String> getClasss() {
        Map<Class<?>, String> builder = new Object2ObjectOpenHashMap<>();
        @Nullable Set<Class<?>> classes = Reflect.scanPackage("one.tranic.sewlia.config.mod");
        if (classes == null || classes.isEmpty()) return null;
        for (Class<?> clazz : classes) {
            builder.put(clazz, generateKey(clazz.getName()));
        }
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
            if (Modifier.isStatic(valueField.getModifiers())) {
                valueField.setAccessible(true);
                Object value = valueField.get(null);
                switch (value) {
                    case Boolean ignored -> ConfigUtils.getConfiguration().addDefault(key, value);
                    case String ignored -> ConfigUtils.getConfiguration().addDefault(key, value);
                    case Integer ignored -> ConfigUtils.getConfiguration().addDefault(key, value);
                    case Double ignored -> ConfigUtils.getConfiguration().addDefault(key, value);
                    case List ignored -> ConfigUtils.getConfiguration().addDefault(key, value);
                    case null, default -> {
                        return;
                    }
                }
                if (valueField.getAnnotation(Comment.class) != null) {
                    ConfigUtils.getConfiguration().setComments(key, List.of(valueField.getAnnotation(Comment.class).value()));
                } else if (valueField.getAnnotation(Comments.class) != null) {
                    ConfigUtils.getConfiguration().setComments(key, List.of(valueField.getAnnotation(Comments.class).value()));
                } else if (valueField.getAnnotation(InlineComment.class) != null) {
                    ConfigUtils.getConfiguration().setInlineComments(key, List.of(valueField.getAnnotation(InlineComment.class).value()));
                } else if (valueField.getAnnotation(InlineComments.class) != null) {
                    ConfigUtils.getConfiguration().setInlineComments(key, List.of(valueField.getAnnotation(InlineComments.class).value()));
                }
                Method wd = clazz.getMethod("WriteDo");
                if (!Modifier.isStatic(wd.getModifiers())) return;
                wd.setAccessible(true);
                wd.invoke(null);
            }
        } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
    }
    public static void processStaticValueFieldWithRead(Class<?> clazz, String key, boolean isReload) {
        try {
            Field valueField = clazz.getDeclaredField("value");
            if (Modifier.isStatic(valueField.getModifiers())) {
                valueField.setAccessible(true);
                Object value = valueField.get(null);
                if (isReload && valueField.getAnnotation(DisableReload.class) != null) return;
                switch (value) {
                    case Boolean ignored -> valueField.set(null, ConfigUtils.getConfiguration().getBoolean(key));
                    case String ignored -> valueField.set(null, ConfigUtils.getConfiguration().getString(key));
                    case Integer ignored -> valueField.set(null, ConfigUtils.getConfiguration().getInt(key));
                    case Double ignored -> valueField.set(null, ConfigUtils.getConfiguration().getDouble(key));
                    case List ignored -> {
                        List<?> target = ConfigUtils.getConfiguration().getList(key);
                        if (target == null || target.isEmpty()) return;
                        valueField.set(null, new ObjectArrayList<>(target));
                    }
                    case null, default -> {
                    }
                }
                Method dd = clazz.getMethod("ReadDo");
                if (!Modifier.isStatic(dd.getModifiers())) return;
                dd.setAccessible(true);
                dd.invoke(null);
            }
        } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
    }
}