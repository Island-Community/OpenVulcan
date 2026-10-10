package me.frep.vulcan.spigot.player.session;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.frep.vulcan.api.data.IPlayerData;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.collection.list.EvictingList;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.settings.PlayerToggles;
import me.frep.vulcan.spigot.tracker.confirmation.ConfirmationTracker;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;
import me.frep.vulcan.spigot.tracker.entity.EntityTracker;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.tracker.vehicle.VehicleTracker;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;
import me.frep.vulcan.spigot.tracker.world.WorldBlockTracker;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import org.bukkit.entity.Player;

public class PlayerData implements IPlayerData {

    private final User user;

    @Nullable
    private Player player;

    private String clientBrand = "Unresolved";

    private int entityId = -42069;

    private UUID uniqueId = null;

    private ClientVersion clientVersion = ClientVersion.UNKNOWN;

    private String playerName = "Unresolved";

    private boolean clientBrandResolved = false;

    private boolean mitigationPending = false;

    private boolean handshakeComplete = false;

    private boolean connectionKickQueued = false;

    private int totalViolations;

    private int combatViolations;

    private int movementViolations;

    private int playerViolations;

    private int autoClickerViolations;

    private int scaffoldViolations;

    private int timerViolations;

    private final long connectedAt;

    private final int joinedAtTick;

    private long lastClientBrandAlert;

    private long lastPunishmentAtMillis;

    private WrapperPlayClientPlayerFlying lastFlyingPacket;

    private WrapperPlayClientInteractEntity lastInteractionPacket;

    private WrapperPlayClientPlayerDigging lastDiggingPacket;

    private WrapperPlayClientEntityAction lastEntityActionPacket;

    private WrapperPlayClientHeldItemChange lastHeldItemPacket;

    private WrapperPlayClientPlayerBlockPlacement lastPlacementPacket;

    private int queuedTransactionCount;

    private List<AbstractCheck> checks;

    private final VehicleTracker vehicleTracker;

    private final WorldBlockTracker worldBlockTracker;

    private final EntityTracker entityTracker;

    private final PlayerToggles playerToggles;

    private final RelativeMoveTracker relativeMoveTracker;

    private final MotionState motionState;

    private final ClickTimingData clickTimingData;

    private final CollisionState collisionState;

    private final RotationTracker rotationTracker;

    private final VelocityTracker velocityTracker;

    private final ConnectionTracker connectionTracker;

    private final ConfirmationTracker confirmationTracker;

    private final EvictingList repliedConfirmationIds;

    private final EvictingList rawConfirmationIds;

    private long lastCancelHitsAtMillis;

    public PlayerData(User user2) {
        this.connectedAt = System.currentTimeMillis();
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        this.joinedAtTick = tickProcessor.getTotalTicks();
        this.queuedTransactionCount = 0;
        this.checks = new ArrayList();
        this.vehicleTracker = new VehicleTracker(this);
        this.worldBlockTracker = new WorldBlockTracker(this);
        this.entityTracker = new EntityTracker(this);
        this.playerToggles = new PlayerToggles(this);
        this.relativeMoveTracker = new RelativeMoveTracker(this);
        this.motionState = new MotionState(this);
        this.clickTimingData = new ClickTimingData(this);
        this.collisionState = new CollisionState(this);
        this.rotationTracker = new RotationTracker(this);
        this.velocityTracker = new VelocityTracker(this);
        this.connectionTracker = new ConnectionTracker(this);
        this.confirmationTracker = new ConfirmationTracker(this);
        this.repliedConfirmationIds = new EvictingList(100);
        this.rawConfirmationIds = new EvictingList(100);
        this.lastCancelHitsAtMillis = -1L;
        this.user = user2;
    }

    public boolean isAtLeast1_17() {
        return CheckUtils.isAtLeast1_17(this);
    }

    public boolean isAtLeast1_13() {
        return CheckUtils.isAtLeast1_13(this);
    }

    public boolean isNewerThan1_9() {
        return CheckUtils.isNewerThan1_9(this);
    }

    public boolean isOlderThan1_9() {
        return this.clientVersion.isOlderThan(ClientVersion.V_1_9);
    }

    public boolean isAtLeast1_21_2() {
        if ((this.clientVersion.isNewerThanOrEquals(ClientVersion.V_1_21_2)) && (ExemptionManager.isAtLeast1_21_2())) {
            return true;
        }
        return false;
    }

