package me.frep.vulcan.spigot.task;

import me.frep.vulcan.api.event.VulcanViolationResetEvent;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.scheduling.ScheduledTask;
import me.frep.vulcan.spigot.util.scheduling.Scheduler;
import org.bukkit.Bukkit;

public class ViolationResetProcessor implements Runnable {

    private static ScheduledTask scheduledTask;

    static final public boolean flag = !ViolationResetProcessor.class.desiredAssertionStatus();

    public static void resetViolations() {
        if (VulcanSettings.violationResetEnabled) {
            if (VulcanSettings.settingsEnableApi) {
                VulcanViolationResetEvent vulcanViolationResetEvent = new VulcanViolationResetEvent();
                if (ExemptionManager.isNewerThan1_13()) {
                    MainThreadExecutor.execute(() -> Bukkit.getPluginManager().callEvent(vulcanViolationResetEvent));
                } else {
                    Bukkit.getPluginManager().callEvent(vulcanViolationResetEvent);
                }
                if (vulcanViolationResetEvent.isCancelled()) {
                    return;
                }
            }
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            playerDataStore.getPlayers().parallelStream().forEach(ViolationResetProcessor::resetPlayerViolations);
            if (VulcanSettings.violationResetMessageEnabled) {
                AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
                String violationResetMessage2 = VulcanSettings.violationResetMessage;
                alertManager.broadcastAlert(violationResetMessage2);
            }
        }
    }

    public void start() {
        if (!flag && scheduledTask != null) {
            throw new AssertionError("ResetProcessor has already been started!");
        }
        int value = VulcanSettings.getInt("violation-reset.interval-in-minutes");
        Scheduler scheduler = PluginServices.getScheduler();
        scheduledTask = scheduler.runTaskTimerAsynchronously(this, value * 1200L, value * 1200L);
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }

    @Override
    public void run() {
        resetViolations();
    }

    private static void resetPlayerViolations(PlayerData playerData) {
        playerData.setTotalViolations(0);
        playerData.setCombatViolations(0);
        playerData.setMovementViolations(0);
        playerData.setPlayerViolations(0);
        playerData.setAutoClickerViolations(0);
        playerData.setScaffoldViolations(0);
        playerData.setTimerViolations(0);
        playerData.getChecks().forEach(ViolationResetProcessor::resetCheckViolations);
    }

    private static void resetCheckViolations(AbstractCheck abstractCheck) {
        abstractCheck.setVl(0);
        abstractCheck.setBuffer();
    }
}
