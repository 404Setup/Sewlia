package one.tranic.sewlia.config.mod.fix;

import one.tranic.sewlia.annotation.config.Comments;

public class POIAccessOffRegion {
    @Comments({"The POIManager of folia has something which has not been patched",
            "for regionized ticking and these would trigger the async catcher",
            "and make the server crash.If you would like to prevent it and didn't",
            "mind the side effect(currently unknown), you can enable this"})
    public static boolean value = false;
}
