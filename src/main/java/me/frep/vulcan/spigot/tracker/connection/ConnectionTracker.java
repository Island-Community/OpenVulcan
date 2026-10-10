package me.frep.vulcan.spigot.tracker.connection;

import java.util.HashMap;
import java.util.Map;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import org.bukkit.entity.Player;

public class ConnectionTracker {

    private final PlayerData playerData;

    private int keepAlivePing;

    private final Map pendingKeepAlives = new HashMap();

    private final Map confirmedKeepAlives = new HashMap();

    private long lastMovementAtMillis;

    private long movementDelayMillis;

    private long lastPingUpdateAtMillis = System.currentTimeMillis();

    private long lastKeepAliveAtMillis = System.currentTimeMillis();

    private long lastFastMovementAtMillis = System.currentTimeMillis();

    private boolean fastMovement;

    private double keepAliveTimeoutBuffer;

    private boolean keepAliveKickQueued;

    private int highPingTicks;

    private boolean highPingKickQueued;

    public ConnectionTracker(PlayerData data) {
        this.playerData = data;
    }

    public void processMovement() {
        long now = System.currentTimeMillis();
        this.movementDelayMillis = now - this.lastMovementAtMillis;
        this.fastMovement = this.movementDelayMillis <= 5L;
        if (this.fastMovement) {
            this.lastFastMovementAtMillis = System.currentTimeMillis();
        }
        Player player = this.playerData.getPlayer();
        this.keepAlivePing = player == null ? 0 : CheckUtils.getPing(player);
        this.lastMovementAtMillis = now;
        this.checkKeepAliveTimeout();
        this.checkMaxPing();
    }

    public void recordKeepAlive() {
        this.lastKeepAliveAtMillis = System.currentTimeMillis();
    }

    private void checkKeepAliveTimeout() {
        if (this.playerData.getPlayer() == null) {
            return;
        }
        if (!VulcanSettings.connectionKeepaliveKickEnabled || this.playerData.getPlayer().hasPermission("vulcan.bypass.connection.keepalive")) {
            return;
        }
        long delay = System.currentTimeMillis() - this.lastKeepAliveAtMillis;
        if ((delay > VulcanSettings.connectionKeepaliveMaxDelay) && (!this.keepAliveKickQueued)) {
            this.keepAliveTimeoutBuffer += 1.0;
            if (this.keepAliveTimeoutBuffer > 5.0) {
                MainThreadExecutor.execute(() -> this.kickForKeepAliveTimeout());
                if (!VulcanSettings.connectionKeepaliveStaffAlertMessage.equals("")) {
                    PluginServices.INSTANCE.getAlertManager().broadcastAlert(VulcanSettings.connectionKeepaliveStaffAlertMessage.replaceAll("%delay%", Long.toString(delay)).replaceAll("%player%", this.playerData.getPlayerName()));
                }
                if (!VulcanSettings.connectionKeepaliveConsoleMessage.equals("")) {
                    ExemptionManager.logColored(ChatColorUtils.colorize(VulcanSettings.connectionKeepaliveConsoleMessage.replaceAll("%player%", this.playerData.getPlayerName()).replaceAll("%delay%", Long.toString(delay))));
                }
                this.keepAliveKickQueued = true;
            } else if (this.keepAliveTimeoutBuffer > 0.0) {
                this.keepAliveTimeoutBuffer -= 0.1;
            }
        }
    }

    private void checkMaxPing() {
        if (this.playerData.getPlayer() == null) {
            return;
        }
        if (!VulcanSettings.connectionMaxPingKickEnabled)
            return;
        if (this.playerData.getPlayer().hasPermission("vulcan.bypass.connection.max-ping")) {
            return;
        }
        if ((this.keepAlivePing > VulcanSettings.connectionMaxPingMaxPing) && (!this.highPingKickQueued)) {
            if (++this.highPingTicks <= VulcanSettings.connectionMaxPingMaxTicks)
                return;
            MainThreadExecutor.execute(() -> this.kickForHighPing());
            if (!VulcanSettings.connectionMaxPingStaffAlertMessage.equals("")) {
                PluginServices.INSTANCE.getAlertManager().broadcastAlert(VulcanSettings.connectionMaxPingStaffAlertMessage.replaceAll("%ping%", Long.toString(this.keepAlivePing)).replaceAll("%player%", this.playerData.getPlayerName()));
            }
            if (!VulcanSettings.connectionMaxPingConsoleMessage.equals("")) {
                ExemptionManager.logColored(ChatColorUtils.colorize(VulcanSettings.connectionMaxPingConsoleMessage.replaceAll("%player%", this.playerData.getPlayerName()).replaceAll("%ping%", Long.toString(this.keepAlivePing))));
            }
            this.highPingKickQueued = true;
            return;
        }
        if (this.highPingTicks <= 0)
            return;
        --this.highPingTicks;
    }

    public int getKeepAlivePing() {
        return this.keepAlivePing;
    }

    public long getMovementDelayMillis() {
        return this.movementDelayMillis;
    }

    public long getLastFastMovementAtMillis() {
        return this.lastFastMovementAtMillis;
    }

    public boolean isFastMovement() {
        return this.fastMovement;
    }

    private void kickForHighPing() {
        Player player2 = this.playerData.getPlayer();
        String connectionMaxPingKickMessage2 = VulcanSettings.connectionMaxPingKickMessage;
        player2.kickPlayer(ChatColorUtils.colorize(connectionMaxPingKickMessage2));
    }

    private void kickForKeepAliveTimeout() {
        Player player2 = this.playerData.getPlayer();
        String connectionKeepaliveKickMessage2 = VulcanSettings.connectionKeepaliveKickMessage;
        player2.kickPlayer(ChatColorUtils.colorize(connectionKeepaliveKickMessage2));
    }
}