    public List<String> getExemptionNames() {
        ArrayList<java.lang.String> arrayList = new ArrayList();
        for (ExemptType exemptType : ExemptType.values()) {
            if (this.playerToggles.isExempt(exemptType)) {
                arrayList.add(exemptType.toString());
            }
        }
        return arrayList;
    }

    public void update() {
        if (this.player == null) {
            return;
        }
        String brand = PluginServices.INSTANCE.getClientBrands().get(this.player.getUniqueId());
        if (brand == null) {
            return;
        }
        if (this.clientBrand.length() > 55 || this.clientBrand.contains("\n") || this.clientBrand.contains("&")) {
            this.player.sendMessage(ChatColorUtils.colorize("&cInvalid client brand."));
            MainThreadExecutor.execute(() -> this.kickInvalidBrand());
            return;
        }
        if (this.clientBrand.contains("%") || this.clientBrand.contains("$")) {
            MainThreadExecutor.execute(() -> this.kickMalformedBrand());
            return;
        }
        if (this.clientBrandResolved || System.currentTimeMillis() - this.lastClientBrandAlert < 10000L) {
            return;
        }
        this.setClientBrand(brand);
        if (!this.player.hasPermission("vulcan.bypass.client-brand.blacklist")) {
            for (String value : VulcanSettings.clientBrandAlertsBlockedClientBrands) {
                if (!this.clientBrand.toLowerCase().contains((value).toLowerCase()))
                    continue;
                VulcanSettings.clientBrandAlertsUnallowedClientBrandKickMessage.replaceAll("%client-brand%", this.clientBrand);
                this.player.sendMessage(ChatColorUtils.colorize(VulcanSettings.clientBrandAlertsUnallowedClientBrandKickMessage.replaceAll("%client-brand%", this.clientBrand)));
                MainThreadExecutor.execute(() -> this.kickBlockedBrand());
            }
        }
        if (VulcanSettings.clientBrandAlertsWhitelistEnabled) {
            if (!(this.player.hasPermission("vulcan.bypass.client-brand.whitelist"))) {
                boolean condition = false;
                for (String string2 : VulcanSettings.clientBrandAlertsWhitelistedClientBrands) {
                    if (!this.clientBrand.toLowerCase().contains(string2.toLowerCase()))
                        continue;
                    condition = true;
                    break;
                }
                if (!condition) {
                    MainThreadExecutor.execute(() -> this.notifyAndKickBlockedBrand());
                }
            }
        }
        if (VulcanSettings.clientBrandAlertsEnabled) {
            boolean ignoredBrand = false;
            for (String value2 : VulcanSettings.clientBrandAlertsIgnoreAlertsList) {
                if (!this.clientBrand.toLowerCase().contains((value2).toLowerCase()))
                    continue;
                ignoredBrand = true;
                break;
            }
            if (!ignoredBrand && System.currentTimeMillis() - this.lastClientBrandAlert >= 10000L) {
                if (VulcanSettings.clientBrandAlertsConsole) {
                    VulcanSettings.clientBrandAlertsConsoleMessage.replaceAll("%player%", this.player.getName()).replaceAll("%client-version%", this.user.getClientVersion().getReleaseName()).replaceAll("%client-brand%", this.clientBrand);
                    Object text = ChatColorUtils.colorize(VulcanSettings.clientBrandAlertsConsoleMessage.replaceAll("%player%", this.player.getName()).replaceAll("%client-version%", this.user.getClientVersion().getReleaseName()).replaceAll("%client-brand%", this.clientBrand));
                    ExemptionManager.logColored(ChatColorUtils.colorize(VulcanSettings.clientBrandAlertsConsoleMessage.replaceAll("%player%", this.player.getName()).replaceAll("%client-version%", this.user.getClientVersion().getReleaseName()).replaceAll("%client-brand%", this.clientBrand)));
                }
                if (!this.player.hasPermission("vulcan.bypass.client-brand-alerts")) {
                    if (PluginServices.INSTANCE.isPlaceholderApiEnabled()) {
                        String value3;
                        java.lang.String text2 = VulcanSettings.clientBrandAlertsMessage.replaceAll("%player%", this.player.getName()).replaceAll("%client-version%", this.user.getClientVersion().getReleaseName()).replaceAll("%client-brand%", this.clientBrand);
                        value3 = text2 = PlaceholderAPI.setPlaceholders(this.player, text2);
                        MainThreadExecutor.execute(() -> PlayerData.broadcastBrandMessage(value3));
                    } else {
                        MainThreadExecutor.execute(() -> this.broadcastClientBrand());
                    }
                    System.currentTimeMillis();
                    this.setLastClientBrandAlert(System.currentTimeMillis());
                }
            }
        }
        this.setClientBrandResolved(true);
        PluginServices.INSTANCE.getClientBrands().remove(this.player.getUniqueId());
    }

