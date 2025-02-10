package one.tranic.sewlia.config.mod.optimization.projectile_loads;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class Max {
    @DisableReload
    @Comment("Controls how many chunks a projectile can load in its lifetime before it gets automatically removed.")
    public static int value = 10;
}