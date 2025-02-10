package one.tranic.sewlia.config.mod.misc.cache_player_profile;

import one.tranic.sewlia.config.annotation.Comment;
import one.tranic.sewlia.config.annotation.DisableReload;

public class ResultTimeout {
    @DisableReload
    @Comment("The timeout of the cache. Unit: Minutes.")
    public static int value = 1440;
}