    public User getUser() {
        return this.user;
    }

    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String getClientBrand() {
        return this.clientBrand;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public ClientVersion getClientVersion() {
        return this.clientVersion;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public boolean isClientBrandResolved() {
        return this.clientBrandResolved;
    }

    public boolean isMitigationPending() {
        return this.mitigationPending;
    }

    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    public boolean isConnectionKickQueued() {
        return this.connectionKickQueued;
    }

    @Override
    public int getTotalViolations() {
        return this.totalViolations;
    }

    @Override
    public int getCombatViolations() {
        return this.combatViolations;
    }

    @Override
    public int getMovementViolations() {
        return this.movementViolations;
    }

    @Override
    public int getPlayerViolations() {
        return this.playerViolations;
    }

    @Override
    public int getAutoClickerViolations() {
        return this.autoClickerViolations;
    }

    @Override
    public int getScaffoldViolations() {
        return this.scaffoldViolations;
    }

    @Override
    public int getTimerViolations() {
        return this.timerViolations;
    }

    @Override
    public long getJoinTime() {
        return this.connectedAt;
    }

    @Override
    public int getJoinTicks() {
        return this.joinedAtTick;
    }

    @Override
    public long getLastClientBrandAlert() {
        return this.lastClientBrandAlert;
    }

    public long getLastPunishmentAtMillis() {
        return this.lastPunishmentAtMillis;
    }

    public WrapperPlayClientPlayerFlying getLastFlyingPacket() {
        return this.lastFlyingPacket;
    }

    public WrapperPlayClientInteractEntity getLastInteractionPacket() {
        return this.lastInteractionPacket;
    }

    public WrapperPlayClientPlayerDigging getLastDiggingPacket() {
        return this.lastDiggingPacket;
    }

    public WrapperPlayClientEntityAction getLastEntityActionPacket() {
        return this.lastEntityActionPacket;
    }

    public WrapperPlayClientHeldItemChange getLastHeldItemPacket() {
        return this.lastHeldItemPacket;
    }

    public WrapperPlayClientPlayerBlockPlacement getLastPlacementPacket() {
        return this.lastPlacementPacket;
    }

    public int getQueuedTransactionCount() {
        return this.queuedTransactionCount;
    }

    public List<AbstractCheck> getChecks() {
        return this.checks;
    }

    public VehicleTracker getVehicleTracker() {
        return this.vehicleTracker;
    }

    public WorldBlockTracker getWorldBlockTracker() {
        return this.worldBlockTracker;
    }

    public EntityTracker getEntityTracker() {
        return this.entityTracker;
    }

    public PlayerToggles getPlayerToggles() {
        return this.playerToggles;
    }

    public RelativeMoveTracker getRelativeMoveTracker() {
        return this.relativeMoveTracker;
    }

    public MotionState getMotionState() {
        return this.motionState;
    }

    public ClickTimingData getClickTimingData() {
        return this.clickTimingData;
    }

    public CollisionState getCollisionState() {
        return this.collisionState;
    }

    public RotationTracker getRotationTracker() {
        return this.rotationTracker;
    }

    public VelocityTracker getVelocityTracker() {
        return this.velocityTracker;
    }

    public ConnectionTracker getConnectionTracker() {
        return this.connectionTracker;
    }

    public ConfirmationTracker getConfirmationTracker() {
        return this.confirmationTracker;
    }

    public EvictingList getRepliedConfirmationIds() {
        return this.repliedConfirmationIds;
    }

    public EvictingList getRawConfirmationIds() {
        return this.rawConfirmationIds;
    }

    public long getLastCancelHitsAtMillis() {
        return this.lastCancelHitsAtMillis;
    }

    public void setPlayer(@Nullable Player player2) {
        this.player = player2;
    }

    public void setClientBrand(String clientBrand2) {
        this.clientBrand = clientBrand2;
    }

    public void setEntityId(int entityId2) {
        this.entityId = entityId2;
    }

    public void setUniqueId(UUID uniqueId2) {
        this.uniqueId = uniqueId2;
    }

    public void setClientVersion(ClientVersion clientVersion2) {
        this.clientVersion = clientVersion2;
    }

    public void setPlayerName(String playerName2) {
        this.playerName = playerName2;
    }

    public void setClientBrandResolved(boolean clientBrandResolved2) {
        this.clientBrandResolved = clientBrandResolved2;
    }

    public void setMitigationPending(boolean mitigationPending2) {
        this.mitigationPending = mitigationPending2;
    }

    public void setHandshakeComplete(boolean handshakeComplete2) {
        this.handshakeComplete = handshakeComplete2;
    }

    public void setConnectionKickQueued(boolean connectionKickQueued2) {
        this.connectionKickQueued = connectionKickQueued2;
    }

    public void setTotalViolations(int totalViolations2) {
        this.totalViolations = totalViolations2;
    }

    public void setCombatViolations(int combatViolations2) {
        this.combatViolations = combatViolations2;
    }

    public void setMovementViolations(int movementViolations2) {
        this.movementViolations = movementViolations2;
    }

    public void setPlayerViolations(int playerViolations2) {
        this.playerViolations = playerViolations2;
    }

    public void setAutoClickerViolations(int autoClickerViolations2) {
        this.autoClickerViolations = autoClickerViolations2;
    }

    public void setScaffoldViolations(int scaffoldViolations2) {
        this.scaffoldViolations = scaffoldViolations2;
    }

    public void setTimerViolations(int timerViolations2) {
        this.timerViolations = timerViolations2;
    }

    public void setLastClientBrandAlert(long lastClientBrandAlert2) {
        this.lastClientBrandAlert = lastClientBrandAlert2;
    }

    public void clearPunishmentCooldown() {
        this.lastPunishmentAtMillis = 0L;
    }

    public void setLastFlyingPacket(WrapperPlayClientPlayerFlying lastFlyingPacket2) {
        this.lastFlyingPacket = lastFlyingPacket2;
    }

    public void setLastInteractionPacket(WrapperPlayClientInteractEntity lastInteractionPacket2) {
        this.lastInteractionPacket = lastInteractionPacket2;
    }

    public void setLastDiggingPacket(WrapperPlayClientPlayerDigging lastDiggingPacket2) {
        this.lastDiggingPacket = lastDiggingPacket2;
    }

    public void setLastEntityActionPacket(WrapperPlayClientEntityAction lastEntityActionPacket2) {
        this.lastEntityActionPacket = lastEntityActionPacket2;
    }

    public void setLastHeldItemPacket(WrapperPlayClientHeldItemChange lastHeldItemPacket2) {
        this.lastHeldItemPacket = lastHeldItemPacket2;
    }

    public void setLastPlacementPacket(WrapperPlayClientPlayerBlockPlacement lastPlacementPacket2) {
        this.lastPlacementPacket = lastPlacementPacket2;
    }

    public void setQueuedTransactionCount(int queuedTransactionCount2) {
        this.queuedTransactionCount = queuedTransactionCount2;
    }

    public void setChecks(List<AbstractCheck> checks2) {
        this.checks = checks2;
    }

    public void clearCancelHitsCooldown() {
        this.lastCancelHitsAtMillis = 0L;
    }

    private void broadcastClientBrand() {
        AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.broadcastAlert(VulcanSettings.clientBrandAlertsMessage.replaceAll("%player%", this.player.getName()).replaceAll("%client-version%", this.user.getClientVersion().getReleaseName()).replaceAll("%client-brand%", this.clientBrand));
    }

    private static void broadcastBrandMessage(String text) {
        AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.broadcastAlert(text);
    }

    private void notifyAndKickBlockedBrand() {
        Player player2 = this.player;
        player2.sendMessage(ChatColorUtils.colorize(VulcanSettings.clientBrandAlertsUnallowedClientBrandKickMessage.replaceAll("%client-brand%", this.clientBrand)));
        player2 = this.player;
        player2.kickPlayer(ChatColorUtils.colorize(VulcanSettings.clientBrandAlertsUnallowedClientBrandKickMessage.replaceAll("%client-brand%", this.clientBrand)));
    }

    private void kickBlockedBrand() {
        Player player2 = this.player;
        player2.kickPlayer(ChatColorUtils.colorize(VulcanSettings.clientBrandAlertsUnallowedClientBrandKickMessage.replaceAll("%client-brand%", this.clientBrand)));
    }

    private void kickMalformedBrand() {
        this.player.kickPlayer(ChatColorUtils.colorize("&cInvalid client brand."));
    }

    private void kickInvalidBrand() {
        this.player.kickPlayer(ChatColorUtils.colorize("&cInvalid client brand."));
    }
}
