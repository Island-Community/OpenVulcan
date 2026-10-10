package me.frep.vulcan.spigot.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import me.frep.vulcan.api.check.Check;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.check.manager.CheckManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.player.settings.PlayerToggles;
import me.frep.vulcan.spigot.tracker.confirmation.PendingConfirmation;
import me.frep.vulcan.spigot.tracker.confirmation.ConfirmationTracker;
import me.frep.vulcan.spigot.task.ViolationResetProcessor;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AlertsCommand extends BaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            if (!sender.hasPermission("vulcan.help")) {
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            VulcanSettings.commandsHelp.forEach(line -> this.sendMessage(sender, line.replace("%version%", PluginServices.INSTANCE.getVulcanPlugin().getDescription().getVersion())));
        } else if (arguments[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("vulcan.help")) {
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            VulcanSettings.commandsHelp.forEach(line -> this.sendMessage(sender, line.replace("%version%", PluginServices.INSTANCE.getVulcanPlugin().getDescription().getVersion())));
        } else if (arguments[0].equalsIgnoreCase("top")) {
            if (!sender.hasPermission("vulcan.top")) {
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            HashMap<UUID, Integer> totalViolationsByPlayer = new HashMap<>();
            HashMap<UUID, Integer> combatViolationsByPlayer = new HashMap<>();
            HashMap<UUID, Integer> movementViolationsByPlayer = new HashMap<>();
            HashMap<UUID, Integer> playerViolationsByPlayer = new HashMap<>();
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            for (PlayerData playerData : playerDataStore.getPlayers()) {
                totalViolationsByPlayer.put(playerData.getPlayer().getUniqueId(), playerData.getTotalViolations());
                combatViolationsByPlayer.put(playerData.getPlayer().getUniqueId(), playerData.getCombatViolations());
                movementViolationsByPlayer.put(playerData.getPlayer().getUniqueId(), playerData.getMovementViolations());
                playerViolationsByPlayer.put(playerData.getPlayer().getUniqueId(), playerData.getPlayerViolations());
            }
            Map<UUID, Integer> sortedTotalViolations = totalViolationsByPlayer.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue, AlertsCommand::keepViolationCount, LinkedHashMap::new));
            Map<UUID, Integer> sortedCombatViolations = combatViolationsByPlayer.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue, AlertsCommand::keepViolationCount, LinkedHashMap::new));
            Map<UUID, Integer> sortedMovementViolations = movementViolationsByPlayer.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue, AlertsCommand::keepViolationCount, LinkedHashMap::new));
            Map<UUID, Integer> sortedPlayerViolations = playerViolationsByPlayer.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue())).collect(Collectors.toMap(Entry::getKey, Entry::getValue, AlertsCommand::keepViolationCount, LinkedHashMap::new));
            UUID[] totalRanking = sortedTotalViolations.keySet().toArray(UUID[]::new);
            UUID[] combatRanking = sortedCombatViolations.keySet().toArray(UUID[]::new);
            UUID[] movementRanking = sortedMovementViolations.keySet().toArray(UUID[]::new);
            UUID[] playerRanking = sortedPlayerViolations.keySet().toArray(UUID[]::new);
            UUID totalFirstId = totalRanking.length > 0 ? totalRanking[0] : null;
            UUID totalSecondId = totalRanking.length > 1 ? totalRanking[1] : null;
            UUID totalThirdId = totalRanking.length > 2 ? totalRanking[2] : null;
            UUID totalFourthId = totalRanking.length > 3 ? totalRanking[3] : null;
            UUID totalFifthId = totalRanking.length > 4 ? totalRanking[4] : null;
            Player totalFirstPlayer = null;
            if (totalFirstId != null) {
                totalFirstPlayer = Bukkit.getPlayer(totalFirstId);
            }
            Player totalSecondPlayer = null;
            if (totalSecondId != null) {
                totalSecondPlayer = Bukkit.getPlayer(totalSecondId);
            }
            Player totalThirdPlayer = null;
            if (totalThirdId != null) {
                totalThirdPlayer = Bukkit.getPlayer(totalThirdId);
            }
            Player totalFourthPlayer = null;
            if (totalFourthId != null) {
                totalFourthPlayer = Bukkit.getPlayer(totalFourthId);
            }
            Player totalFifthPlayer = null;
            if (totalFifthId != null) {
                totalFifthPlayer = Bukkit.getPlayer(totalFifthId);
            }
            UUID combatFirstId = combatRanking.length > 0 ? combatRanking[0] : null;
            UUID combatSecondId = combatRanking.length > 1 ? combatRanking[1] : null;
            UUID combatThirdId = combatRanking.length > 2 ? combatRanking[2] : null;
            UUID combatFourthId = combatRanking.length > 3 ? combatRanking[3] : null;
            UUID combatFifthId = combatRanking.length > 4 ? combatRanking[4] : null;
            Player combatFirstPlayer = null;
            if (combatFirstId != null) {
                combatFirstPlayer = Bukkit.getPlayer(combatFirstId);
            }
            Player combatSecondPlayer = null;
            if (combatSecondId != null) {
                combatSecondPlayer = Bukkit.getPlayer(combatSecondId);
            }
            Player combatThirdPlayer = null;
            if (combatThirdId != null) {
                combatThirdPlayer = Bukkit.getPlayer(combatThirdId);
            }
            Player combatFourthPlayer = null;
            if (combatFourthId != null) {
                combatFourthPlayer = Bukkit.getPlayer(combatFourthId);
            }
            Player combatFifthPlayer = null;
            if (combatFifthId != null) {
                combatFifthPlayer = Bukkit.getPlayer(combatFifthId);
            }
            UUID movementFirstId = movementRanking.length > 0 ? movementRanking[0] : null;
            UUID movementSecondId = movementRanking.length > 1 ? movementRanking[1] : null;
            UUID movementThirdId = movementRanking.length > 2 ? movementRanking[2] : null;
            UUID movementFourthId = movementRanking.length > 3 ? movementRanking[3] : null;
            UUID movementFifthId = movementRanking.length > 4 ? movementRanking[4] : null;
            Player movementFirstPlayer = null;
            if (movementFirstId != null) {
                movementFirstPlayer = Bukkit.getPlayer(movementFirstId);
            }
            Player movementSecondPlayer = null;
            if (movementSecondId != null) {
                movementSecondPlayer = Bukkit.getPlayer(movementSecondId);
            }
            Player movementThirdPlayer = null;
            if (movementThirdId != null) {
                movementThirdPlayer = Bukkit.getPlayer(movementThirdId);
            }
            Player movementFourthPlayer = null;
            if (movementFourthId != null) {
                movementFourthPlayer = Bukkit.getPlayer(movementFourthId);
            }
            Player movementFifthPlayer = null;
            if (movementFifthId != null) {
                movementFifthPlayer = Bukkit.getPlayer(movementFifthId);
            }
            UUID playerFirstId = playerRanking.length > 0 ? playerRanking[0] : null;
            UUID playerSecondId = playerRanking.length > 1 ? playerRanking[1] : null;
            UUID playerThirdId = playerRanking.length > 2 ? playerRanking[2] : null;
            UUID playerFourthId = playerRanking.length > 3 ? playerRanking[3] : null;
            UUID playerFifthId = playerRanking.length > 4 ? playerRanking[4] : null;
            Player playerFirstPlayer = null;
            if (playerFirstId != null) {
                playerFirstPlayer = Bukkit.getPlayer(playerFirstId);
            }
            Player playerSecondPlayer = null;
            if (playerSecondId != null) {
                playerSecondPlayer = Bukkit.getPlayer(playerSecondId);
            }
            Player playerThirdPlayer = null;
            if (playerThirdId != null) {
                playerThirdPlayer = Bukkit.getPlayer(playerThirdId);
            }
            Player playerFourthPlayer = null;
            if (playerFourthId != null) {
                playerFourthPlayer = Bukkit.getPlayer(playerFourthId);
            }
            Player playerFifthPlayer = null;
            if (playerFifthId != null) {
                playerFifthPlayer = Bukkit.getPlayer(playerFifthId);
            }
            String totalFirstName = totalFirstPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : totalFirstPlayer.getName();
            String totalSecondName = totalSecondPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : totalSecondPlayer.getName();
            String totalThirdName = totalThirdPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : totalThirdPlayer.getName();
            String totalFourthName = totalFourthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : totalFourthPlayer.getName();
            String totalFifthName = totalFifthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : totalFifthPlayer.getName();
            String totalFirstViolations = totalFirstId == null ? "0" : sortedTotalViolations.get(totalFirstId).toString();
            String totalSecondViolations = totalSecondId == null ? "0" : sortedTotalViolations.get(totalSecondId).toString();
            String totalThirdViolations = totalThirdId == null ? "0" : sortedTotalViolations.get(totalThirdId).toString();
            String totalFourthViolations = totalFourthId == null ? "0" : sortedTotalViolations.get(totalFourthId).toString();
            String totalFifthViolations = totalFifthId == null ? "0" : sortedTotalViolations.get(totalFifthId).toString();
            String combatFirstName = combatFirstPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : combatFirstPlayer.getName();
            String combatSecondName = combatSecondPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : combatSecondPlayer.getName();
            String combatThirdName = combatThirdPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : combatThirdPlayer.getName();
            String combatFourthName = combatFourthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : combatFourthPlayer.getName();
            String combatFifthName = combatFifthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : combatFifthPlayer.getName();
            String combatFirstViolations = combatFirstId == null ? "0" : sortedCombatViolations.get(combatFirstId).toString();
            String combatSecondViolations = combatSecondId == null ? "0" : sortedCombatViolations.get(combatSecondId).toString();
            String combatThirdViolations = combatThirdId == null ? "0" : sortedCombatViolations.get(combatThirdId).toString();
            String combatFourthViolations = combatFourthId == null ? "0" : sortedCombatViolations.get(combatFourthId).toString();
            String combatFifthViolations = combatFifthId == null ? "0" : sortedCombatViolations.get(combatFifthId).toString();
            String movementFirstName = movementFirstPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : movementFirstPlayer.getName();
            String movementSecondName = movementSecondPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : movementSecondPlayer.getName();
            String movementThirdName = movementThirdPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : movementThirdPlayer.getName();
            String movementFourthName = movementFourthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : movementFourthPlayer.getName();
            String movementFifthName = movementFifthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : movementFifthPlayer.getName();
            String movementFirstViolations = movementFirstId == null ? "0" : sortedMovementViolations.get(movementFirstId).toString();
            String movementSecondViolations = movementSecondId == null ? "0" : sortedMovementViolations.get(movementSecondId).toString();
            String movementThirdViolations = movementThirdId == null ? "0" : sortedMovementViolations.get(movementThirdId).toString();
            String movementFourthViolations = movementFourthId == null ? "0" : sortedMovementViolations.get(movementFourthId).toString();
            String movementFifthViolations = movementFifthId == null ? "0" : sortedMovementViolations.get(movementFifthId).toString();
            String playerFirstName = playerFirstPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : playerFirstPlayer.getName();
            String playerSecondName = playerSecondPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : playerSecondPlayer.getName();
            String playerThirdName = playerThirdPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : playerThirdPlayer.getName();
            String playerFourthName = playerFourthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : playerFourthPlayer.getName();
            String playerFifthName = playerFifthPlayer == null ? VulcanSettings.settingsTopCommandUnresolved : playerFifthPlayer.getName();
            String playerFirstViolations = playerFirstId == null ? "0" : sortedPlayerViolations.get(playerFirstId).toString();
            String playerSecondViolations = playerSecondId == null ? "0" : sortedPlayerViolations.get(playerSecondId).toString();
            String playerThirdViolations = playerThirdId == null ? "0" : sortedPlayerViolations.get(playerThirdId).toString();
            String playerFourthViolations = playerFourthId == null ? "0" : sortedPlayerViolations.get(playerFourthId).toString();
            String playerFifthViolations = playerFifthId == null ? "0" : sortedPlayerViolations.get(playerFifthId).toString();
            VulcanSettings.commandsTop.forEach(line -> this.sendLeaderboardLine(sender, totalFirstName, totalSecondName, totalThirdName, totalFourthName, totalFifthName, totalFirstViolations, totalSecondViolations, totalThirdViolations, totalFourthViolations, totalFifthViolations, combatFirstName, combatSecondName, combatThirdName, combatFourthName, combatFifthName, combatFirstViolations, combatSecondViolations, combatThirdViolations, combatFourthViolations, combatFifthViolations, movementFirstName, movementSecondName, movementThirdName, movementFourthName, movementFifthName, movementFirstViolations, movementSecondViolations, movementThirdViolations, movementFourthViolations, movementFifthViolations, playerFirstName, playerSecondName, playerThirdName, playerFourthName, playerFifthName, playerFirstViolations, playerSecondViolations, playerThirdViolations, playerFourthViolations, playerFifthViolations, line));
        } else if (arguments[0].equalsIgnoreCase("exempts")) {
            if (!sender.hasPermission("vulcan.exempts")) {
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            if (arguments.length == 1) {
                this.sendMessage(sender, "must include player parameter");
            } else {
                String playerName = arguments[1];
                Player target = Bukkit.getPlayer(playerName);
                if (target == null) {
                    this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                    return true;
                }
                PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                PlayerData playerData = playerDataStore.getPlayerData(target);
                if (playerData == null) {
                    this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                    return true;
                }
                ArrayList<String> exemptions = new ArrayList<>();
                for (ExemptType exemptType : ExemptType.values()) {
                    PlayerToggles playerToggles = playerData.getPlayerToggles();
                    if (playerToggles.isExempt(exemptType)) {
                        exemptions.add(exemptType.toString());
                    }
                }
                this.sendSeparator(sender);
                this.sendMessage(sender, "&cExemptions:");
                exemptions.forEach(line -> this.sendMessage(sender, " &8- &7" + line));
                this.sendMessage(sender, "&cBypass Permission: " + target.hasPermission("vulcan.bypass.speeda"));
                this.sendSeparator(sender);
            }
        } else if (arguments[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("vulcan.reload")) {
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            PluginServices.INSTANCE.reload();
            this.sendMessage(sender, VulcanSettings.messagesReloadSuccess);
        } else if (arguments[0].equalsIgnoreCase("description")) {
            if (!sender.hasPermission("vulcan.description")) {
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            if (arguments.length == 1) {
                this.sendMessage(sender, VulcanSettings.messagesDescriptionCommandSyntax);
                return true;
            }
            String checkId = arguments[1].toLowerCase();
            for (Class<?> checkClass : CheckManager.checkTypes) {
                CheckInfo checkInfo = checkClass.getAnnotation(CheckInfo.class);
                String displayCheckId = (checkInfo.name() + checkInfo.type()).replace(" ", "");
                if (checkId.equalsIgnoreCase(displayCheckId.toLowerCase())) {
                    this.sendMessage(sender, VulcanSettings.commandsDescription.replaceAll("%check%", displayCheckId).replaceAll("%description%", checkInfo.description()));
                }
            }
        } else {
            if (arguments[0].equalsIgnoreCase("testalert")) {
                if (sender.hasPermission("vulcan.testalert")) {
                    if (sender instanceof Player player) {
                        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                        PlayerData playerData = playerDataStore.getPlayerData(player);
                        if (playerData == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        if (sender.hasPermission("vulcan.bypass.speeda")) {
                            this.sendSeparator(sender);
                            this.sendMessage(sender, ("&4&lNOTE! &cThis command emulates a real alert message, so if you are OP or have the bypass per" + "mission it will not work! If you use LuckPerms, you can negate the bypass permission by doing:"));
                            this.sendMessage(sender, " &8- &c/lp user " + sender.getName() + " permission set vulcan.bypass.* false");
                            this.sendSeparator(sender);
                            return true;
                        }
                        for (Check check : playerData.getChecks()) {
                            if (check.getDisplayName().toLowerCase().equals("speed") && Character.toString(check.getDisplayType()).toLowerCase().equals("a")) {
                                PluginServices.INSTANCE.getViolationExecutor().execute(() -> AlertsCommand.sendTestAlerts(check, playerData));
                            }
                        }
                        this.sendMessage(sender, VulcanSettings.messagesSentTestAlert);
                        return true;
                    }
                    this.sendMessage(sender, VulcanSettings.messagesCantExecuteFromConsole);
                    return true;
                }
                this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                return true;
            }
            if (arguments[0].equalsIgnoreCase("clickalert")) {
                if (!sender.hasPermission("vulcan.clickalert")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    this.sendMessage(sender, VulcanSettings.messagesCantExecuteFromConsole);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, "&cInvalid syntax!");
                } else {
                    String playerName = arguments[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                    } else {
                        VulcanSettings.alertsClickAlertCommandCommands.forEach(line -> ((Player) sender).performCommand(line.replace("%player%", target.getName())));
                    }
                }
            } else if (arguments[0].equalsIgnoreCase("disablecheck")) {
                if (!sender.hasPermission("vulcan.disablecheck")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, VulcanSettings.messagesDisableCheckCommandSyntax);
                } else {
                    String checkId = arguments[1];
                    if (!VulcanSettings.checkEnabled.containsKey(checkId) || !(Boolean) VulcanSettings.checkEnabled.get(checkId)) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidCheck);
                        return true;
                    }
                    VulcanSettings.checkEnabled.remove(checkId);
                    VulcanSettings.checkEnabled.put(checkId, false);
                    this.sendMessage(sender, VulcanSettings.messagesDisabledCheck.replaceAll("%check%", checkId));
                }
            } else if (arguments[0].equalsIgnoreCase("freeze")) {
                if (!sender.hasPermission("vulcan.freeze")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, VulcanSettings.messagesFreezeCommandSyntax);
                    return true;
                }
                String playerName = arguments[1];
                Player target = Bukkit.getPlayer(playerName);
                if (target == null) {
                    this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                    return true;
                }
                PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                PlayerData playerData = playerDataStore.getPlayerData(target);
                if (playerData == null) {
                    this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                    return true;
                }
                CollisionState collisionState = playerData.getCollisionState();
                boolean frozen = collisionState.isFrozen();
                if (frozen) {
                    playerData.getCollisionState().setFrozen(false);
                    String unfreezeMessage = VulcanSettings.messagesUnfroze.replaceAll("%player%", target.getName());
                    String unfreezeBroadcast = VulcanSettings.messagesUnfrozeStaffBroadcast.replaceAll("%player%", target.getName()).replaceAll("%staff%", sender.getName());
                    if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                        unfreezeMessage = PlaceholderAPI.setPlaceholders(target, unfreezeMessage);
                        unfreezeBroadcast = PlaceholderAPI.setPlaceholders(target, unfreezeBroadcast);
                    }
                    this.sendMessage(sender, unfreezeMessage);
                    AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
                    alertManager.broadcastAlert(ChatColorUtils.colorize(unfreezeBroadcast));
                } else {
                    playerData.getCollisionState().setFrozen(true);
                    String freezeMessage = VulcanSettings.messagesFroze.replaceAll("%player%", target.getName());
                    String freezeBroadcast = VulcanSettings.messagesFrozeStaffBroadcast.replaceAll("%player%", target.getName()).replaceAll("%staff%", sender.getName());
                    if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                        freezeMessage = PlaceholderAPI.setPlaceholders(target, freezeMessage);
                        freezeBroadcast = PlaceholderAPI.setPlaceholders(target, freezeBroadcast);
                    }
                    this.sendMessage(sender, freezeMessage);
                    AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
                    alertManager.broadcastAlert(ChatColorUtils.colorize(freezeBroadcast));
                }
            } else if (arguments[0].equalsIgnoreCase("ban")) {
                if (!sender.hasPermission("vulcan.ban")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, VulcanSettings.messagesBanCommandSyntax);
                } else {
                    String playerName = arguments[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    for (String punishmentCommand : VulcanSettings.commandsBanCommands) {
                        if (!punishmentCommand.startsWith("Bungee:") && !punishmentCommand.startsWith("bungee")) {
                            ExemptionManager.dispatchConsoleCommand(ChatColorUtils.colorize(punishmentCommand.replaceAll("%player%", playerName)));
                        } else {
                            this.sendBungeePunishment(target, ChatColorUtils.colorize(punishmentCommand.replaceAll("Bungee:", "").replaceAll("bungee:", "")).replaceAll("%player%", playerName));
                        }
                    }
                    VulcanSettings.commandsBanBroadcast.forEach(line -> ExemptionManager.broadcast(line.replace("%player%", playerName)));
                }
            } else if (arguments[0].equalsIgnoreCase("shuffle")) {
                if (sender.hasPermission("vulcan.shuffle")) {
                    if (arguments.length == 1) {
                        this.sendMessage(sender, VulcanSettings.messagesShuffleCommandSyntax);
                    } else {
                        String playerName = arguments[1];
                        Player target = Bukkit.getPlayer(playerName);
                        if (target == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        CheckUtils.sendHotbarShuffle(target);
                        String shuffleMessage = VulcanSettings.messagesShuffledHotbar.replaceAll("%player%", playerName);
                        if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                            shuffleMessage = PlaceholderAPI.setPlaceholders(target, shuffleMessage);
                        }
                        this.sendMessage(sender, shuffleMessage);
                    }
                }
            } else if (arguments[0].equalsIgnoreCase("rotate")) {
                if (sender.hasPermission("vulcan.rotate")) {
                    if (arguments.length == 1) {
                        this.sendMessage(sender, VulcanSettings.messagesRotateCommandSyntax);
                    } else {
                        String playerName = arguments[1];
                        Player target = Bukkit.getPlayer(playerName);
                        if (target == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        CheckUtils.rotatePlayerRandomly(target);
                        String rotateMessage = VulcanSettings.messagesRandomlyRotated.replaceAll("%player%", playerName);
                        if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                            rotateMessage = PlaceholderAPI.setPlaceholders(target, rotateMessage);
                        }
                        if (sender instanceof Player) {
                            this.sendMessage(sender, rotateMessage);
                        }
                    }
                }
            } else if (arguments[0].equalsIgnoreCase("violations")) {
                if (!sender.hasPermission("vulcan.violations")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, VulcanSettings.messagesViolationsCommandSyntax);
                } else {
                    String playerName = arguments[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                    PlayerData playerData = playerDataStore.getPlayerData(target);
                    if (playerData == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    int totalViolations = playerData.getTotalViolations();
                    if (totalViolations < 1) {
                        this.sendMessage(sender, VulcanSettings.messagesNoLogs);
                        return true;
                    }
                    this.sendSeparator(sender);
                    this.sendMessage(sender, "&7Total Violations for &c" + playerName + " &7(&c" + totalViolations + "&7):");
                    for (AbstractCheck check : playerData.getChecks()) {
                        if (check.getVl() > 0) {
                            StringBuilder violationLine = new StringBuilder().append(" &8- &b");
                            this.sendMessage(sender, violationLine.append(check.getCheck().name()).append(" &7(&bType ").append(Character.toUpperCase(check.getType())).append("&7) VL: ").append(check.getVl()).toString());
                        }
                    }
                    this.sendSeparator(sender);
                }
            } else if (arguments[0].equalsIgnoreCase("cps")) {
                if (!sender.hasPermission("vulcan.cps")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, VulcanSettings.messagesCpsCommandSyntax);
                } else {
                    String playerName = arguments[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                    PlayerData playerData = playerDataStore.getPlayerData(target);
                    if (playerData == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    DecimalFormat cpsFormat = new DecimalFormat("##.##");
                    ClickTimingData clickTimingData = playerData.getClickTimingData();
                    String clicksPerSecond = cpsFormat.format(clickTimingData.getClicksPerSecond());
                    this.sendMessage(sender, VulcanSettings.commandsCps.replaceAll("%cps%", clicksPerSecond).replace("%player%", playerName));
                }
            } else if (!arguments[0].equalsIgnoreCase("kb") && !arguments[0].equalsIgnoreCase("knockback")) {
                if (arguments[0].equalsIgnoreCase("reset")) {
                    if (!sender.hasPermission("vulcan.reset")) {
                        this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                        return true;
                    }
                    if (arguments.length == 1) {
                        ViolationResetProcessor.resetViolations();
                    } else {
                        String playerName = arguments[1];
                        Player target = Bukkit.getPlayer(playerName);
                        if (target == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                        PlayerData playerData = playerDataStore.getPlayerData(target);
                        if (playerData == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        for (Check check : playerData.getChecks()) {
                            check.setVl(0);
                            check.setBuffer(0.0);
                        }
                        playerData.setMovementViolations(0);
                        playerData.setTotalViolations(0);
                        playerData.setCombatViolations(0);
                        playerData.setAutoClickerViolations(0);
                        playerData.setScaffoldViolations(0);
                        playerData.setPlayerViolations(0);
                        playerData.setTimerViolations(0);
                        String resetMessage = VulcanSettings.messagesViolationsReset.replaceAll("%player%", playerName);
                        if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                            resetMessage = PlaceholderAPI.setPlaceholders(target, resetMessage);
                        }
                        this.sendMessage(sender, resetMessage);
                    }
                } else if (arguments[0].equalsIgnoreCase("connection")) {
                    if (!sender.hasPermission("vulcan.connection")) {
                        this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                        return true;
                    }
                    if (arguments.length == 1) {
                        this.sendMessage(sender, VulcanSettings.messagesConnectionCommandSyntax);
                    } else {
                        String playerName = arguments[1];
                        Player target = Bukkit.getPlayer(playerName);
                        if (target == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                        PlayerData playerData = playerDataStore.getPlayerData(target);
                        if (playerData == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        ConfirmationTracker transactionTracker = playerData.getConfirmationTracker();
                        String transactionPing = Long.toString(transactionTracker.getTransactionPingMillis());
                        ConnectionTracker keepAliveTracker = playerData.getConnectionTracker();
                        String keepAlivePing = Integer.toString(keepAliveTracker.getKeepAlivePing());
                        long now = System.currentTimeMillis();
                        ConfirmationTracker lastConfirmationTracker = playerData.getConfirmationTracker();
                        String lastConfirmationDelay = Long.toString(now - lastConfirmationTracker.getLastConfirmationAtMillis());
                        ConnectionTracker movementConnectionTracker = playerData.getConnectionTracker();
                        String movementDelay = Long.toString(movementConnectionTracker.getMovementDelayMillis());
                        ConfirmationTracker pendingTracker = playerData.getConfirmationTracker();
                        String queuedTransactions = Integer.toString(pendingTracker.getPendingConfirmations().size());
                        VulcanSettings.commandsConnection.forEach(line -> this.sendConnectionInfoLine(sender, target, transactionPing, keepAlivePing, queuedTransactions, lastConfirmationDelay, movementDelay, line));
                        StringBuilder connectionLine = new StringBuilder().append("Pending Confirmations (");
                        ConfirmationTracker confirmationTracker = playerData.getConfirmationTracker();
                        connectionLine = connectionLine.append(confirmationTracker.getPendingConfirmations().size()).append("): ");
                        confirmationTracker = playerData.getConfirmationTracker();
                        this.sendMessage(sender, connectionLine.append(confirmationTracker.getPendingConfirmations().stream().map(AlertsCommand::formatPendingConfirmationId).collect(Collectors.joining(", "))).toString());
                        connectionLine = new StringBuilder().append("&7Last Replied IDs: ");
                        this.sendMessage(sender, connectionLine.append(playerData.getRepliedConfirmationIds().stream().map(String::valueOf).collect(Collectors.joining("&7, &c"))).toString());
                        connectionLine = new StringBuilder().append("&7Last Replied Raw IDs: ");
                        this.sendMessage(sender, connectionLine.append(playerData.getRawConfirmationIds().stream().map(String::valueOf).collect(Collectors.joining("&7, &c"))).toString());
                    }
                } else if (arguments[0].equalsIgnoreCase("dump")) {
                    if (!sender.hasPermission("vulcan.dump")) {
                        this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                        return true;
                    }
                    this.sendSeparator(sender);
                    this.sendMessage(sender, "&7Vulcan Version: &c" + PluginServices.INSTANCE.getVulcanPlugin().getDescription().getVersion());
                    StringBuilder dumpLine = new StringBuilder().append("&7Server Version: &c");
                    this.sendMessage(sender, dumpLine.append(ExemptionManager.getServerVersion()).toString());
                    this.sendMessage(sender, "&7Server Software: &c" + Bukkit.getName() + " " + Bukkit.getVersion());
                    this.sendMessage(sender, "&7PacketEvents Version: &c" + Bukkit.getPluginManager().getPlugin("packetevents").getDescription().getVersion());
                    this.sendMessage(sender, "");
                    dumpLine = new StringBuilder().append("&7TPS: &c");
                    this.sendMessage(sender, dumpLine.append(ExemptionManager.getTps()).toString());
                    this.sendMessage(sender, "");
                    this.sendSeparator(sender);
                } else if (arguments[0].equalsIgnoreCase("attributes")) {
                    if (!sender.hasPermission("vulcan.attributes")) {
                        this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                        return true;
                    }
                    if (arguments.length < 2) {
                        this.sendMessage(sender, "&cUsage: /vulcan attributes <player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(arguments[1]);
                    if (target == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                    PlayerData playerData = playerDataStore.getPlayerData(target);
                    if (playerData == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    this.sendSeparator(sender);
                    this.sendMessage(sender, "&7Attributes for &c" + target.getName() + "&7:");
                    StringBuilder attributeLine = new StringBuilder().append(" &8- &7Movement Speed: &c");
                    MotionState motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getMovementSpeed())).toString());
                    attributeLine = new StringBuilder().append(" &8- &7Step Height: &c");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getStepHeight())).toString());
                    attributeLine = new StringBuilder().append(" &8- &7Jump Strength: &c");
                    motionState = playerData.getMotionState();
                    attributeLine = attributeLine.append(MathUtils.formatNumber(motionState.getJumpStrength())).append(" &8(previous: ");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getPreviousJumpStrength())).append(")").toString());
                    attributeLine = new StringBuilder().append(" &8- &7Gravity: &c");
                    motionState = playerData.getMotionState();
                    attributeLine = attributeLine.append(MathUtils.formatNumber(motionState.getGravity())).append(" &8(previous: ");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getPreviousGravity())).append(")").toString());
                    attributeLine = new StringBuilder().append(" &8- &7Entity Interaction Range: &c");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getEntityInteractionRange())).toString());
                    attributeLine = new StringBuilder().append(" &8- &7Bounciness: &c");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getBounciness())).toString());
                    attributeLine = new StringBuilder().append(" &8- &7Air Drag Modifier: &c");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(motionState.getAirDragModifier())).toString());
                    attributeLine = new StringBuilder().append(" &8- &7Friction Modifier: &c");
                    this.sendMessage(sender, attributeLine.append(MathUtils.formatNumber(playerData.getMotionState().getFrictionModifier())).toString());
                    this.sendMessage(sender, "");
                    attributeLine = new StringBuilder().append(" &8- &7Ticks Since Jump Strength Change: &c");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(motionState.getTicksSinceJumpStrengthChange()).toString());
                    attributeLine = new StringBuilder().append(" &8- &7Ticks Since Gravity Change: &c");
                    motionState = playerData.getMotionState();
                    this.sendMessage(sender, attributeLine.append(motionState.getTicksSinceGravityChange()).toString());
                    this.sendSeparator(sender);
                } else {
                    if (!arguments[0].equalsIgnoreCase("profile")) {
                        this.sendMessage(sender, VulcanSettings.messagesUnknownCommand);
                        return true;
                    }
                    if (!sender.hasPermission("vulcan.profile")) {
                        this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                        return true;
                    }
                    if (arguments.length == 1) {
                        this.sendMessage(sender, VulcanSettings.messagesProfileCommandSyntax);
                    } else {
                        String playerName = arguments[1];
                        Player target = Bukkit.getPlayer(playerName);
                        if (target == null) {
                            this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                            return true;
                        }
                        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                        PlayerData playerData = playerDataStore.getPlayerData(target);
                        if (playerData != null) {
                            if (ExemptionManager.isNewerThan1_13()) {
                                String clientBrand = playerData.getClientBrand() == null ? "Unresolved" : playerData.getClientBrand();
                                RotationTracker rotationTracker = playerData.getRotationTracker();
                                String sensitivity = Integer.toString(rotationTracker.getSensitivity());
                                String totalViolations = Integer.toString(playerData.getTotalViolations());
                                String combatViolations = Integer.toString(playerData.getCombatViolations());
                                String movementViolations = Integer.toString(playerData.getMovementViolations());
                                String playerViolations = Integer.toString(playerData.getPlayerViolations());
                                String clientVersion = playerData.getUser().getClientVersion().getReleaseName();
                                ClickTimingData clickTimingData = playerData.getClickTimingData();
                                String clicksPerSecond = MathUtils.formatNumber(clickTimingData.getClicksPerSecond());
                                String uuid = playerData.getPlayer().getUniqueId().toString();
                                CollisionState blockCollisionState = playerData.getCollisionState();
                                String nearbyBlocks;
                                if (blockCollisionState.getSurroundingMaterials() == null) {
                                    nearbyBlocks = "Empty";
                                } else {
                                    CollisionState resolvedBlockCollisionState = playerData.getCollisionState();
                                    nearbyBlocks = resolvedBlockCollisionState.getSurroundingMaterials().toString();
                                }
                                CollisionState entityCollisionState = playerData.getCollisionState();
                                String nearbyEntities;
                                if (entityCollisionState.getNearbyEntities() == null) {
                                    nearbyEntities = "Empty";
                                } else {
                                    CollisionState resolvedEntityCollisionState = playerData.getCollisionState();
                                    nearbyEntities = resolvedEntityCollisionState.getNearbyEntities().toString();
                                }
                                VulcanSettings.commandsProfile.forEach(line -> this.sendPlayerInfoLine(sender, playerData.getPlayerName(), clientBrand, clicksPerSecond, uuid, playerData, nearbyBlocks, nearbyEntities, clientVersion, totalViolations, combatViolations, movementViolations, playerViolations, sensitivity, line));
                            } else {
                                String clientBrand = playerData.getClientBrand() == null ? "Unresolved" : playerData.getClientBrand();
                                RotationTracker rotationTracker = playerData.getRotationTracker();
                                String sensitivity = Integer.toString(rotationTracker.getSensitivity());
                                String totalViolations = Integer.toString(playerData.getTotalViolations());
                                String combatViolations = Integer.toString(playerData.getCombatViolations());
                                String movementViolations = Integer.toString(playerData.getMovementViolations());
                                String playerViolations = Integer.toString(playerData.getPlayerViolations());
                                String clientVersion = playerData.getUser().getClientVersion().getReleaseName();
                                ClickTimingData clickTimingData = playerData.getClickTimingData();
                                String clicksPerSecond = MathUtils.formatNumber(clickTimingData.getClicksPerSecond());
                                String uuid = playerData.getPlayer().getUniqueId().toString();
                                CollisionState blockCollisionState = playerData.getCollisionState();
                                String nearbyBlocks;
                                if (blockCollisionState.getSurroundingMaterialsLegacy() == null) {
                                    nearbyBlocks = "Empty";
                                } else {
                                    CollisionState resolvedBlockCollisionState = playerData.getCollisionState();
                                    nearbyBlocks = resolvedBlockCollisionState.getSurroundingMaterialsLegacy().toString();
                                }
                                CollisionState entityCollisionState = playerData.getCollisionState();
                                String nearbyEntities;
                                if (entityCollisionState.getNearbyEntities() == null) {
                                    nearbyEntities = "Empty";
                                } else {
                                    CollisionState resolvedEntityCollisionState = playerData.getCollisionState();
                                    nearbyEntities = resolvedEntityCollisionState.getNearbyEntities().toString();
                                }
                                VulcanSettings.commandsProfile.forEach(line -> this.sendPlayerInfoLine(sender, playerData.getPlayerName(), clientBrand, clicksPerSecond, uuid, playerData, nearbyBlocks, nearbyEntities, clientVersion, totalViolations, combatViolations, movementViolations, playerViolations, sensitivity, line));
                            }
                        }
                    }
                }
            } else {
                if (!sender.hasPermission("vulcan.kb") && !sender.hasPermission("vulcan.knockback")) {
                    this.sendMessage(sender, VulcanSettings.messagesNoPermission);
                    return true;
                }
                if (arguments.length == 1) {
                    this.sendMessage(sender, VulcanSettings.messagesKbCommandSyntax);
                } else {
                    String playerName = arguments[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        this.sendMessage(sender, VulcanSettings.messagesInvalidTarget);
                        return true;
                    }
                    Vector velocity = new Vector(0.75, 1.0, 0.75);
                    target.setVelocity(velocity);
                    String knockbackMessage = VulcanSettings.messagesKbTestSuccess.replaceAll("%player%", playerName);
                    if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                        knockbackMessage = PlaceholderAPI.setPlaceholders(target, knockbackMessage);
                    }
                    this.sendMessage(sender, knockbackMessage);
                }
            }
        }
        return true;
    }

    private void sendBungeePunishment(Player player, String punishmentCommand) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("punishment");
        output.writeUTF(punishmentCommand);
        player.sendPluginMessage(PluginServices.INSTANCE.getVulcanPlugin(), "vulcan:bungee", output.toByteArray());
    }

    private void sendPlayerInfoLine(CommandSender sender, String playerName, String clientBrand, String clicksPerSecond, String uuid, PlayerData playerData, String nearbyBlocks, String nearbyEntities, String clientVersion, String totalViolations, String combatViolations, String movementViolations, String playerViolations, String sensitivity, String line) {
        String message = line.replaceAll("%player%", playerName).replaceAll("%client-brand%", clientBrand).replaceAll("%cps%", clicksPerSecond).replaceAll("%uuid%", uuid);
        MotionState motionState = playerData.getMotionState();
        message = message.replaceAll("%move-speed%", Double.toString(motionState.getMovementSpeed()));
        CollisionState xCollisionState = playerData.getCollisionState();
        message = message.replaceAll("%x%", Integer.toString(xCollisionState.getBlockX()));
        CollisionState yCollisionState = playerData.getCollisionState();
        message = message.replaceAll("%y%", Integer.toString(yCollisionState.getBlockY()));
        CollisionState zCollisionState = playerData.getCollisionState();
        message = message.replaceAll("%z%", Integer.toString(zCollisionState.getBlockZ()));
        message = message.replaceAll("%world%", playerData.getPlayer().getWorld().getName());
        this.sendMessage(sender, message.replaceAll("%potion-effects%", playerData.getPlayer().getActivePotionEffects().toString()).replaceAll("%nearby-blocks%", nearbyBlocks).replaceAll("%nearby-entities%", nearbyEntities).replaceAll("%client-version%", clientVersion).replaceAll("%total-violations%", totalViolations).replaceAll("%combat-violations%", combatViolations).replaceAll("%movement-violations%", movementViolations).replaceAll("%player-violations%", playerViolations).replaceAll("%sensitivity%", sensitivity));
    }

    private static String formatPendingConfirmationId(PendingConfirmation confirmation) {
        return String.valueOf(confirmation.getId());
    }

    private void sendConnectionInfoLine(CommandSender sender, Player player, String transactionPing, String keepAlivePing, String queuedTransactions, String lastConfirmationDelay, String movementDelay, String line) {
        this.sendMessage(sender, line.replaceAll("%player%", player.getName()).replaceAll("%transaction-ping%", transactionPing).replaceAll("%keepalive-ping%", keepAlivePing).replaceAll("%queued-transactions%", queuedTransactions).replaceAll("%last-replied-transaction%", lastConfirmationDelay).replaceAll("%flying-delay%", movementDelay));
    }

    private static void sendTestAlerts(Check check, PlayerData playerData) {
        AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.processViolation((AbstractCheck) check, playerData, "Test Alert!");
        alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.processViolation((AbstractCheck) check, playerData, "Test Alert!");
        alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.processViolation((AbstractCheck) check, playerData, "Test Alert!");
    }

    private void sendLeaderboardLine(CommandSender sender, String totalFirstName, String totalSecondName, String totalThirdName, String totalFourthName, String totalFifthName, String totalFirstViolations, String totalSecondViolations, String totalThirdViolations, String totalFourthViolations, String totalFifthViolations, String combatFirstName, String combatSecondName, String combatThirdName, String combatFourthName, String combatFifthName, String combatFirstViolations, String combatSecondViolations, String combatThirdViolations, String combatFourthViolations, String combatFifthViolations, String movementFirstName, String movementSecondName, String movementThirdName, String movementFourthName, String movementFifthName, String movementFirstViolations, String movementSecondViolations, String movementThirdViolations, String movementFourthViolations, String movementFifthViolations, String playerFirstName, String playerSecondName, String playerThirdName, String playerFourthName, String playerFifthName, String playerFirstViolations, String playerSecondViolations, String playerThirdViolations, String playerFourthViolations, String playerFifthViolations, String line) {
        this.sendMessage(sender, line.replaceAll("%total-1-name%", totalFirstName).replaceAll("%total-2-name%", totalSecondName).replaceAll("%total-3-name%", totalThirdName).replaceAll("%total-4-name%", totalFourthName).replaceAll("%total-5-name%", totalFifthName).replaceAll("%total-1-violations%", totalFirstViolations).replaceAll("%total-2-violations%", totalSecondViolations).replaceAll("%total-3-violations%", totalThirdViolations).replaceAll("%total-4-violations%", totalFourthViolations).replaceAll("%total-5-violations%", totalFifthViolations).replaceAll("%combat-1-name%", combatFirstName).replaceAll("%combat-2-name%", combatSecondName).replaceAll("%combat-3-name%", combatThirdName).replaceAll("%combat-4-name%", combatFourthName).replaceAll("%combat-5-name%", combatFifthName).replaceAll("%combat-1-violations%", combatFirstViolations).replaceAll("%combat-2-violations%", combatSecondViolations).replaceAll("%combat-3-violations%", combatThirdViolations).replaceAll("%combat-4-violations%", combatFourthViolations).replaceAll("%combat-5-violations%", combatFifthViolations).replaceAll("%movement-1-name%", movementFirstName).replaceAll("%movement-2-name%", movementSecondName).replaceAll("%movement-3-name%", movementThirdName).replaceAll("%movement-4-name%", movementFourthName).replaceAll("%movement-5-name%", movementFifthName).replaceAll("%movement-1-violations%", movementFirstViolations).replaceAll("%movement-2-violations%", movementSecondViolations).replaceAll("%movement-3-violations%", movementThirdViolations).replaceAll("%movement-4-violations%", movementFourthViolations).replaceAll("%movement-5-violations%", movementFifthViolations).replaceAll("%player-1-name%", playerFirstName).replaceAll("%player-2-name%", playerSecondName).replaceAll("%player-3-name%", playerThirdName).replaceAll("%player-4-name%", playerFourthName).replaceAll("%player-5-name%", playerFifthName).replaceAll("%player-1-violations%", playerFirstViolations).replaceAll("%player-2-violations%", playerSecondViolations).replaceAll("%player-3-violations%", playerThirdViolations).replaceAll("%player-4-violations%", playerFourthViolations).replaceAll("%player-5-violations%", playerFifthViolations));
    }

    private static Integer keepViolationCount(Integer existingCount, Integer duplicateCount) {
        return existingCount;
    }
}
