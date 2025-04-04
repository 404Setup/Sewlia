package one.tranic.sewlia.config.mod.misc.cache_player_profile;

import one.tranic.sewlia.annotation.config.Comment;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class ResultTimeout {
    @DisableReload
    @Comment("The timeout of the cache. Unit: Minutes.")
    public static int value = 1440;
}