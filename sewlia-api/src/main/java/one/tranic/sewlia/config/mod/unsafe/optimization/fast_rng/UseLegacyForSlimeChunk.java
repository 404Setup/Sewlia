package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.config.annotation.Comment;

public class UseLegacyForSlimeChunk {
    @Comment("Use legacy random source for slime chunk generation, to follow vanilla behavior.")
    public static boolean value = false;
}
