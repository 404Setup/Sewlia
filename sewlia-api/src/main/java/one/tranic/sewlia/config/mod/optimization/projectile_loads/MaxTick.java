package one.tranic.sewlia.config.mod.optimization.projectile_loads;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class MaxTick {
    @Comment("Controls how many chunks are allowed to be sync loaded by projectiles in a tick.")
    @DisableReload
    public static int value = 10;
}