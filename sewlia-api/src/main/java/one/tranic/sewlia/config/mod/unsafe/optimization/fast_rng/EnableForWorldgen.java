package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.After;
import one.tranic.sewlia.annotation.loader.DisableReload;
import one.tranic.sewlia.annotation.loader.ReadAction;
import one.tranic.sewlia.config.util.ConfigUtils;

import java.util.random.RandomGeneratorFactory;

@After(Enabled.class)
public class EnableForWorldgen {
    @Comments({"Enable faster random generator for world generation.",
            "WARNING: This will affect world generation!!!"})
    @DisableReload
    public static boolean value = false;

    public static boolean isEnabled() {
        return Enabled.value && value;
    }

    @ReadAction
    public static void read() {
        if (Enabled.value || EnableForWorldgen.value) {
            try {
                RandomGeneratorFactory.of(RandomGenerator.value);

                ConfigUtils.logger.info("Faster random generator is enabled for world generation.");
            } catch (Exception e) {
                ConfigUtils.logger.error("Faster random generator is enabled but {} is not supported by your JVM, " +
                        "falling back to legacy random source.", RandomGenerator.value);
                Enabled.value = false;
                EnableForWorldgen.value = false;
            }
        }
    }
}
