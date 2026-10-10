package me.frep.vulcan.spigot.command;

import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import org.bukkit.command.CommandSender;

public class BaseCommand {

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColorUtils.colorize(message));
    }

    public void sendSeparator(CommandSender sender) {
        sendMessage(sender, "&7&m---»--*-------------------------------------*--«---");
    }
}
