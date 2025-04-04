package one.tranic.sewlia.config.mod.optimization;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class CompactBitStorage {
    @Comments({"Fixes memory waste caused by some legacy servers (e.g. Hypixel) sending empty chunks",
            " as if they contain blocks. Reduces memory usage significantly on these servers."})
    @DisableReload
    public static boolean value = false;
}
