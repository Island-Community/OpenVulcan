package me.frep.vulcan.spigot.task;

import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.StatisticsFile;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.punishment.JudgementDay;
import me.frep.vulcan.spigot.util.scheduling.ScheduledTask;

public class TickProcessor implements Runnable {

    private int totalTicks = 0;

    private int lagSpikes = 0;

    private static ScheduledTask scheduledTask;

    private long lastTickAtMillis = Long.MAX_VALUE;

    private long tickDurationMillis = Long.MAX_VALUE;

    private long lastLagSpikeAtMillis = Long.MAX_VALUE;

    static final public boolean flag = !TickProcessor.class.desiredAssertionStatus();

    public void start() {
        if ((!flag) && (scheduledTask != null)) {
            throw new AssertionError((Object) "TickProcessor has already been started!");
        }
        scheduledTask = PluginServices.getScheduler().runTaskTimer(this, 0L, 1L);
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }

    @Override
    public void run() {
        ++this.totalTicks;
        for (PlayerData playerData : PluginServices.INSTANCE.getPlayerDataStore().getPlayers()) {
            playerData.getConfirmationTracker().sendConfirmation(true);
        }
        if (this.totalTicks % 200 == 0) {
            this.lagSpikes = 0;
        }
        if ((VulcanSettings.punishmentStatisticsBroadcastEnabled) && (this.totalTicks % VulcanSettings.punishmentStatisticsBroadcastInterval == 0)) {
            VulcanSettings.punishmentStatisticsBroadcastBroadcast.forEach(text -> TickProcessor.broadcastPunishmentCount(text));
        }
        if ((VulcanSettings.judgementDaysRunAtInterval) && (this.totalTicks % VulcanSettings.judgementDaysInterval == 0)) {
            JudgementDay.execute();
        }
        if (VulcanSettings.discordWebhookCooldown && this.totalTicks % VulcanSettings.discordWebhookCooldownResetTicks == 0) {
            PluginServices.INSTANCE.setPunishmentsInWindow(0);
        }
        long lastTickAtMillis2 = System.currentTimeMillis();
        this.tickDurationMillis = lastTickAtMillis2 - this.lastTickAtMillis;
        this.lastTickAtMillis = lastTickAtMillis2;
        if (!(this.totalTicks <= 500)) {
            if (!(this.tickDurationMillis > 90L)) {
                if (!(this.tickDurationMillis >= 10L)) {
                    ++this.lagSpikes;
                    this.lastLagSpikeAtMillis = System.currentTimeMillis();
                    if (VulcanSettings.settingsTestingStuff) {
                        String adjustedAdjustedAdjustedTickDurationMillis = null;
                        adjustedAdjustedAdjustedTickDurationMillis = "Potential lag spike detected! delay=" + this.tickDurationMillis + " spikes=" + this.lagSpikes;
                        ExemptionManager.logInfo(adjustedAdjustedAdjustedTickDurationMillis);
                    }
                }
            } else {
                ++this.lagSpikes;
                this.lastLagSpikeAtMillis = System.currentTimeMillis();
                if (VulcanSettings.settingsTestingStuff) {
                    String adjustedAdjustedAdjustedTickDurationMillis2 = null;
                    adjustedAdjustedAdjustedTickDurationMillis2 = "Potential lag spike detected! delay=" + this.tickDurationMillis + " spikes=" + this.lagSpikes;
                    ExemptionManager.logInfo(adjustedAdjustedAdjustedTickDurationMillis2);
                }
            }
        }
    }

    public int getTotalTicks() {
        return this.totalTicks;
    }

    public int getLagSpikes() {
        return this.lagSpikes;
    }

    public long getLastTickAtMillis() {
        return this.lastTickAtMillis;
    }

    public long getTickDurationMillis() {
        return this.tickDurationMillis;
    }

    public long getLastLagSpikeAtMillis() {
        return this.lastLagSpikeAtMillis;
    }

    private static void broadcastPunishmentCount(String text) {
        ExemptionManager.broadcast(text.replaceAll("%amount%", Integer.toString(StatisticsFile.getPunishmentCount())));
    }
}
