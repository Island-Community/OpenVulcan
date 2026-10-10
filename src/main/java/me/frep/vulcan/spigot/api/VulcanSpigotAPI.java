package me.frep.vulcan.spigot.api;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.frep.vulcan.api.VulcanAPI;
import me.frep.vulcan.api.check.Check;
import me.frep.vulcan.api.data.IPlayerData;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.check.manager.CheckManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.punishment.JudgementDay;
import me.frep.vulcan.spigot.tracker.confirmation.ConfirmationTracker;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import org.bukkit.entity.Player;

public class VulcanSpigotAPI implements VulcanAPI {

    @Override
    public IPlayerData getPlayerData(Player player2) {
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        return playerDataStore.getPlayerData(player2);
    }

    @Override
    public int getPing(Player player2) {
        return CheckUtils.getPing(player2);
    }

    @Override
    public void executeBanWave() {
        JudgementDay.execute();
    }

    @Override
    public boolean isFrozen(Player player2) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        CollisionState collisionState = playerData2.getCollisionState();
        return collisionState.isFrozen();
    }

    @Override
    public double getKurtosis(Player player2) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        return playerData2.getClickTimingData().getKurtosis();
    }

    @Override
    public void setFrozen(Player player2, boolean condition) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        if (playerData2 != null) {
            playerData2.getCollisionState().setFrozen(condition);
        }
    }

    @Override
    public int getSensitivity(Player player2) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        RotationTracker rotationTracker = playerData2.getRotationTracker();
        return rotationTracker.getSensitivity();
    }

    @Override
    public double getCps(Player player2) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        ClickTimingData clickTimingData = playerData2.getClickTimingData();
        return clickTimingData.getClicksPerSecond();
    }

    @Override
    public int getTransactionPing(Player player2) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        ConfirmationTracker confirmationTracker = playerData2.getConfirmationTracker();
        return (int) confirmationTracker.getTransactionPingMillis();
    }

    @Override
    public int getTotalViolations(Player player2) {
        return this.getPlayerData(player2).getTotalViolations();
    }

    @Override
    public int getCombatViolations(Player player2) {
        return this.getPlayerData(player2).getCombatViolations();
    }

    @Override
    public int getMovementViolations(Player player2) {
        return this.getPlayerData(player2).getMovementViolations();
    }

    @Override
    public int getPlayerViolations(Player player2) {
        return this.getPlayerData(player2).getPlayerViolations();
    }

    @Override
    public double getTps() {
        return ExemptionManager.getTps();
    }

    @Override
    public int getTicks() {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        return tickProcessor.getTotalTicks();
    }

    @Override
    public int getJoinTicks(Player player2) {
        return this.getPlayerData(player2).getJoinTicks();
    }

    @Override
    public String getVulcanVersion() {
        return PluginServices.INSTANCE.getVulcanPlugin().getDescription().getVersion();
    }

    @Override
    public Check getCheck(Player player, String string, char c) {
        Player player2 = player;
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player2);
        if (playerData == null) {
            return null;
        }
        for (AbstractCheck abstractCheck : playerData.getChecks()) {
            if (!abstractCheck.getName().equals(string) || !Character.toString(abstractCheck.getType()).equals(Character.toString(c)))
                continue;
            return abstractCheck;
        }
        return null;
    }

    @Override
    public Map getCheckData() {
        return VulcanSettings.checkDataByName;
    }

    @Override
    public boolean hasAlertsEnabled(Player player2) {
        return PluginServices.INSTANCE.getAlertManager().getAlertStaff().contains(player2);
    }

    @Override
    public boolean isCheckEnabled(String text) {
        for (Constructor constructor : CheckManager.constructors) {
            if (constructor.getClass().getSimpleName().equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getServerVersion() {
        return ExemptionManager.getServerVersion().toString();
    }

    @Override
    public Set getChecks() {
        return VulcanSettings.checkEnabled.keySet();
    }

    @Override
    public Map getEnabledChecks() {
        return VulcanSettings.checkEnabled;
    }

    @Override
    public Map getMaxViolations() {
        return VulcanSettings.maximumViolations;
    }

    @Override
    public Map getAlertIntervals() {
        return VulcanSettings.alertIntervals;
    }

    @Override
    public Map getMinimumViolationsToNotify() {
        return VulcanSettings.minimumAlertViolations;
    }

    @Override
    public Map getPunishmentCommands() {
        return VulcanSettings.punishmentCommands;
    }

    @Override
    public Map getPunishableChecks() {
        return VulcanSettings.checkPunishable;
    }

    @Override
    public Map getBroadcastPunishments() {
        return VulcanSettings.broadcastPunishments;
    }

    @Override
    public Map getMaximumPings() {
        return VulcanSettings.maximumPing;
    }

    @Override
    public Map getMinimumTps() {
        return VulcanSettings.minimumTps;
    }

    @Override
    public Map getMaxBuffers() {
        return VulcanSettings.maximumBuffers;
    }

    @Override
    public Map getBufferDecays() {
        return VulcanSettings.bufferDecays;
    }

    @Override
    public Map getBufferMultiples() {
        return VulcanSettings.bufferMultipliers;
    }

    @Override
    public Map getHotbarShuffle() {
        return VulcanSettings.hotbarShuffleEnabled;
    }

    @Override
    public Map getHotbarShuffleMinimums() {
        return VulcanSettings.minimumShuffleViolations;
    }

    @Override
    public Map getHotbarShuffleIntervals() {
        return VulcanSettings.shuffleIntervals;
    }

    @Override
    public Map getRandomRotation() {
        return VulcanSettings.randomRotationEnabled;
    }

    @Override
    public Map getRandomRotationMinimums() {
        return VulcanSettings.minimumRotationViolations;
    }

    @Override
    public Map getRandomRotationIntervals() {
        return VulcanSettings.rotationIntervals;
    }

    @Override
    public List getChecks(Player player2) {
        PlayerData playerData2 = (PlayerData) this.getPlayerData(player2);
        return playerData2.getChecks().stream().map(VulcanSpigotAPI::asCheck).collect(Collectors.toList());
    }

    @Override
    public void toggleAlerts(Player player2) {
        PluginServices.INSTANCE.getAlertManager().toggleAlerts(player2);
    }

    @Override
    public void toggleVerbose(Player player2) {
        PluginServices.INSTANCE.getAlertManager().toggleVerbose(player2);
    }

    @Override
    public void flag(Player player2, String text, String text2, String text3) {
        AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.sendApiAlert(player2, text, text2, text3);
    }

    private static Check asCheck(AbstractCheck abstractCheck2) {
        return (Check) abstractCheck2;
    }
}
