package one.tranic.sewlia.config.mod.feature.maven;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class Enable {
    @DisableReload
    @Comment("Maven Central Repository Acceleration")
    public static boolean value = true;
}