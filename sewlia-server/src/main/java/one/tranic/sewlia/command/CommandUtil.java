package one.tranic.sewlia.command;

import one.tranic.sewlia.reflect.NewReflect;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CommandUtil {
    public static void register() {
        try {
            @NotNull Command[] commands = NewReflect.findAllClass("one.tranic.sewlia.command.mod", Command.class);
            if (commands.length < 1) return;
            for (int i = 0; i < commands.length; i++) {
                @NotNull Command command = commands[i];
                Bukkit.getCommandMap().register(command.getName(), "minecraft", command);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}