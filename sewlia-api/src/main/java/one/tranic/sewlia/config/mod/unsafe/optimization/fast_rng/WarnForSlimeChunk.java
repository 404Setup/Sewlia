package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.annotation.loader.After;
import one.tranic.sewlia.annotation.loader.DisableReload;
import one.tranic.sewlia.annotation.loader.ReadAction;
import one.tranic.sewlia.config.util.ConfigUtils;
import one.tranic.sewlia.annotation.config.Comment;

@After(EnableForWorldgen.class)
public class WarnForSlimeChunk {
    @Comment("Warn if you are not using legacy random source for slime chunk generation.")
    @DisableReload
    public static boolean value = true;

    @ReadAction
    public static void read() {
        if (EnableForWorldgen.isEnabled() && value) {
            ConfigUtils.logger.warn("==========");
            ConfigUtils.logger.warn("You enabled faster random generator, it will offset location of slime chunk");
            ConfigUtils.logger.warn("If your server has slime farms or facilities need vanilla slime chunk,");
            ConfigUtils.logger.warn("set unsafe.faster-rng.use-legacy-random-for-slime-chunk to true to use LegacyRandomSource for slime chunk generation.");
            ConfigUtils.logger.warn("Set unsafe.faster-rng.warn-for-slime-chunk to false to disable this warning.");
            ConfigUtils.logger.warn("==========");
        }
    }
}
