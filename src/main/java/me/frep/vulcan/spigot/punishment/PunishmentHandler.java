package me.frep.vulcan.spigot.punishment;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import me.clip.placeholderapi.PlaceholderAPI;
import me.frep.vulcan.api.event.VulcanDiscordWebhookPunishEvent;
import me.frep.vulcan.api.event.VulcanPunishEvent;
import me.frep.vulcan.api.event.VulcanPunishmentLogCreateEvent;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.StatisticsFile;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.message.ViolationFormatter;
import me.frep.vulcan.spigot.message.format.MessageBuilder;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.task.PunishmentTask;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishmentHandler {

    public void punish(AbstractCheck abstractCheck, PlayerData playerData) {
        Map checkPunishable2 = VulcanSettings.checkPunishable;
        if (((Boolean) checkPunishable2.get(abstractCheck.getCheckKey())) && (playerData.getPlayer() != null)) {
            if (System.currentTimeMillis() - playerData.getLastPunishmentAtMillis() < VulcanSettings.settingsPunishmentDelay) {
                return;
            }
            playerData.clearPunishmentCooldown();
            Player player2 = playerData.getPlayer();
            StringBuilder stringBuilder = new StringBuilder().append("vulcan.bypass.");
            if (player2.hasPermission(stringBuilder.append(abstractCheck.getCheckKey().toLowerCase()).toString())) {
                return;
            }
            if (VulcanSettings.settingsEnableApi) {
                VulcanPunishEvent vulcanPunishEvent = new VulcanPunishEvent(playerData.getPlayer(), abstractCheck);
                Bukkit.getPluginManager().callEvent(vulcanPunishEvent);
                if (vulcanPunishEvent.isCancelled()) {
                    return;
                }
            }
            if (VulcanSettings.punishmentFileEnabled) {
                ViolationFormatter.writePunishmentLog(abstractCheck, playerData);
                if (VulcanSettings.settingsEnableApi) {
                    VulcanPunishmentLogCreateEvent vulcanPunishmentLogCreateEvent = new VulcanPunishmentLogCreateEvent(playerData.getPlayer());
                    Bukkit.getPluginManager().callEvent(vulcanPunishmentLogCreateEvent);
                }
            }
            int index = ThreadLocalRandom.current().nextInt(50000);
            if (VulcanSettings.discordWebhookPunishmentsEnabled && !VulcanSettings.discordWebhookPunishmentsUrl.equalsIgnoreCase("insert-url-here")) {
                VulcanDiscordWebhookPunishEvent vulcanDiscordWebhookPunishEvent = new VulcanDiscordWebhookPunishEvent(playerData.getPlayer());
                if (!vulcanDiscordWebhookPunishEvent.isCancelled()) {
                    PluginServices.INSTANCE.getPunishmentExecutor().execute(() -> PunishmentHandler.sendPunishmentWebhook(playerData, abstractCheck));
                }
            }
            StatisticsFile.setAndSave("punishments", StatisticsFile.getPunishmentCount() + 1);
            if (!VulcanSettings.punishmentsMessage.equalsIgnoreCase("")) {
                if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                    AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
                    Player player3 = playerData.getPlayer();
                    String text = VulcanSettings.punishmentsMessage.replaceAll("%prefix%", VulcanSettings.prefix).replaceAll("%punishment-id", Integer.toString(index));
                    text = text.replace("%player%", playerData.getPlayerName());
                    text = text.replaceAll("%uuid%", playerData.getPlayer().getUniqueId().toString()).replaceAll("%server-name%", VulcanSettings.settingsServerName);
                    text = text.replaceAll("%category%", ViolationFormatter.capitalize(abstractCheck.getCategory().toLowerCase()));
                    text = text.replaceAll("%complex-type%", abstractCheck.getCheck().complexType());
                    text = text.replaceAll("%description%", abstractCheck.getCheck().description());
                    text = text.replaceAll("%check%", abstractCheck.getCheck().name());
                    RelativeMoveTracker relativeMoveTracker = playerData.getRelativeMoveTracker();
                    String text2;
                    if (relativeMoveTracker.getPlayer() == null) {
                        text2 = "None";
                    } else {
                        relativeMoveTracker = playerData.getRelativeMoveTracker();
                        text2 = relativeMoveTracker.getPlayer().getName();
                    }
                    text = text.replaceAll("%opponennt%", text2);
                    text = text.replaceAll("%client-version%", playerData.getUser().getClientVersion().getReleaseName()).replaceAll("%client-brand%", playerData.getClientBrand());
                    alertManager.broadcastAlert(PlaceholderAPI.setPlaceholders(player3, text.replaceAll("%type%", Character.toString(abstractCheck.getCheck().type())).replaceAll("%vl%", Integer.toString(abstractCheck.getVl()))));
                } else {
                    AlertManager alertManager2 = PluginServices.INSTANCE.getAlertManager();
                    String text3 = VulcanSettings.punishmentsMessage.replaceAll("%prefix%", VulcanSettings.prefix).replaceAll("%server-name%", VulcanSettings.settingsServerName).replaceAll("%punishment-id", Integer.toString(index));
                    String text4 = text3.replace("%player%", playerData.getPlayerName());
                    String text5 = text4.replaceAll("%uuid%", playerData.getPlayer().getUniqueId().toString());
                    String text6 = text5.replaceAll("%category%", ViolationFormatter.capitalize(abstractCheck.getCategory().toLowerCase()));
                    String text7 = text6.replaceAll("%complex-type%", abstractCheck.getCheck().complexType());
                    String text8 = text7.replaceAll("%description%", abstractCheck.getCheck().description());
                    RelativeMoveTracker relativeMoveTracker2 = playerData.getRelativeMoveTracker();
                    String text9;
                    if (relativeMoveTracker2.getPlayer() == null) {
                        text9 = "None";
                    } else {
                        relativeMoveTracker2 = playerData.getRelativeMoveTracker();
                        text9 = relativeMoveTracker2.getPlayer().getName();
                    }
                    String text10 = text8.replaceAll("%opponent%", text9);
                    String text11 = text10.replaceAll("%check%", abstractCheck.getCheck().name());
                    String text12 = text11.replaceAll("%client-version%", playerData.getUser().getClientVersion().getReleaseName()).replaceAll("%client-brand%", playerData.getClientBrand());
                    alertManager2.broadcastAlert(text12.replaceAll("%type%", Character.toString(abstractCheck.getCheck().type())).replaceAll("%vl%", Integer.toString(abstractCheck.getVl())));
                }
            }
            checkPunishable2 = VulcanSettings.broadcastPunishments;
            boolean condition = (Boolean) checkPunishable2.get(abstractCheck.getCheckKey());
            if (condition) {
                VulcanSettings.punishmentsBroadcast.forEach(line -> PunishmentHandler.broadcastPunishmentMessage(playerData, abstractCheck, index, line));
            }
            checkPunishable2 = VulcanSettings.punishmentCommands;
            List list = (List) checkPunishable2.get(abstractCheck.getCheckKey());
            if (!list.isEmpty()) {
                PunishmentTask punishmentTask = new PunishmentTask(this, list, playerData, index, abstractCheck);
                punishmentTask.runTask(PluginServices.INSTANCE.getVulcanPlugin());
            }
            playerData.getChecks().forEach(PunishmentHandler::resetCheckViolations);
            return;
        }
    }

    private void forwardPunishmentToBungee(Player player2, String text) {
        if (VulcanSettings.settingsPluginMessaging) {
            ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
            byteArrayDataOutput.writeUTF("punishment");
            byteArrayDataOutput.writeUTF(text);
            player2.sendPluginMessage(PluginServices.INSTANCE.getVulcanPlugin(), "vulcan:bungee", byteArrayDataOutput.toByteArray());
        }
    }

    public static void forwardPunishment(PunishmentHandler punishmentHandler, Player player2, String text) {
        punishmentHandler.forwardPunishmentToBungee(player2, text);
    }

    private static void resetCheckViolations(AbstractCheck abstractCheck) {
        abstractCheck.setVl(0);
    }

    private static void broadcastPunishmentMessage(PlayerData playerData, AbstractCheck abstractCheck, int index, String text) {
        String text2 = text.replace("%player%", playerData.getPlayerName());
        text2 = text2.replaceAll("%client-version%", playerData.getUser().getClientVersion().getReleaseName()).replaceAll("%client-brand%", playerData.getClientBrand());
        text2 = text2.replaceAll("%check%", abstractCheck.getCheck().name()).replaceAll("%server-name%", VulcanSettings.settingsServerName);
        text2 = text2.replaceAll("%uuid%", playerData.getPlayer().getUniqueId().toString());
        RelativeMoveTracker relativeMoveTracker = playerData.getRelativeMoveTracker();
        String text3;
        if (relativeMoveTracker.getPlayer() == null) {
            text3 = "None";
        } else {
            relativeMoveTracker = playerData.getRelativeMoveTracker();
            text3 = relativeMoveTracker.getPlayer().getName();
        }
        text2 = text2.replaceAll("%opponent%", text3);
        text2 = text2.replaceAll("%complex-type%", abstractCheck.getCheck().complexType());
        text2 = text2.replaceAll("%category%", ViolationFormatter.capitalize(abstractCheck.getCategory().toLowerCase())).replaceAll("%punishment-id", Integer.toString(index));
        text2 = text2.replaceAll("%description%", abstractCheck.getCheck().description());
        ExemptionManager.broadcast(text2.replaceAll("%type%", Character.toString(abstractCheck.getCheck().type())));
    }

    private static void sendPunishmentWebhook(PlayerData data, AbstractCheck check) {
        if (VulcanSettings.discordWebhookCooldown && PluginServices.INSTANCE.getPunishmentsInWindow() >= VulcanSettings.discordWebhookCooldownMaxWebhooks)
            return;
        CollisionState movement = data.getCollisionState();
        String world = data.getPlayer().getWorld().getName();
        String ping = Integer.toString(data.getConnectionTracker().getKeepAlivePing());
        String tps = MathUtils.formatNumber(ExemptionManager.getTps());
        Player opponent = data.getRelativeMoveTracker().getPlayer();
        String opponentName = opponent == null ? "None" : opponent.getName();
        java.util.function.UnaryOperator<String> format = template -> {
            String message = template.replace("%player%", data.getPlayerName()).replace("%name%", data.getPlayerName()).replace("%uuid%", data.getUniqueId().toString()).replace("%check%", check.getCheck().name()).replace("%max-vl%", Integer.toString(check.getMaxVl())).replace("%vl%", Integer.toString(check.getVl())).replace("%server-name%", VulcanSettings.settingsServerName).replace("%client-brand%", data.getClientBrand()).replace("%client-version%", data.getUser().getClientVersion().getReleaseName()).replace("%x%", MathUtils.formatNumber(movement.getX())).replace("%y%", MathUtils.formatNumber(movement.getY())).replace("%z%", MathUtils.formatNumber(movement.getZ())).replace("%world%", world).replace("%cps%", Double.toString(data.getClickTimingData().getClicksPerSecond())).replace("%tps%", tps).replace("%ping%", ping).replace("%total-violations%", Integer.toString(data.getTotalViolations())).replace("%complex-type%", check.getCheck().complexType()).replace("%description%", check.getDescription()).replace("%category%", ViolationFormatter.capitalize(check.getCategory().toLowerCase(java.util.Locale.ROOT))).replace("%version%", data.getUser().getClientVersion().getReleaseName()).replace("%opponent%", opponentName).replace("%opponennt%", opponentName).replace("%dev%", check.getCheck().experimental() ? ChatColorUtils.colorize(VulcanSettings.alertsExperimentalSymbol) : "").replace("%type%", Character.toString(check.getCheck().type()));
            return PluginServices.INSTANCE.isPlaceholderApiEnabled() ? PlaceholderAPI.setPlaceholders(data.getPlayer(), message) : message;
        };
        DiscordWebhook webhook = new DiscordWebhook(VulcanSettings.discordWebhookPunishmentsUrl);
        webhook.setAvatarUrl(VulcanSettings.discordWebhookPunishmentsAvatar);
        webhook.setUsername(VulcanSettings.discordWebhookPunishmentsUsername);
        MessageBuilder embed = new MessageBuilder().setTitle(format.apply(VulcanSettings.discordWebhookPunishmentsTitle)).setThumbnail(format.apply(VulcanSettings.discordWebhookPunishmentsThumbnail)).setDescription(format.apply(VulcanSettings.discordWebhookPunishmentsDescription)).setColor(new Color(VulcanSettings.discordWebhookPunishmentsColorR, VulcanSettings.discordWebhookPunishmentsColorG, VulcanSettings.discordWebhookPunishmentsColorB));
        if (VulcanSettings.discordWebhookPunishmentsDescriptionField)
            embed.addField("Description", check.getDescription(), true);
        if (VulcanSettings.discordWebhookPunishmentsServerNameField)
            embed.addField("Server Name", VulcanSettings.settingsServerName, true);
        if (VulcanSettings.discordWebhookPunishmentsClientBrandField)
            embed.addField("Client Brand", data.getClientBrand(), true);
        if (VulcanSettings.discordWebhookPunishmentsClientVersionField)
            embed.addField("Client Version", data.getUser().getClientVersion().getReleaseName(), true);
        if (VulcanSettings.discordWebhookPunishmentsPingTpsField)
            embed.addField("Ping | TPS", ping + "ms | " + tps, true);
        if (VulcanSettings.discordWebhookPunishmentsLocationField)
            embed.addField("Location", "(" + world + ", " + movement.getBlockX() + ", " + movement.getBlockY() + ", " + movement.getBlockZ() + ")", true);
        webhook.addEmbed(embed);
        if (!VulcanSettings.discordWebhookPunishmentsContent.isEmpty())
            webhook.setContent(format.apply(VulcanSettings.discordWebhookPunishmentsContent));
        webhook.send();
        if (VulcanSettings.discordWebhookCooldown)
            PluginServices.INSTANCE.setPunishmentsInWindow(PluginServices.INSTANCE.getPunishmentsInWindow() + 1);
    }
}
