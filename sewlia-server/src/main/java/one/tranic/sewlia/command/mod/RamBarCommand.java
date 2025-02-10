package one.tranic.sewlia.command.mod;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import one.tranic.sewlia.task.RamBarTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class RamBarCommand extends Command {
    public RamBarCommand() {
        super("rambar");
        this.setPermission("sewlia.command.rambar.self");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Only players can use this command", NamedTextColor.RED));
                return true;
            }
            args = new String[]{sender.getName()};
        } else {
            if (!sender.hasPermission("sewlia.command.rambar.another")) {
                sender.sendMessage(Component.text("You do not have permission to switch other people's display!", NamedTextColor.RED));
                return true;
            }
        }
        for (String target : args) {
            @Nullable Player player = Bukkit.getPlayer(target);
            boolean self = (sender instanceof Player) && player != null && player.getUniqueId() == ((Player) sender).getUniqueId();
            Component output = player != null ? updateTarget(player) : Component.text("Player " + target + " not found");
            sender.sendMessage(output);
            if (player != null && !self) player.sendMessage(output);
        }
        return true;
    }

    private Component updateTarget(Player player) {
        boolean result = RamBarTask.instance().togglePlayer(player);
        ((CraftPlayer) player).getHandle().ramBar(result);

        return MiniMessage.miniMessage().deserialize("<green>RamBar toggled <onoff> for <target>",
                Placeholder.component("onoff", Component.translatable(result ? "options.on" : "options.off")
                        .color(result ? NamedTextColor.GREEN : NamedTextColor.RED)),
                Placeholder.parsed("target", player.getName()));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1 || !sender.hasPermission("sewlia.command.rambar.another")) return List.of();
        List<String> completion = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        return StringUtil.copyPartialMatches(args[0], completion, new ObjectArrayList<>(completion.size()));
    }
}