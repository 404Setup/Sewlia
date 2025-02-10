package one.tranic.sewlia.config.mod.feature;

import one.tranic.sewlia.config.annotation.Comments;
import one.tranic.sewlia.config.annotation.DisableReload;

public class TpsCatchup {
    @Comments({"If this setting is true, the server will run faster after a lag spike in",
            "an attempt to maintain 20 TPS. This option (defaults to true per",
            "spigot/paper) can cause mobs to move fast after a lag spike."})
    @DisableReload
    public static boolean value = false;
}