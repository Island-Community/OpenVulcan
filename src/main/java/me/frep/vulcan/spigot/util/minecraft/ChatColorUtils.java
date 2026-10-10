package me.frep.vulcan.spigot.util.minecraft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import net.md_5.bungee.api.ChatColor;

public final class ChatColorUtils {

    public static String colorize(String message) {
        String colored = message.replace("%prefix%", VulcanSettings.prefix);
        if (ExemptionManager.isAtLeast1_16()) {
            Matcher matches = Pattern.compile("#[a-fA-F0-9]{6}").matcher(colored);
            StringBuffer result = new StringBuffer();
            while (matches.find()) matches.appendReplacement(result, Matcher.quoteReplacement(ChatColor.of(matches.group()).toString()));
            matches.appendTail(result);
            colored = result.toString();
        }
        return ChatColor.translateAlternateColorCodes('&', colored);
    }

    private ChatColorUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
