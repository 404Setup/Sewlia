package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.config.util.ConfigUtils;
import one.tranic.sewlia.config.annotation.Comment;

public class WarnForSlimeChunk {
    @Comment("Warn if you are not using legacy random source for slime chunk generation.")
    public static boolean value = true;

    public static void ReadDo() {
        if (Enabled.value && value) {
            ConfigUtils.logger.warn("==========");
            ConfigUtils.logger.warn("You enabled faster random generator, it will offset location of slime chunk");
            ConfigUtils.logger.warn("If your server has slime farms or facilities need vanilla slime chunk,");
            ConfigUtils.logger.warn("set performance.faster-random-generator.use-legacy-random-for-slime-chunk " +
                    "to true to use LegacyRandomSource for slime chunk generation.");
            ConfigUtils.logger.warn("Set performance.faster-random-generator.warn-for-slime-chunk to false to " +
                    "disable this warning.");
            ConfigUtils.logger.warn("==========");
        }
    }
}
