package one.tranic.sewlia.config.mod.misc.cache_player_profile;

import one.tranic.sewlia.annotation.config.Comments;
import one.tranic.sewlia.annotation.loader.DisableReload;

public class Enable {
    @DisableReload
    @Comments({"Cache the player profile result on they first join.",
            "It's useful if Mojang's verification server is down."})
    public static boolean value = false;
}