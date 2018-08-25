package ng.precious.LimitedEffects;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandReload implements CommandExecutor {
    private Main plugin;

    CommandReload(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equals("reload")) {
            if (sender.isOp() || sender instanceof ConsoleCommandSender || sender.hasPermission("limitedeffects.reload")) {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "LimitedEffects reloaded successfully!" + ChatColor.RESET);
                return true;
            }
        }

        return false;
    }
}
