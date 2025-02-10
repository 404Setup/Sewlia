package one.tranic.sewlia.config.mod.optimization.map;

import one.tranic.sewlia.config.annotation.Comment;

public class SkipUpdateIfNoRenderer {
    @Comment("Don't update maps if they don't have the CraftMapRenderer in the render list.")
    public static boolean value = false;
}