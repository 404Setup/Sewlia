package one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng;

import one.tranic.sewlia.config.annotation.Comments;

public class EnableForWorldgen {
    @Comments({"Enable faster random generator for world generation.",
            "WARNING: This will affect world generation!!!"})
    public static boolean value = false;

    public static boolean isEnabled() {
        return Enabled.value && value;
    }
}
