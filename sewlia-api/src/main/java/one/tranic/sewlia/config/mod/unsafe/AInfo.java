package one.tranic.sewlia.config.mod.unsafe;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class AInfo {
    @DisableReload
    @Comment("Everything in Unsafe is extremely unstable and can easily cause the server to crash.")
    public static boolean value = false;
}
