package one.tranic.sewlia.config.mod.fix.entity_moving;

import one.tranic.sewlia.annotation.config.Comments;

public class Enable {
    @Comments({"A simple fix of a issue on folia.",
            "Some times the entity would have a large moment that cross the different tick regions and it would",
            "make the server crashed)  but sometimes it might doesn't work."})
    public static boolean value = true;
}
