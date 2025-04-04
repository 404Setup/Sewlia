package one.tranic.sewlia.annotation.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated configuration value will be loaded
 * after the specified configuration has been loaded. This allows
 * for the creation of a load order chain.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface After {
    Class<?> value();
}
