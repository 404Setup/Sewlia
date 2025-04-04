package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class RandomGenerator {
    @Comments({"Which random generator will be used?",
            "See https://openjdk.org/jeps/356"})
    @DisableReload
    public static String value = "Xoroshiro128PlusPlus";
}
