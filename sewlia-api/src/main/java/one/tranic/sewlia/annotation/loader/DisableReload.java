package one.tranic.sewlia.annotation.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated configuration value will not be refreshed
 * when the configuration file is reloaded.
 * <p>
 * This does not affect the initial loading of the configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisableReload {
}