package me.frep.vulcan.spigot.command;

import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends BaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!(sender instanceof Player player)) {
            this.sendMessage(sender, VulcanSettings.messagesCantExecuteFromConsole);
            return true;
        } else if (player.hasPermission("vulcan.alerts")) {
            PluginServices.INSTANCE.getAlertManager().toggleAlerts(player);
            return true;
        } else {
            this.sendMessage(sender, VulcanSettings.messagesNoPermission);
            return true;
        }
    }
}
