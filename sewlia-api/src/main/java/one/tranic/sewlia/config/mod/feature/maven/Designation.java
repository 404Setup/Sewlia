package one.tranic.sewlia.config.mod.feature.maven;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class Designation {
    @DisableReload
    @Comment("Manually specify a repository")
    public static String value = "";
}