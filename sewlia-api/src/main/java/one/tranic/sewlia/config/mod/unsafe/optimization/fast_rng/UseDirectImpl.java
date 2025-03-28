package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.config.annotation.Comments;

public class UseDirectImpl {
    @Comments({"Use direct random implementation instead of delegating to Java's RandomGenerator.",
            "This may improve performance but potentially changes RNG behavior."})
    public static boolean value = false;
}
