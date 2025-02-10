package one.tranic.sewlia.config.mod.fix;

import one.tranic.sewlia.config.annotation.Comments;

public class SpectorTeleportation {
    @Comments({"The teleportation of spector players would call absMoveTo directly.",
            "And when the camera teleported to another region,this would call absMoveTo",
            "to let the spector player move to another region without any checks, which ",
            "would trigger the async catcher and crash the server"})
    public static boolean value = false;
}
