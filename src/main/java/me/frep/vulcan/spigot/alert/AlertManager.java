package me.frep.vulcan.spigot.alert;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.clip.placeholderapi.PlaceholderAPI;
import me.frep.vulcan.api.event.VulcanDisableAlertsEvent;
import me.frep.vulcan.api.event.VulcanEnableAlertsEvent;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import me.frep.vulcan.api.event.VulcanPostFlagEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.message.ViolationFormatter;
import me.frep.vulcan.spigot.message.format.MessageBuilder;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.punishment.JudgementDay;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.webhook.DiscordWebhook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class AlertManager {

    private final Set<org.bukkit.entity.Player> alertStaff = new HashSet();

    private final Set<org.bukkit.entity.Player> verboseStaff = new HashSet();

    private final DecimalFormat decimalFormat = new DecimalFormat("##.##");

    public void toggleAlerts(Player player) {
        if (this.alertStaff.contains(player)) {
            this.alertStaff.remove(player);
            player.sendMessage(ChatColorUtils.colorize(VulcanSettings.alertsToggledOffMessage));
            if (VulcanSettings.settingsEnableApi) {
                VulcanDisableAlertsEvent event = new VulcanDisableAlertsEvent(player);
                if (ExemptionManager.isNewerThan1_13()) {
                    MainThreadExecutor.execute(() -> AlertManager.fireDisableAlertsEvent(event));
                } else {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        } else {
            this.alertStaff.add(player);
            player.sendMessage(ChatColorUtils.colorize(VulcanSettings.alertsToggledOnMessage));
            if (VulcanSettings.settingsEnableApi) {
                VulcanEnableAlertsEvent event = new VulcanEnableAlertsEvent(player);
                if (ExemptionManager.isNewerThan1_13()) {
                    MainThreadExecutor.execute(() -> AlertManager.fireEnableAlertsEvent(event));
                } else {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }
    }

    public void toggleVerbose(Player player) {
        if (this.verboseStaff.contains(player)) {
            this.verboseStaff.remove(player);
            player.sendMessage(ChatColorUtils.colorize(VulcanSettings.verboseToggledOffMessage));
        } else {
            this.verboseStaff.add(player);
            player.sendMessage(ChatColorUtils.colorize(VulcanSettings.verboseToggledOnMessage));
        }
    }

    public void processViolation(AbstractCheck check, PlayerData data, String info) {
        if (!((Boolean) VulcanSettings.checkEnabled.get(check.getCheckKey())).booleanValue()) {
            return;
        }
        if (System.currentTimeMillis() - data.getLastPunishmentAtMillis() < (long) VulcanSettings.settingsPunishmentDelay) {
            return;
        }
        if (data.getPlayer() != null && !data.getPlayer().isOnline()) {
            return;
        }
        if (VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isLegacyFloodgateEnabled() && org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(data.getPlayer().getUniqueId())) {
            return;
        }
        if (VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isFloodgateEnabled() && FloodgateApi.getInstance().isFloodgatePlayer(data.getPlayer().getUniqueId())) {
            return;
        }
        if (ExemptionManager.getTps() < (Double) VulcanSettings.minimumTps.get(check.getCheckKey())) {
            return;
        }
        Player pingPlayer = data.getPlayer();
        if (CheckUtils.getPing(pingPlayer) > (Integer) VulcanSettings.maximumPing.get(check.getCheckKey())) {
            return;
        }
        if (VulcanSettings.settingsIgnoreVivecraft && data.getClientBrand().toLowerCase().contains("vivecraft")) {
            return;
        }
        if (data.getPlayer().hasPermission("vulcan.bypass." + check.getCheckKey().toLowerCase())) {
            return;
        }
        if (VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isLegacyFloodgateEnabled() && org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(data.getPlayer().getUniqueId())) {
            return;
        }
        if (VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isFloodgateEnabled() && FloodgateApi.getInstance().isFloodgatePlayer(data.getPlayer().getUniqueId())) {
            return;
        }
        if (VulcanSettings.settingsIgnoreFlagsGeyserClientBrand && data.getClientBrand().toLowerCase().contains("geyser")) {
            return;
        }
        if (VulcanSettings.settingsIgnoreGeyserUuids && data.getPlayer().getUniqueId().toString().startsWith("00000")) {
            return;
        }
        if (VulcanSettings.settingsIgnoreGeyserPrefixes && data.getPlayerName().startsWith(VulcanSettings.settingsIgnoreGeyserPrefix)) {
            return;
        }
        if (VulcanSettings.settingsEnableApi) {
            VulcanFlagEvent flagEvent = new VulcanFlagEvent(data.getPlayer(), check, info);
            Bukkit.getPluginManager().callEvent(flagEvent);
            if (flagEvent.isCancelled()) {
                return;
            }
        }
        String cancelHitsCheck = (check.getName() + check.getType()).toLowerCase().replace(" ", "");
        if (VulcanSettings.cancelHitsEnabledForAllChecks || VulcanSettings.cancelHitsChecks.contains(cancelHitsCheck)) {
            data.clearCancelHitsCooldown();
        }
        check.setVl(check.getVl() + 1);
        int violations = check.getVl();
        if (!check.isExperimental() && this.isCountedInvalidCheck(check)) {
            if (!check.getName().equalsIgnoreCase("timer")) {
                data.setTotalViolations(data.getTotalViolations() + 1);
            }
            if (check.getName().equalsIgnoreCase("timer")) {
                data.setTimerViolations(data.getTimerViolations() + 1);
            }
            if (check.getName().equalsIgnoreCase("autoclicker")) {
                data.setAutoClickerViolations(data.getAutoClickerViolations() + 1);
            }
            if (check.getName().equalsIgnoreCase("scaffold")) {
                data.setScaffoldViolations(data.getScaffoldViolations() + 1);
            }
            switch(check.getCategory()) {
                case "combat":
                    {
                        if (check.getName().equalsIgnoreCase("autoclicker"))
                            break;
                        data.setCombatViolations(data.getCombatViolations() + 1);
                        break;
                    }
                case "movement":
                    {
                        data.setMovementViolations(data.getMovementViolations() + 1);
                        break;
                    }
                case "player":
                    {
                        if (check.getName().equalsIgnoreCase("timer"))
                            break;
                        data.setPlayerViolations(data.getPlayerViolations() + 1);
                    }
            }
        }
        int alertInterval = (Integer) VulcanSettings.alertIntervals.get(check.getCheckKey());
        int minimumAlertViolations = (Integer) VulcanSettings.minimumAlertViolations.get(check.getCheckKey());
        int totalViolations = data.getTotalViolations();
        int maxViolations = (Integer) VulcanSettings.maximumViolations.get(check.getCheckKey());
        if (violations % alertInterval == 0 && violations >= minimumAlertViolations) {
            String experimentalSymbol;
            if (VulcanSettings.logFileEnabled) {
                PluginServices.INSTANCE.getAlertExecutor().execute(() -> AlertManager.writeViolationLog(check, data, info));
            }
            String playerName = data.getPlayerName();
            String description = check.getCheck().description();
            String checkName = check.getCheck().name();
            double locationX = data.getCollisionState().getX();
            String x = MathUtils.formatNumber(locationX);
            double locationY = data.getCollisionState().getY();
            String y = MathUtils.formatNumber(locationY);
            double locationZ = data.getCollisionState().getZ();
            String z = MathUtils.formatNumber(locationZ);
            String world = data.getPlayer().getWorld().getName();
            Player alertPingPlayer = data.getPlayer();
            String ping = Integer.toString(CheckUtils.getPing(alertPingPlayer));
            double serverTps = ExemptionManager.getTps();
            String tps = MathUtils.formatNumber(serverTps);
            String violationsText = Integer.toString(check.getVl());
            String totalViolationsText = Integer.toString(data.getTotalViolations());
            String maxViolationsText = Integer.toString(maxViolations);
            String version = data.getClientVersion().getReleaseName();
            double clicksPerSecond = data.getClickTimingData().getClicksPerSecond();
            String cps = MathUtils.formatNumber(clicksPerSecond);
            if (check.getCheck().experimental()) {
                String experimentalSymbolSetting;
                experimentalSymbolSetting = VulcanSettings.alertsExperimentalSymbol;
                experimentalSymbol = ChatColorUtils.colorize(experimentalSymbolSetting);
            } else {
                experimentalSymbol = "";
            }
            int severity = 1;
            if (VulcanSettings.settingsPerCheckSeverities) {
                if (violations >= VulcanSettings.alertsSeverityViolations2) {
                    severity = 2;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations3) {
                    severity = 3;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations4) {
                    severity = 4;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations5) {
                    severity = 5;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations6) {
                    severity = 6;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations7) {
                    severity = 7;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations8) {
                    severity = 8;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations9) {
                    severity = 9;
                }
                if (violations >= VulcanSettings.alertsSeverityViolations10) {
                    severity = 10;
                }
            } else {
                if (totalViolations >= VulcanSettings.alertsSeverityViolations2) {
                    severity = 2;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations3) {
                    severity = 3;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations4) {
                    severity = 4;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations5) {
                    severity = 5;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations6) {
                    severity = 6;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations7) {
                    severity = 7;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations8) {
                    severity = 8;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations9) {
                    severity = 9;
                }
                if (totalViolations >= VulcanSettings.alertsSeverityViolations10) {
                    severity = 10;
                }
            }
            String bungeeMessage = check.getDisplayName() + "#VULCAN#" + check.getDisplayType() + "#VULCAN#" + violationsText + "#VULCAN#" + playerName + "#VULCAN#" + maxViolationsText + "#VULCAN#" + version + "#VULCAN#" + tps + "#VULCAN#" + ping + "#VULCAN#" + description + "#VULCAN#" + info + "#VULCAN#" + experimentalSymbol + "#VULCAN#" + this.getSeverityColor(severity);
            Player bungeePlayer = data.getPlayer();
            this.forwardAlertToBungee(bungeePlayer, bungeeMessage);
            if (VulcanSettings.alertsPrintToConsole) {
                if (!VulcanSettings.settingsIgnoreAlertsOverMaxViolations || violations <= maxViolations + VulcanSettings.settingsIgnoreAlertsOverMaxViolationsAmount) {
                    String consoleExperimentalSymbol;
                    String consoleOpponentName;
                    String consoleTemplate = VulcanSettings.alertsConsoleFormat;
                    String consoleCategory = check.getCategory().toLowerCase();
                    String consoleDetails = ChatColorUtils.colorize(consoleTemplate).replace("%player%", playerName).replaceAll("%max-vl%", maxViolationsText).replaceAll("%check%", checkName).replaceAll("%info%", info).replaceAll("%server-name%", VulcanSettings.settingsServerName).replaceAll("%cps%", cps).replaceAll("%category%", ViolationFormatter.capitalize(consoleCategory)).replaceAll("%complex-type%", check.getCheck().complexType()).replaceAll("%client-brand%", data.getClientBrand()).replaceAll("%x%", x).replaceAll("%y%", y).replaceAll("%uuid%", data.getPlayer().getUniqueId().toString());
                    if (data.getRelativeMoveTracker().getPlayer() == null) {
                        consoleOpponentName = "None";
                    } else {
                        consoleOpponentName = data.getRelativeMoveTracker().getPlayer().getName();
                    }
                    String consoleContext = consoleDetails.replaceAll("%opponennt%", consoleOpponentName).replaceAll("%z%", z).replaceAll("%world%", world).replaceAll("%total-violations%", totalViolationsText).replaceAll("%ping%", ping).replaceAll("%description%", description).replaceAll("%tps%", tps).replaceAll("%version%", version);
                    if (check.getCheck().experimental()) {
                        String consoleSymbolSetting = VulcanSettings.alertsExperimentalSymbol;
                        consoleExperimentalSymbol = ChatColorUtils.colorize(consoleSymbolSetting);
                    } else {
                        consoleExperimentalSymbol = "";
                    }
                    String consoleMessage = consoleContext.replaceAll("%dev%", consoleExperimentalSymbol).replaceAll("%vl%", violationsText).replaceAll("%type%", Character.toString(check.getCheck().type()));
                    ExemptionManager.logColored(consoleMessage);
                }
            }
            if (!this.alertStaff.isEmpty() && (!VulcanSettings.settingsIgnoreAlertsOverMaxViolations || violations <= maxViolations + VulcanSettings.settingsIgnoreAlertsOverMaxViolationsAmount)) {
                String hoverExperimentalSymbol;
                String hoverOpponentName;
                String alertExperimentalSymbol;
                String alertOpponentName;
                String alertFormat = ChatColorUtils.colorize(VulcanSettings.alertsFormat);
                if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                    alertFormat = PlaceholderAPI.setPlaceholders(data.getPlayer(), alertFormat);
                }
                String alertCategory = check.getCategory().toLowerCase();
                String alertDetails = ChatColorUtils.colorize(alertFormat).replace("%player%", playerName).replaceAll("%ping%", ping).replaceAll("%tps%", tps).replaceAll("%max-vl%", maxViolationsText).replaceAll("%severity%", this.getSeverityColor(severity)).replaceAll("%client-brand%", data.getClientBrand()).replaceAll("%info%", info).replaceAll("%cps%", cps).replaceAll("%server-name%", VulcanSettings.settingsServerName).replaceAll("%category%", ViolationFormatter.capitalize(alertCategory)).replaceAll("%complex-type%", check.getCheck().complexType()).replaceAll("%x%", x).replaceAll("%y%", y).replaceAll("%z%", z).replaceAll("%uuid%", data.getPlayer().getUniqueId().toString());
                if (data.getRelativeMoveTracker().getPlayer() == null) {
                    alertOpponentName = "None";
                } else {
                    alertOpponentName = data.getRelativeMoveTracker().getPlayer().getName();
                }
                String alertContext = alertDetails.replaceAll("%opponennt%", alertOpponentName).replaceAll("%world%", world).replaceAll("%total-violations%", totalViolationsText).replaceAll("%check%", check.getCheck().name()).replaceAll("%description%", check.getCheck().description()).replaceAll("%version%", version);
                if (check.getCheck().experimental()) {
                    String alertSymbolSetting = VulcanSettings.alertsExperimentalSymbol;
                    alertExperimentalSymbol = ChatColorUtils.colorize(alertSymbolSetting);
                } else {
                    alertExperimentalSymbol = "";
                }
                TextComponent alertComponent = new TextComponent(TextComponent.fromLegacyText(alertContext.replaceAll("%dev%", alertExperimentalSymbol).replaceAll("%vl%", violationsText).replaceAll("%type%", Character.toString(check.getCheck().type()))));
                for (String clickCommand : VulcanSettings.alertsClickCommands) {
                    alertComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand.replace("%player%", data.getPlayer().getName()).replaceAll("%uuid%", data.getPlayer().getUniqueId().toString())));
                }
                StringBuilder hoverBuilder = new StringBuilder();
                int hoverLineCount = VulcanSettings.alertsHoverMessage.size();
                int hoverLineNumber = 1;
                for (String hoverLine : VulcanSettings.alertsHoverMessage) {
                    if (hoverLineNumber == hoverLineCount) {
                        hoverBuilder.append(hoverLine);
                    } else {
                        hoverBuilder.append(hoverLine).append("\n");
                    }
                    ++hoverLineNumber;
                }
                String hoverTemplate = hoverBuilder.toString();
                String hoverFormat = ChatColorUtils.colorize(hoverTemplate);
                if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                    hoverFormat = PlaceholderAPI.setPlaceholders(data.getPlayer(), hoverFormat);
                }
                String hoverCategory = check.getCategory().toLowerCase();
                String hoverDetails = hoverFormat.replace("%player%", playerName).replaceAll("%check%", checkName).replaceAll("%max-vl%", maxViolationsText).replaceAll("%server-name%", VulcanSettings.settingsServerName).replaceAll("%client-brand%", data.getClientBrand()).replaceAll("%severity", this.getSeverityColor(severity)).replaceAll("%x%", x).replaceAll("%y%", y).replaceAll("%uuid%", data.getPlayer().getUniqueId().toString()).replaceAll("%category%", ViolationFormatter.capitalize(hoverCategory)).replaceAll("%z%", z).replaceAll("%world%", world).replaceAll("%vl%", violationsText).replaceAll("%combat-violations%", Integer.toString(data.getCombatViolations())).replaceAll("%movement-violations%", Integer.toString(data.getMovementViolations())).replaceAll("%player-violations%", Integer.toString(data.getPlayerViolations())).replaceAll("%cps%", cps).replaceAll("%complex-type%", check.getCheck().complexType()).replaceAll("%version%", version).replaceAll("%total-violations%", totalViolationsText).replaceAll("%ping%", ping);
                if (data.getRelativeMoveTracker().getPlayer() == null) {
                    hoverOpponentName = "None";
                } else {
                    hoverOpponentName = data.getRelativeMoveTracker().getPlayer().getName();
                }
                String hoverContext = hoverDetails.replaceAll("%opponennt%", hoverOpponentName).replaceAll("%description%", description);
                if (check.getCheck().experimental()) {
                    String hoverSymbolSetting = VulcanSettings.alertsExperimentalSymbol;
                    hoverExperimentalSymbol = ChatColorUtils.colorize(hoverSymbolSetting);
                } else {
                    hoverExperimentalSymbol = "";
                }
                String hoverMessage = hoverContext.replaceAll("%dev%", hoverExperimentalSymbol).replaceAll("%type%", Character.toString(check.getCheck().type())).replaceAll("%tps%", tps).replaceAll("%info%", info).replaceAll("%check%", check.getCheck().name());
                String hoverText = ChatColorUtils.colorize(hoverMessage);
                if (ExemptionManager.isAtLeast1_16()) {
                    alertComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText)));
                } else {
                    alertComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                }
                boolean sendAlert = true;
                if (VulcanSettings.settingsEnableApi) {
                    VulcanPostFlagEvent postFlagEvent = new VulcanPostFlagEvent(data.getPlayer(), check, info);
                    Bukkit.getPluginManager().callEvent(postFlagEvent);
                    sendAlert = !postFlagEvent.isCancelled();
                }
                if (sendAlert) {
                    if (VulcanSettings.settingsAsyncAlerts) {
                        this.alertStaff.forEach(recipient -> AlertManager.sendAlertComponent(alertComponent, recipient));
                    } else {
                        MainThreadExecutor.execute(() -> this.broadcastAlertComponent(alertComponent));
                    }
                }
            }
            if (!VulcanSettings.alertsCustomCommands.isEmpty()) {
                if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                    MainThreadExecutor.execute(() -> AlertManager.runPlaceholderAlertCommands(data, playerName, maxViolationsText, check, info, ping, x, y, z, cps, world, tps, version, violationsText));
                } else {
                    MainThreadExecutor.execute(() -> AlertManager.runAlertCommands(playerName, maxViolationsText, data, info, check, ping, x, y, z, cps, world, tps, version, violationsText));
                }
            }
            if (VulcanSettings.discordWebhookAlertsEnabled && !VulcanSettings.discordWebhookAlertsUrl.equalsIgnoreCase("insert-url-here")) {
                PluginServices.INSTANCE.getPunishmentExecutor().execute(() -> AlertManager.sendViolationWebhook(check, violations, data, playerName, checkName, maxViolationsText, x, y, z, world, violationsText, cps, version, totalViolationsText, ping, description, tps, info));
            }
        }
        if (!((Boolean) VulcanSettings.checkPunishable.get(check.getCheckKey())).booleanValue()) {
            return;
        }
        if (check.getCheck().experimental()) {
            return;
        }
        if (VulcanSettings.settingsIgnoreIfInJudgementDay && JudgementDay.isQueued(data.getPlayer())) {
            return;
        }
        if (violations >= maxViolations && data.getPlayer().isOnline()) {
            PluginServices.INSTANCE.getPunishmentHandler().punish(check, data);
        }
    }

    public void sendApiAlert(Player player, String checkName, String checkType, String info) {
        PlayerData data = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player);
        if (data == null || data.getPlayer() == null)
            return;
        if (VulcanSettings.settingsIgnoreFloodgate && (PluginServices.INSTANCE.isLegacyFloodgateEnabled() || PluginServices.INSTANCE.isFloodgateEnabled()) && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId()))
            return;
        String brand = data.getClientBrand() == null ? "Unresolved" : data.getClientBrand();
        if ((VulcanSettings.settingsIgnoreVivecraft && brand.toLowerCase(java.util.Locale.ROOT).contains("vivecraft")) || (VulcanSettings.settingsIgnoreFlagsGeyserClientBrand && brand.toLowerCase(java.util.Locale.ROOT).contains("geyser")))
            return;
        java.util.function.UnaryOperator<String> format = template -> formatApiMessage(template, data, checkName, checkType, info);
        if (VulcanSettings.alertsPrintToConsole)
            ExemptionManager.logColored(format.apply(VulcanSettings.alertsConsoleFormat));
        if (!alertStaff.isEmpty()) {
            String message = format.apply(VulcanSettings.alertsFormat);
            if (PluginServices.INSTANCE.isPlaceholderApiEnabled())
                message = PlaceholderAPI.setPlaceholders(player, message);
            TextComponent component = new TextComponent(TextComponent.fromLegacyText(message));
            for (String command : VulcanSettings.alertsClickCommands) {
                component.setClickEvent(new ClickEvent(Action.RUN_COMMAND, command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString())));
            }
            String hover = format.apply(String.join("\n", VulcanSettings.alertsHoverMessage));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
            Runnable send = () -> alertStaff.forEach(recipient -> recipient.spigot().sendMessage(component));
            if (VulcanSettings.settingsAsyncAlerts)
                send.run();
            else
                MainThreadExecutor.execute(send);
        }
        if (!VulcanSettings.alertsCustomCommands.isEmpty())
            MainThreadExecutor.execute(() -> {
                for (String command : VulcanSettings.alertsCustomCommands) {
                    String formatted = format.apply(command);
                    if (PluginServices.INSTANCE.isPlaceholderApiEnabled())
                        formatted = PlaceholderAPI.setPlaceholders(player, formatted);
                    ExemptionManager.dispatchConsoleCommand(formatted);
                }
            });
    }

    private static String formatApiMessage(String template, PlayerData data, String checkName, String checkType, String info) {
        Player opponent = data.getRelativeMoveTracker().getPlayer();
        String opponentName = opponent == null ? "None" : opponent.getName();
        CollisionState collision = data.getCollisionState();
        return ChatColorUtils.colorize(template).replace("%player%", data.getPlayerName()).replace("%uuid%", data.getPlayer().getUniqueId().toString()).replace("%client-brand%", data.getClientBrand() == null ? "Unresolved" : data.getClientBrand()).replace("%server-name%", VulcanSettings.settingsServerName).replace("%opponent%", opponentName).replace("%opponennt%", opponentName).replace("%check%", checkName).replace("%type%", checkType).replace("%info%", info).replace("%x%", Integer.toString(collision.getBlockX())).replace("%y%", Integer.toString(collision.getBlockY())).replace("%z%", Integer.toString(collision.getBlockZ())).replace("%world%", data.getPlayer().getWorld().getName()).replace("%cps%", MathUtils.formatNumber(data.getClickTimingData().getClicksPerSecond())).replace("%ping%", Integer.toString(CheckUtils.getPing(data.getPlayer()))).replace("%tps%", MathUtils.formatNumber(ExemptionManager.getTps())).replace("%version%", data.getClientVersion().getReleaseName()).replace("%total-violations%", Integer.toString(data.getTotalViolations())).replace("%combat-violations%", Integer.toString(data.getCombatViolations())).replace("%movement-violations%", Integer.toString(data.getMovementViolations())).replace("%player-violations%", Integer.toString(data.getPlayerViolations()));
    }

    private String getSeverityColor(int severity) {
        return ChatColorUtils.colorize(switch(severity) {
            case 1 ->
                VulcanSettings.alertsSeverityColors1;
            case 2 ->
                VulcanSettings.alertsSeverityColors2;
            case 3 ->
                VulcanSettings.alertsSeverityColors3;
            case 4 ->
                VulcanSettings.alertsSeverityColors4;
            case 5 ->
                VulcanSettings.alertsSeverityColors5;
            case 6 ->
                VulcanSettings.alertsSeverityColors6;
            case 7 ->
                VulcanSettings.alertsSeverityColors7;
            case 8 ->
                VulcanSettings.alertsSeverityColors8;
            case 9 ->
                VulcanSettings.alertsSeverityColors9;
            case 10 ->
                VulcanSettings.alertsSeverityColors10;
            default ->
                "";
        });
    }

    private boolean isCountedInvalidCheck(AbstractCheck check) {
        String checkKey = check.getName().toLowerCase() + ":" + String.valueOf(check.getType()).toLowerCase();
        List<String> countedChecks = Arrays.asList("invalid:a", "invalid:b");
        return countedChecks.contains(checkKey);
    }

    public void broadcastAlert(String message) {
        this.alertStaff.forEach(recipient -> AlertManager.sendColoredMessage(message, recipient));
    }

    private void forwardAlertToBungee(Player player, String message) {
        if (VulcanSettings.settingsPluginMessaging) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("alert");
            out.writeUTF(message);
            player.sendPluginMessage(PluginServices.INSTANCE.getVulcanPlugin(), "vulcan:bungee", out.toByteArray());
        }
    }

    public Set getAlertStaff() {
        return this.alertStaff;
    }

    public Set getVerboseStaff() {
        return this.verboseStaff;
    }

    private static void sendColoredMessage(String message, Player player) {
        player.sendMessage(ChatColorUtils.colorize(message));
    }

    private static void sendViolationWebhook(AbstractCheck check, int violations, PlayerData data, String name, String checkName, String maxViolations, String x, String y, String z, String world, String vl, String cps, String version, String totalViolations, String ping, String description, String tps, String info) {
        int interval = (Integer) VulcanSettings.discordAlertIntervals.get(check.getCheckKey());
        if (interval <= 0 || violations % interval != 0)
            return;
        if (VulcanSettings.discordWebhookCooldown && PluginServices.INSTANCE.getPunishmentsInWindow() >= VulcanSettings.discordWebhookCooldownMaxWebhooks)
            return;
        Player opponent = data.getRelativeMoveTracker().getPlayer();
        String opponentName = opponent == null ? "None" : opponent.getName();
        java.util.function.UnaryOperator<String> format = template -> {
            String message = template.replace("%player%", name).replace("%name%", name).replace("%uuid%", data.getUniqueId().toString()).replace("%check%", checkName).replace("%max-vl%", maxViolations).replace("%server-name%", VulcanSettings.settingsServerName).replace("%client-brand%", data.getClientBrand()).replace("%client-version%", data.getUser().getClientVersion().getReleaseName()).replace("%x%", x).replace("%y%", y).replace("%z%", z).replace("%world%", world).replace("%vl%", vl).replace("%cps%", cps).replace("%version%", version).replace("%total-violations%", totalViolations).replace("%ping%", ping).replace("%description%", description).replace("%complex-type%", check.getCheck().complexType()).replace("%category%", ViolationFormatter.capitalize(check.getCategory().toLowerCase(java.util.Locale.ROOT))).replace("%opponent%", opponentName).replace("%opponennt%", opponentName).replace("%dev%", check.getCheck().experimental() ? ChatColorUtils.colorize(VulcanSettings.alertsExperimentalSymbol) : "").replace("%type%", Character.toString(check.getCheck().type())).replace("%tps%", tps).replace("%info%", info);
            return PluginServices.INSTANCE.isPlaceholderApiEnabled() ? PlaceholderAPI.setPlaceholders(data.getPlayer(), message) : message;
        };
        DiscordWebhook webhook = new DiscordWebhook(VulcanSettings.discordWebhookAlertsUrl);
        webhook.setAvatarUrl(VulcanSettings.discordWebhookAlertsAvatar);
        webhook.setUsername(VulcanSettings.discordWebhookAlertsUsername);
        MessageBuilder embed = new MessageBuilder().setTitle(format.apply(VulcanSettings.discordWebhookAlertsTitle)).setThumbnail(format.apply(VulcanSettings.discordWebhookAlertsThumbnail)).setColor(new Color(VulcanSettings.discordWebhookAlertsColorR, VulcanSettings.discordWebhookAlertsColorG, VulcanSettings.discordWebhookAlertsColorB)).setDescription(format.apply(VulcanSettings.discordWebhookAlertsDescription));
        if (VulcanSettings.discordWebhookAlertsDescriptionField)
            embed.addField("Description", description, true);
        if (VulcanSettings.discordWebhookAlertsInformationField)
            embed.addField("Information", info, true);
        if (VulcanSettings.discordWebhookAlertsServerNameField)
            embed.addField("Server Name", VulcanSettings.settingsServerName, true);
        if (VulcanSettings.discordWebhookAlertsClientBrandField)
            embed.addField("Client Brand", data.getClientBrand(), true);
        if (VulcanSettings.discordWebhookAlertsClientVersionField)
            embed.addField("Client Version", data.getUser().getClientVersion().getReleaseName(), true);
        if (VulcanSettings.discordWebhookAlertsPingTpsField)
            embed.addField("Ping | TPS", ping + "ms | " + tps, true);
        if (VulcanSettings.discordWebhookAlertsLocationField)
            embed.addField("Location", "(" + world + ", " + x + ", " + y + ", " + z + ")", true);
        webhook.addEmbed(embed);
        if (!VulcanSettings.discordWebhookAlertsContent.isEmpty())
            webhook.setContent(format.apply(VulcanSettings.discordWebhookAlertsContent));
        webhook.send();
        if (VulcanSettings.discordWebhookCooldown)
            PluginServices.INSTANCE.setPunishmentsInWindow(PluginServices.INSTANCE.getPunishmentsInWindow() + 1);
    }

    private static void runAlertCommands(String playerName, String maxViolations, PlayerData data, String info, AbstractCheck check, String ping, String x, String y, String z, String cps, String world, String tps, String version, String violations) {
        VulcanSettings.alertsCustomCommands.forEach(line -> AlertManager.runAlertCommand(playerName, maxViolations, data, info, check, ping, x, y, z, cps, world, tps, version, violations, line));
    }

    private static void runAlertCommand(String playerName, String maxViolations, PlayerData data, String info, AbstractCheck check, String ping, String x, String y, String z, String cps, String world, String tps, String version, String violations, String command) {
        String formattedCommand = command.replace("%player%", playerName).replaceAll("%max-vl%", maxViolations).replaceAll("%client-brand%", data.getClientBrand()).replaceAll("%server-name%", VulcanSettings.settingsServerName).replaceAll("%info%", info);
        RelativeMoveTracker opponentTracker = data.getRelativeMoveTracker();
        String opponentName;
        if (opponentTracker.getPlayer() == null) {
            opponentName = "None";
        } else {
            opponentTracker = data.getRelativeMoveTracker();
            opponentName = opponentTracker.getPlayer().getName();
        }
        formattedCommand = formattedCommand.replaceAll("%opponennt%", opponentName);
        formattedCommand = formattedCommand.replaceAll("%complex-type%", check.getCheck().complexType());
        formattedCommand = formattedCommand.replaceAll("%check%", check.getCheck().name()).replaceAll("%ping%", ping);
        formattedCommand = formattedCommand.replaceAll("%description%", check.getCheck().description()).replaceAll("%x%", x).replaceAll("%y%", y).replaceAll("%z%", z);
        formattedCommand = formattedCommand.replaceAll("%uuid%", data.getPlayer().getUniqueId().toString());
        String category = check.getCategory().toLowerCase();
        formattedCommand = formattedCommand.replaceAll("%category%", ViolationFormatter.capitalize(category)).replaceAll("%cps%", cps).replaceAll("%world%", world).replaceAll("%total-violations%", Integer.toString(data.getTotalViolations())).replaceAll("%tps%", tps);
        formattedCommand = formattedCommand.replaceAll("%version%", version);
        String experimentalSymbol;
        if (check.getCheck().experimental()) {
            String experimentalSymbolSetting = VulcanSettings.alertsExperimentalSymbol;
            experimentalSymbol = ChatColorUtils.colorize(experimentalSymbolSetting);
        } else {
            experimentalSymbol = "";
        }
        formattedCommand = formattedCommand.replaceAll("%dev%", experimentalSymbol).replaceAll("%vl%", violations);
        String commandToDispatch = formattedCommand.replaceAll("%type%", Character.toString(check.getCheck().type()));
        ExemptionManager.dispatchConsoleCommand(commandToDispatch);
    }

    private static void runPlaceholderAlertCommands(PlayerData data, String playerName, String maxViolations, AbstractCheck check, String info, String ping, String x, String y, String z, String cps, String world, String tps, String version, String violations) {
        VulcanSettings.alertsCustomCommands.forEach(line -> AlertManager.runPlaceholderAlertCommand(data, playerName, maxViolations, check, info, ping, x, y, z, cps, world, tps, version, violations, line));
    }

    private static void runPlaceholderAlertCommand(PlayerData data, String playerName, String maxViolations, AbstractCheck check, String info, String ping, String x, String y, String z, String cps, String world, String tps, String version, String violations, String command) {
        Player player = data.getPlayer();
        String formattedCommand = command.replace("%player%", playerName).replaceAll("%max-vl%", maxViolations).replaceAll("%client-brand%", data.getClientBrand());
        String category = check.getCategory().toLowerCase();
        formattedCommand = formattedCommand.replaceAll("%category%", ViolationFormatter.capitalize(category)).replaceAll("%server-name%", VulcanSettings.settingsServerName).replaceAll("%info%", info);
        formattedCommand = formattedCommand.replaceAll("%complex-type%", check.getCheck().complexType());
        formattedCommand = formattedCommand.replaceAll("%check%", check.getCheck().name()).replaceAll("%ping%", ping);
        formattedCommand = formattedCommand.replaceAll("%description%", check.getCheck().description()).replaceAll("%x%", x);
        formattedCommand = formattedCommand.replaceAll("%uuid%", data.getPlayer().getUniqueId().toString());
        RelativeMoveTracker opponentTracker = data.getRelativeMoveTracker();
        String opponentName;
        if (opponentTracker.getPlayer() == null) {
            opponentName = "None";
        } else {
            opponentTracker = data.getRelativeMoveTracker();
            opponentName = opponentTracker.getPlayer().getName();
        }
        formattedCommand = formattedCommand.replaceAll("%opponennt%", opponentName).replaceAll("%y%", y).replaceAll("%z%", z).replaceAll("%cps%", cps).replaceAll("%world%", world).replaceAll("%total-violations%", Integer.toString(data.getTotalViolations())).replaceAll("%tps%", tps);
        formattedCommand = formattedCommand.replaceAll("%version%", version);
        String experimentalSymbol;
        if (check.getCheck().experimental()) {
            String experimentalSymbolSetting = VulcanSettings.alertsExperimentalSymbol;
            experimentalSymbol = ChatColorUtils.colorize(experimentalSymbolSetting);
        } else {
            experimentalSymbol = "";
        }
        formattedCommand = formattedCommand.replaceAll("%dev%", experimentalSymbol).replaceAll("%vl%", violations);
        String commandToDispatch = PlaceholderAPI.setPlaceholders(player, formattedCommand.replaceAll("%type%", Character.toString(check.getCheck().type())));
        ExemptionManager.dispatchConsoleCommand(commandToDispatch);
    }

    private void broadcastAlertComponent(TextComponent component) {
        this.alertStaff.forEach(player -> player.spigot().sendMessage(component));
    }

    private static void sendVerboseComponent(TextComponent component, Player player) {
        player.spigot().sendMessage(component);
    }

    private static void sendAlertComponent(TextComponent component, Player player) {
        player.spigot().sendMessage(component);
    }

    private static void writeViolationLog(AbstractCheck check, PlayerData data, String info) {
        ViolationFormatter.writeViolationLog(check, data, info);
    }

    private static void fireEnableAlertsEvent(VulcanEnableAlertsEvent event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void fireDisableAlertsEvent(VulcanDisableAlertsEvent event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
