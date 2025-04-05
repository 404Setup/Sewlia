package one.tranic.sewlia.command.mod;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.sewlia.config.ConfigUtils;
import one.tranic.sewlia.plugin.InternalServerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class SewliaCommand extends Command {

    public SewliaCommand() {
        super("sewlia");
        this.setUsage("/sewlia [reload]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        boolean isConsole = (!(sender instanceof Player)) || sender.isOp();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (isConsole || sender.hasPermission("sewlia.command.reload")) {
                    Bukkit.getGlobalRegionScheduler().runDelayed(InternalServerPlugin.instance, (task) -> {
                        ConfigUtils.reloadConfiguration(true);
                        sender.sendMessage(Component.text(
                                "Configuration reload has been completed. Some configurations require a restart to take effect.",
                                NamedTextColor.AQUA
                        ));
                    }, 1);
                } else {
                    sender.sendMessage(Component.text(
                            "You do not have permission to use this command!",
                            NamedTextColor.RED
                    ));
                }
                return true;
            }
        }
        sender.sendMessage(Component.text("Usage: " + this.getUsage(), NamedTextColor.AQUA));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        ObjectArrayList<String> tabs = new ObjectArrayList<>();
        boolean isConsole = (!(sender instanceof Player)) || sender.isOp();
        if (args.length == 1) {
            if (isConsole || sender.hasPermission("sewlia.command.reload")) {
                tabs.add("reload"); // Reload the configuration file
            }
        }
        return tabs;
    }
}