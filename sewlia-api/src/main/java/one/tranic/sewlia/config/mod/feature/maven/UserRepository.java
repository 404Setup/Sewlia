package one.tranic.sewlia.config.mod.feature.maven;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class UserRepository {
    @DisableReload
    @Comment("Users add a central repository mirror")
    public static String value = "";
}