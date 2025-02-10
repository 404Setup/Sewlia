package one.tranic.sewlia.command;

import one.tranic.sewlia.reflect.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public class CommandUtil {
    public static void register() {
        try {
            @Nullable Set<Command> set = Reflect.findAllClass("one.tranic.sewlia.command.mod", Command.class);
            if (set == null || set.isEmpty()) return;
            for (Command clazz : set) {
                Bukkit.getCommandMap().register(clazz.getName(), "minecraft", clazz);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}