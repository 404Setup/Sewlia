package one.tranic.sewlia.config.mod.optimization;

import one.tranic.sewlia.annotation.config.Comments;

public class Suffocation {
    @Comments({"Optimizes the suffocation check by selectively skipping",
            "the check in a way that still appears vanilla. This should",
            "be left enabled on most servers, but is provided as a",
            "configuration option if the vanilla deviation is undesirable."})
    public static boolean value = false;
}