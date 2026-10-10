package me.frep.vulcan.spigot.command;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import me.clip.placeholderapi.PlaceholderAPI;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ViolationLogsCommand extends BaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (!sender.hasPermission("vulcan.logs")) {
            sendMessage(sender, VulcanSettings.messagesNoPermission);
            return true;
        }
        if (arguments.length < 1 || arguments.length > 2) {
            sendMessage(sender, VulcanSettings.messagesLogsCommandSyntax);
            return true;
        }
        int page;
        try {
            page = arguments.length == 1 ? 1 : Integer.parseInt(arguments[1]);
            if (page < 1)
                throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            sendMessage(sender, VulcanSettings.messagesLogsCommandSyntax);
            return true;
        }
        String playerName = arguments[0];
        PluginServices.getScheduler().runTaskAsynchronously(() -> readLogs(sender, playerName, page));
        return true;
    }

    private void readLogs(CommandSender sender, String playerName, int page) {
        File file = new File(PluginServices.INSTANCE.getVulcanPlugin().getDataFolder(), "violations.txt");
        List<String> matches = new ArrayList<>();
        try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8)) {
            String query = playerName.toLowerCase(Locale.ROOT);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.toLowerCase(Locale.ROOT).contains(query))
                    matches.add(line);
            }
            if (reader.ioException() != null)
                throw reader.ioException();
        } catch (IOException exception) {
            MainThreadExecutor.execute(() -> sendMessage(sender, VulcanSettings.messagesNoLogsFile));
            return;
        }
        Collections.reverse(matches);
        MainThreadExecutor.execute(() -> showPage(sender, playerName, page, matches));
    }

    private void showPage(CommandSender sender, String playerName, int page, List<String> matches) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (matches.isEmpty()) {
            sendFormatted(sender, player, VulcanSettings.messagesLogsCommandNoLogs.replace("%player%", playerName));
            return;
        }
        int pageCount = (matches.size() + 9) / 10;
        if (page > pageCount) {
            sendFormatted(sender, player, VulcanSettings.messagesNoPage.replace("%page%", Integer.toString(page)).replace("%player%", playerName));
            return;
        }
        String header = VulcanSettings.commandsLogsHeaderFooter.replace("%player%", playerName).replace("%page%", Integer.toString(page)).replace("%max-pages%", Integer.toString(pageCount));
        sendFormatted(sender, player, header);
        int start = (page - 1) * 10;
        for (int start2 = start; start2 < Math.min(start + 10, matches.size()); start2++) {
            if (start2 > start)
                sendMessage(sender, "&r");
            sendMessage(sender, VulcanSettings.commandsLogsColor + matches.get(start2));
        }
        sendFormatted(sender, player, header);
    }

    private void sendFormatted(CommandSender sender, Player player, String message) {
        if (PluginServices.INSTANCE.isPlaceholderApiEnabled() && player != null)
            message = PlaceholderAPI.setPlaceholders(player, message);
        sendMessage(sender, message);
    }
}
