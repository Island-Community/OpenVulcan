package me.frep.vulcan.spigot.command;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import me.frep.vulcan.spigot.config.VulcanSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class VulcanTabCompleter implements TabCompleter {

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] arguments) {
        if ((arguments.length != 1)) {
            return arguments.length == 2 && arguments[0].equalsIgnoreCase("disablecheck") ? StringUtil.copyPartialMatches(arguments[0], VulcanSettings.checkEnabled.keySet(), new ArrayList<String>()) : null;
        }
        ArrayList<String> subcommands = new ArrayList<>();
        subcommands.add("help");
        subcommands.add("profile");
        subcommands.add("ban");
        subcommands.add("reload");
        subcommands.add("disablecheck");
        subcommands.add("violations");
        subcommands.add("cps");
        subcommands.add("kb");
        subcommands.add("reset");
        subcommands.add("knockback");
        subcommands.add("connection");
        subcommands.add("freeze");
        subcommands.add("rotate");
        subcommands.add("shuffle");
        subcommands.add("top");
        return StringUtil.copyPartialMatches(arguments[0], subcommands, new ArrayList<String>());
    }
}
