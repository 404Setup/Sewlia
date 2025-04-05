package one.tranic.sewlia.config.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

class ConfigAction {
    static final Map<Class<?>, Method> readActionMethodMap = new Object2ObjectOpenHashMap<>();
    static final Map<Class<?>, Method> writeActionMethodMap = new Object2ObjectOpenHashMap<>();

    static void invokeStaticMethod(Class<?> clazz, MethodType type) {
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
