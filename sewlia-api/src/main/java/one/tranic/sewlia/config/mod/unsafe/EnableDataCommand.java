package one.tranic.sewlia.config.mod.unsafe;

import one.tranic.sewlia.config.annotation.DisableReload;
import one.tranic.sewlia.config.annotation.UnsafeMods;

public class EnableDataCommand {
    @DisableReload
    @UnsafeMods
    public static boolean value = false;
}
