package one.tranic.sewlia.config.mod.feature.secure_seed;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class SecureRandom {
    @Comment("Whether to enable the secure RNG generator in secure seed operations, at the expense of some performance.")
    @DisableReload
    public static boolean value = true;
}