package one.tranic.sewlia.config.mod.optimization;

import one.tranic.sewlia.config.annotation.Comments;
import one.tranic.sewlia.config.annotation.DisableReload;
import one.tranic.sewlia.config.annotation.UnsafeMods;

public class MojangRegistrySize {
    @Comments({"Fixes an issue causing registration of blocks/items to slow down proportional ","to the number already registered. This improves startup time."})
    @DisableReload
    @UnsafeMods
    public static boolean value = false;
}
