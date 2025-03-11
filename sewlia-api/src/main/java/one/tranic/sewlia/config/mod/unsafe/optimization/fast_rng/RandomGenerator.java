package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.config.annotation.Comments;

public class RandomGenerator {
    @Comments({"Which random generator will be used?",
            "See https://openjdk.org/jeps/356"})
    public static String value = "Xoroshiro128PlusPlus";
}
