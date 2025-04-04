package one.tranic.sewlia.config.mod.feature.maven;

import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class Designation {
    @DisableReload
    @Comment("Manually specify a repository")
    public static String value = "";
}