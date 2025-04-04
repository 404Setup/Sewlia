package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class UseDirectImpl {
    @Comments({"Use direct random implementation instead of delegating to Java's RandomGenerator.",
            "This may improve performance but potentially changes RNG behavior."})
    @DisableReload
    public static boolean value = false;
}
