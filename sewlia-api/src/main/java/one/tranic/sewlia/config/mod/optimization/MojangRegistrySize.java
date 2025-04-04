package one.tranic.sewlia.config.mod.optimization;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class MojangRegistrySize {
    @Comments({"Fixes an issue causing registration of blocks/items to slow down proportional ","to the number already registered. This improves startup time."})
    @DisableReload
    public static boolean value = false;
}
