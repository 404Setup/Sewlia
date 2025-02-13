package one.tranic.sewlia.command;

import one.tranic.sewlia.reflect.NewReflect;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.io.IOException;
import java.util.Set;

public class CommandUtil {
    public static void register() {
        try {
            Set<Command> set = NewReflect.findAllClass("one.tranic.sewlia.command.mod", Command.class);
            if (set.isEmpty()) return;
            for (Command clazz : set) Bukkit.getCommandMap().register(clazz.getName(), "minecraft", clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}