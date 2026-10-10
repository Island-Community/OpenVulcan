package me.frep.vulcan.spigot.task;

import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.message.ViolationFormatter;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.punishment.PunishmentHandler;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.util.scheduling.ScheduledRunnable;
import org.bukkit.entity.Player;

public class PunishmentTask extends ScheduledRunnable {

    public final List<String> commands;

    public final PlayerData playerData;

    public final int violationCount;

    public final AbstractCheck check;

    public final PunishmentHandler punishmentHandler;

    public PunishmentTask(PunishmentHandler punishmentHandler, List<String> list, PlayerData playerData, int count2, AbstractCheck abstractCheck) {
        this.commands = list;
        this.playerData = playerData;
        this.violationCount = count2;
        this.check = abstractCheck;
        this.punishmentHandler = punishmentHandler;
    }

    @Override
    public void run() {
        for (String command : commands) executePunishmentCommand(playerData, violationCount, check, command);
    }

    private void executePunishmentCommand(PlayerData data, int punishmentId, AbstractCheck check, String template) {
        Player opponent = data.getRelativeMoveTracker().getPlayer();
        String command = template.replace("%player%", data.getPlayerName()).replace("%client-version%", data.getUser().getClientVersion().getReleaseName()).replace("%client-brand%", data.getClientBrand()).replace("%ping%", Integer.toString(data.getConnectionTracker().getKeepAlivePing())).replace("%tps%", MathUtils.formatNumber(ExemptionManager.getTps())).replace("%server-name%", VulcanSettings.settingsServerName).replace("%opponent%", opponent == null ? "None" : opponent.getName()).replace("%opponennt%", opponent == null ? "None" : opponent.getName()).replace("%punishment-id%", Integer.toString(punishmentId)).replace("%punishment-id", Integer.toString(punishmentId)).replace("%category%", ViolationFormatter.capitalize(check.getCategory().toLowerCase(java.util.Locale.ROOT))).replace("%complex-type%", check.getCheck().complexType()).replace("%description%", check.getCheck().description()).replace("%check%", check.getCheck().name()).replace("%type%", Character.toString(check.getCheck().type()));
        if (PluginServices.INSTANCE.isPlaceholderApiEnabled())
            command = PlaceholderAPI.setPlaceholders(data.getPlayer(), command);
        command = ChatColorUtils.colorize(command);
        if (command.regionMatches(true, 0, "Bungee:", 0, 7)) {
            String message = command.substring(7) + "#VULCAN#" + check.getDisplayName() + "#VULCAN#" + check.getDisplayType() + "#VULCAN#" + check.getVl() + "#VULCAN#" + data.getPlayerName() + "#VULCAN#" + check.getMaxVl();
            PunishmentHandler.forwardPunishment(punishmentHandler, data.getPlayer(), message);
        } else
            ExemptionManager.dispatchConsoleCommand(command);
    }
}
