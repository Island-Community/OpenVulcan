package me.frep.vulcan.spigot.command;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class PunishmentLogsCommand extends BaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!sender.hasPermission("vulcan.punishlogs")) {
            sendMessage(sender, VulcanSettings.messagesNoPermission);
        } else if (arguments.length == 0) {
            sendMessage(sender, VulcanSettings.messagesPunishlogsSyntax);
        } else {
            String query = arguments[0];
            PluginServices.getScheduler().runTaskAsynchronously(() -> showPunishmentLogs(query, sender));
        }
        return true;
    }

    private void showPunishmentLogs(String playerName, CommandSender sender) {
        File file = new File(PluginServices.INSTANCE.getVulcanPlugin().getDataFolder(), "punishments.txt");
        if (!file.isFile()) {
            MainThreadExecutor.execute(() -> sendMessage(sender, VulcanSettings.messagesNoLogsFile));
            return;
        }
        List<String> matches = new ArrayList<>();
        String query = playerName.toLowerCase(Locale.ROOT);
        try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String normalized = line.toLowerCase(Locale.ROOT);
                if (normalized.contains(query) && normalized.contains("was punished")) {
                    matches.add(line.replace("-", "").replace("[", "").replace("]", ""));
                }
            }
            if (reader.ioException() != null)
                throw reader.ioException();
        } catch (IOException exception) {
            PluginServices.INSTANCE.getVulcanPlugin().getLogger().log(java.util.logging.Level.WARNING, "Cannot read punishment logs", exception);
            MainThreadExecutor.execute(() -> sendMessage(sender, VulcanSettings.messagesNoLogsFile));
            return;
        }
        Collections.reverse(matches);
        MainThreadExecutor.execute(() -> {
            if (matches.isEmpty()) {
                sendMessage(sender, VulcanSettings.messagesNoLogs);
                return;
            }
            String header = VulcanSettings.commandsPunishlogsHeaderFooter.replace("%player%", playerName);
            sendMessage(sender, header);
            for (int index = 0; index < matches.size(); index++) {
                sendMessage(sender, " &8- &7" + (index + 1) + ".)" + matches.get(index));
            }
            sendMessage(sender, header);
        });
    }
}
