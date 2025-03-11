package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.config.annotation.Comments;

public class Enabled {
    @Comments({"Use faster random generator?",
            "Requires a JVM that supports RandomGenerator.",
            "Some JREs don't support this."})
    public static boolean value = false;
}
