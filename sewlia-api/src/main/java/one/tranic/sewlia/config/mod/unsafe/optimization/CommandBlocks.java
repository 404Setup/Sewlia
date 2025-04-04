package one.tranic.sewlia.config.mod.unsafe.optimization;

import one.tranic.sewlia.annotation.config.Comments;

public class CommandBlocks {
    @Comments({"Optimizes command block executions by caching parsed commands.",
            "Command parsing is a relatively expensive operation. By caching it we avoid",
            "parsing the same command every time it is executed."
    })
    public static boolean value = false;
}
