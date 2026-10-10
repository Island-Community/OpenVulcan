package me.frep.vulcan.spigot.check;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import java.util.Objects;
import me.frep.vulcan.api.check.Check;
import me.frep.vulcan.api.event.VulcanSetbackEvent;
import me.frep.vulcan.spigot.alert.AlertManager;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.punishment.PunishmentHandler;
import me.frep.vulcan.spigot.task.TickProcessor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class AbstractCheck implements Check {

    protected final PlayerData playerData;

    private int violations;

    private double buffer;

    public final String checkName;

    public final double maximumBuffer;

    public final double bufferDecay;

    public final double bufferMultiplier;

    public AbstractCheck(PlayerData playerData2) {
        this.checkName = this.getClass().getSimpleName();
        this.maximumBuffer = ((Integer) VulcanSettings.maximumBuffers.get(this.checkName)).intValue();
        this.bufferDecay = (Double) VulcanSettings.bufferDecays.get(this.checkName);
        this.bufferMultiplier = (Double) VulcanSettings.bufferMultipliers.get(this.checkName);
        this.playerData = playerData2;
    }

    public void fail(Object value) {
        Object value2 = value;
        if (this.playerData.getPlayer() == null) {
            return;
        }
        if ((((Boolean) VulcanSettings.setbackEnabled.get(this.getCheckKey())).booleanValue()) && (this.getVl() > (Integer) VulcanSettings.minimumSetbackViolations.get(this.getCheckKey()))) {
            if (VulcanSettings.settingsEnableApi) {
                VulcanSetbackEvent vulcanSetbackEvent = new VulcanSetbackEvent(this.playerData.getPlayer(), this);
                Bukkit.getPluginManager().callEvent((Event) vulcanSetbackEvent);
                if (!vulcanSetbackEvent.isCancelled()) {
                    this.playerData.getCollisionState().requestSetback();
                }
            } else if (!this.playerData.getPlayer().hasPermission("vulcan.bypass.setback." + this.getCheckKey().toLowerCase())) {
                this.playerData.getCollisionState().requestSetback();
            }
        }
        this.applyConfiguredMitigation();
        this.applyConfiguredCommands();
        this.multiplyBuffer(this.bufferMultiplier);
        PluginServices.INSTANCE.getViolationExecutor().execute(() -> this.reportViolationWithDetails(value2));
    }

    public void fail() {
        if (this.playerData.getPlayer() == null) {
            return;
        }
        if ((((Boolean) VulcanSettings.setbackEnabled.get(this.getCheckKey())).booleanValue()) && (this.getVl() > (Integer) VulcanSettings.minimumSetbackViolations.get(this.getCheckKey()))) {
            if (VulcanSettings.settingsEnableApi) {
                VulcanSetbackEvent vulcanSetbackEvent = new VulcanSetbackEvent(this.playerData.getPlayer(), this);
                Bukkit.getPluginManager().callEvent((Event) vulcanSetbackEvent);
                if (!vulcanSetbackEvent.isCancelled()) {
                    this.playerData.getCollisionState().requestSetback();
                }
            } else if (!this.playerData.getPlayer().hasPermission("vulcan.bypass.setback." + this.getCheckKey().toLowerCase())) {
                this.playerData.getCollisionState().requestSetback();
            }
        }
        this.applyConfiguredMitigation();
        this.applyConfiguredCommands();
        this.multiplyBuffer(this.bufferMultiplier);
        PluginServices.INSTANCE.getViolationExecutor().execute(() -> this.reportViolation());
    }

    public void update() {
        PluginServices.INSTANCE.getViolationExecutor().execute(() -> this.executePunishment());
    }

    protected boolean isExempt(ExemptType[] exemptions) {
        return playerData.getPlayerToggles().isAnyExempt(exemptions);
    }

    public int getCurrentTick() {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        return tickProcessor.getTotalTicks();
    }

    public double increaseBuffer() {
        return this.buffer = Math.min(10000.0, this.buffer + 1.0);
    }

    public double decreaseBuffer(double amount) {
        return this.buffer = Math.max(0.0, this.buffer - amount);
    }

    public double increaseBuffer(double amount) {
        return this.buffer = Math.min(10000.0, this.buffer + amount);
    }

    public void setBuffer() {
        this.buffer = 0.0;
    }

    public void multiplyBuffer(double amount) {
        this.buffer *= amount;
    }

    public void decayBuffer() {
        this.decreaseBuffer(this.bufferDecay);
    }

    public int getAttackTicks() {
        return this.playerData.getRelativeMoveTracker().getTicksSinceAttack();
    }

    public boolean hasRecentAttack(int ticks) {
        if (this.playerData.getRelativeMoveTracker().getTicksSinceAttack() < ticks && this.playerData.getRelativeMoveTracker().getTicksSinceAttackGiven() < ticks * 6) {
            return true;
        }
        return false;
    }

    public CheckInfo getCheck() {
        CheckInfo info = getClass().getAnnotation(CheckInfo.class);
        if (info == null)
            throw new IllegalStateException("Missing CheckInfo on " + checkName);
        return info;
    }

    public void debug(Object value) {
        ExemptionManager.debug(value);
    }

    @Override
    public String getCategory() {
        String packageName = getClass().getPackageName();
        if (packageName.contains(".combat."))
            return "combat";
        if (packageName.contains(".movement."))
            return "movement";
        if (packageName.contains(".player."))
            return "player";
        throw new IllegalStateException("Unknown check category: " + packageName);
    }

    public boolean isTeleporting() {
        return this.playerData.getMotionState().isTeleporting();
    }

    public boolean isSetbackPending() {
        return this.playerData.getCollisionState().isSetbackPending();
    }

    @Override
    public String getName() {
        return this.getCheck().name().toLowerCase().replaceAll(" ", "");
    }

    @Override
    public char getType() {
        return Character.toLowerCase(this.getCheck().type());
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int getVl() {
        return this.violations;
    }

    @Override
    public int getMaxVl() {
        return (Integer) VulcanSettings.maximumViolations.get(this.checkName);
    }

    @Override
    public int getMinimumVlToNotify() {
        return (Integer) VulcanSettings.minimumAlertViolations.get(this.checkName);
    }

    @Override
    public double getMaxBuffer() {
        return this.maximumBuffer;
    }

    @Override
    public String getDescription() {
        return this.getCheck().description();
    }

    @Override
    public String getComplexType() {
        return this.getCheck().complexType();
    }

    @Override
    public double getBufferDecay() {
        return this.bufferDecay;
    }

    @Override
    public int getAlertInterval() {
        return (Integer) VulcanSettings.alertIntervals.get(this.checkName);
    }

    @Override
    public double getBufferMultiple() {
        return this.bufferMultiplier;
    }

    @Override
    public boolean isExperimental() {
        return this.getCheck().experimental();
    }

    @Override
    public boolean isPunishable() {
        return (Boolean) VulcanSettings.checkPunishable.get(this.checkName);
    }

    @Override
    public String getDisplayName() {
        return this.getCheck().name();
    }

    @Override
    public char getDisplayType() {
        return this.getCheck().type();
    }

    public void applyConfiguredMitigation() {
        if (this.playerData != null) {
            PlayerData playerData2 = this.playerData;
            if (playerData2.getPlayer() != null) {
                boolean condition = (Boolean) VulcanSettings.hotbarShuffleEnabled.get(this.checkName);
                int index = (Integer) VulcanSettings.minimumShuffleViolations.get(this.checkName);
                int index2 = (Integer) VulcanSettings.shuffleIntervals.get(this.checkName);
                if (condition && this.violations >= index && this.violations % index2 == 0) {
                    playerData2 = this.playerData;
                    CheckUtils.sendHotbarShuffle(playerData2.getPlayer());
                }
                return;
            }
        }
    }

    public void applyConfiguredCommands() {
        if (this.playerData.getPlayer() != null) {
            boolean condition = (Boolean) VulcanSettings.randomRotationEnabled.get(this.checkName);
            int index = (Integer) VulcanSettings.minimumRotationViolations.get(this.checkName);
            int index2 = (Integer) VulcanSettings.rotationIntervals.get(this.checkName);
            if (condition && this.violations >= index && this.violations % index2 == 0) {
                CheckUtils.rotatePlayerRandomly(this.playerData);
            }
        }
    }

    public boolean isRotationUpdatePacket(PacketEvent packetEvent) {
        return !(packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent) ? false : packetPlayReceiveEvent.getPacketType() == Client.PLAYER_ROTATION || packetPlayReceiveEvent.getPacketType() == Client.PLAYER_POSITION_AND_ROTATION;
    }

    public boolean isAttackPacket(PacketEvent event) {
        return event instanceof PacketPlayReceiveEvent packet && (packet.getPacketType() == Client.INTERACT_ENTITY || packet.getPacketType() == Client.ATTACK);
    }

    public boolean isMovementPacket(PacketEvent packetEvent) {
        return !(packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent) ? false : packetPlayReceiveEvent.getPacketType() == Client.PLAYER_FLYING || packetPlayReceiveEvent.getPacketType() == Client.PLAYER_POSITION || packetPlayReceiveEvent.getPacketType() == Client.PLAYER_ROTATION || packetPlayReceiveEvent.getPacketType() == Client.PLAYER_POSITION_AND_ROTATION;
    }

    public boolean isBlockPlacementPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.PLAYER_BLOCK_PLACEMENT : false;
    }

    public boolean isUseItemPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.USE_ITEM : false;
    }

    public boolean isPositionUpdatePacket(PacketEvent packetEvent) {
        if (!(packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent)) {
            return false;
        }
        Client packetType = packetPlayReceiveEvent.getPacketType();
        return packetType == Client.PLAYER_POSITION || packetType == Client.PLAYER_POSITION_AND_ROTATION;
    }

    public boolean isArmAnimationPacket(PacketEvent packetEvent) {
        return !(packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent) ? false : packetPlayReceiveEvent.getPacketType() == Client.ANIMATION || packetPlayReceiveEvent.getPacketType() == Client.PUNCH;
    }

    public boolean isVehicleMovePacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.VEHICLE_MOVE : false;
    }

    public boolean isEntityActionPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.ENTITY_ACTION : false;
    }

    public boolean isDiggingPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.PLAYER_DIGGING : false;
    }

    public boolean isAbilitiesPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.PLAYER_ABILITIES : false;
    }

    public boolean isSteerVehiclePacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.STEER_VEHICLE : false;
    }

    public boolean isClientStatusPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.CLIENT_STATUS : false;
    }

    public boolean isHeldItemChangePacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.HELD_ITEM_CHANGE : false;
    }

    public boolean isKeepAlivePacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.KEEP_ALIVE : false;
    }

    public boolean isClickWindowPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.CLICK_WINDOW : false;
    }

    public boolean isSpectatePacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.SPECTATE : false;
    }

    public boolean isServerPositionPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlaySendEvent packetPlaySendEvent ? packetPlaySendEvent.getPacketType() == Server.PLAYER_POSITION_AND_LOOK : false;
    }

    public boolean isClientTickEndPacket(PacketEvent packetEvent) {
        return packetEvent instanceof PacketPlayReceiveEvent packetPlayReceiveEvent ? packetPlayReceiveEvent.getPacketType() == Client.CLIENT_TICK_END : false;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    @Override
    public double getBuffer() {
        return this.buffer;
    }

    public String getCheckKey() {
        return this.checkName;
    }

    public double getBufferThreshold() {
        return this.maximumBuffer;
    }

    public double getConfiguredBufferDecay() {
        return this.bufferDecay;
    }

    public double getConfiguredBufferMultiplier() {
        return this.bufferMultiplier;
    }

    @Override
    public void setVl(int violations2) {
        this.violations = violations2;
    }

    @Override
    public void setBuffer(double buffer2) {
        this.buffer = buffer2;
    }

    private void executePunishment() {
        PunishmentHandler punishmentHandler = PluginServices.INSTANCE.getPunishmentHandler();
        punishmentHandler.punish(this, this.playerData);
    }

    private void reportViolation() {
        AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.processViolation(this, this.playerData, "");
    }

    private void reportViolationWithDetails(Object value) {
        AlertManager alertManager = PluginServices.INSTANCE.getAlertManager();
        alertManager.processViolation(this, this.playerData, Objects.toString(value));
    }

    public void handlePacket(PacketEvent event) {
    }

    public void handleEvent(org.bukkit.event.Event event) {
    }
}
