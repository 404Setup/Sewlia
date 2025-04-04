package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.After;
import one.tranic.sewlia.annotation.loader.DisableReload;

@After(RandomGenerator.class)
public class Enabled {
    @Comments({"Use faster random generator?",
            "Requires a JVM that supports RandomGenerator.",
            "Some JREs don't support this."})
    @DisableReload
    public static boolean value = false;
}
