package one.tranic.sewlia.config.mod.optimization;

import one.tranic.sewlia.annotation.config.Comments;

public class InactiveGoalSelectorThrottle {
    @Comments({"Throttles the AI goal selector in entity inactive ticks.",
            "This can improve performance by a few percent, but has minor gameplay implications."})
    public static boolean value = false;
}