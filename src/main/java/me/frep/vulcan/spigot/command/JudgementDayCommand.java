package me.frep.vulcan.spigot.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.config.YamlConfig;
import me.frep.vulcan.spigot.punishment.JudgementDay;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JudgementDayCommand extends BaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!sender.hasPermission("vulcan.jday")) {
            this.sendMessage(sender, VulcanSettings.messagesNoPermission);
            return true;
        } else if (arguments.length == 0) {
            this.sendMessage(sender, VulcanSettings.messagesJdayCommandSyntax);
            return true;
        } else if (arguments[0].equalsIgnoreCase("execute")) {
            if (JudgementDay.pendingCount() == 0) {
                this.sendMessage(sender, VulcanSettings.messagesJdayNoPendingBans);
                return true;
            } else {
                JudgementDay.execute();
                return true;
            }
        } else if (arguments[0].equalsIgnoreCase("add")) {
            if (arguments.length < 2) {
                this.sendMessage(sender, VulcanSettings.messagesJdayCommandSyntax);
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(arguments[1]);
            YamlConfig pendingConfig = new YamlConfig("jday-pending");
            StringBuilder reasonBuilder = new StringBuilder();
            for (int index = 2; index < arguments.length; index++) {
                reasonBuilder.append(arguments[index]).append(" ");
            }
            String reason = reasonBuilder.toString();
            if (!reason.isEmpty() && !reason.equals(" ")) {
                reason = reasonBuilder.toString();
            } else {
                reason = "No Reason Specified.";
            }
            int reasonLength = reason.length();
            reason = reason.substring(0, reasonLength - 1);
            pendingConfig.getFileConfiguration().set("PendingUsers." + player.getUniqueId() + ".Name", player.getName());
            pendingConfig.getFileConfiguration().set("PendingUsers." + player.getUniqueId() + ".UUID", player.getUniqueId().toString());
            pendingConfig.getFileConfiguration().set("PendingUsers." + player.getUniqueId() + ".Date", Calendar.getInstance().getTime().toString());
            pendingConfig.getFileConfiguration().set("PendingUsers." + player.getUniqueId() + ".Reason", reason);
            pendingConfig.getFileConfiguration().set("PendingUsers." + player.getUniqueId() + ".ExecutedBy", sender.getName());
            pendingConfig.save();
            String playerName = arguments[1];
            this.sendMessage(sender, VulcanSettings.messagesJdayAddedToList.replaceAll("%player%", playerName));
        } else if (arguments[0].equalsIgnoreCase("list")) {
            YamlConfig pendingConfig = new YamlConfig("jday-pending");
            if (pendingConfig.getFileConfiguration() == null) {
                this.sendMessage(sender, VulcanSettings.messagesJdayNoPendingBans);
                return true;
            }
            if (pendingConfig.getFileConfiguration().getConfigurationSection("PendingUsers") == null) {
                this.sendMessage(sender, VulcanSettings.messagesJdayNoPendingBans);
                return true;
            }
            Set<String> pendingUsers = pendingConfig.getFileConfiguration().getConfigurationSection("PendingUsers").getKeys(false);
            if (pendingUsers.isEmpty()) {
                this.sendMessage(sender, VulcanSettings.messagesJdayNoPendingBans);
                return true;
            }
            ArrayList<String> playerNames = new ArrayList<>();
            for (String pendingUser : pendingUsers) {
                String playerName = pendingConfig.getFileConfiguration().getString("PendingUsers." + pendingUser + ".Name");
                playerNames.add(playerName);
            }
            String headerFooter = VulcanSettings.commandsJdayListHeaderFooter;
            this.sendMessage(sender, headerFooter);
            for (String playerName : playerNames) {
                this.sendMessage(sender, VulcanSettings.commandsJdayListFormat.replaceAll("%name%", playerName));
            }
            headerFooter = VulcanSettings.commandsJdayListHeaderFooter;
            this.sendMessage(sender, headerFooter);
        } else {
            if (!arguments[0].equalsIgnoreCase("remove")) {
                this.sendMessage(sender, VulcanSettings.messagesJdayCommandSyntax);
                return true;
            }
            if (arguments.length == 1) {
                this.sendMessage(sender, VulcanSettings.messagesJdayRemoveSyntax);
                return true;
            }
            String playerName = arguments[1];
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            UUID uuid = player.getUniqueId();
            YamlConfig pendingConfig = new YamlConfig("jday-pending");
            if (pendingConfig.getFileConfiguration() == null) {
                this.sendMessage(sender, VulcanSettings.messagesJdayNoPendingBans);
                return true;
            }
            if (pendingConfig.getFileConfiguration().getConfigurationSection("PendingUsers") == null) {
                this.sendMessage(sender, VulcanSettings.messagesJdayNoPendingBans);
                return true;
            }
            Set<String> pendingUsers = pendingConfig.getFileConfiguration().getConfigurationSection("PendingUsers").getKeys(false);
            String pendingUser = uuid.toString();
            if (!pendingUsers.contains(pendingUser)) {
                this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                return true;
            }
            pendingConfig.getFileConfiguration().set("PendingUsers." + pendingUser + ".Name", null);
            pendingConfig.getFileConfiguration().set("PendingUsers." + pendingUser + ".UUID", null);
            pendingConfig.getFileConfiguration().set("PendingUsers." + pendingUser + ".Reason", null);
            pendingConfig.getFileConfiguration().set("PendingUsers." + pendingUser + ".ExecutedBy", null);
            pendingConfig.getFileConfiguration().set("PendingUsers." + pendingUser + ".wasOnline", null);
            pendingConfig.getFileConfiguration().set("PendingUsers." + pendingUser + ".Date", null);
            pendingConfig.getFileConfiguration().getConfigurationSection("PendingUsers").set(pendingUser, null);
            pendingConfig.save();
            this.sendMessage(sender, VulcanSettings.messagesRemovedFromJday.replaceAll("%player%", playerName));
        }
        return true;
    }
}
