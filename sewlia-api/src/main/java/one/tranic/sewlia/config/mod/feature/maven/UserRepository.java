package one.tranic.sewlia.config.mod.feature.maven;

import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class UserRepository {
    @DisableReload
    @Comment("Users add a central repository mirror")
    public static String value = "";
}