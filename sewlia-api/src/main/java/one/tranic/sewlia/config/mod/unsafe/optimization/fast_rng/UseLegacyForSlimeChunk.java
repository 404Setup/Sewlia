package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class UseLegacyForSlimeChunk {
    @Comment("Use legacy random source for slime chunk generation, to follow vanilla behavior.")
    @DisableReload
    public static boolean value = false;
}
