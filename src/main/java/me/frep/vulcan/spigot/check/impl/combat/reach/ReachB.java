package me.frep.vulcan.spigot.check.impl.combat.reach;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.player.settings.PlayerToggles;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = 'B', complexType = "Simple", description = "Hit from too far away.")
public class ReachB extends AbstractCheck {

    private boolean pendingAttack = false;

    private Player player;

    private static final String DETAIL_SEPARATOR;

    public ReachB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        double yawDifference;
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            if ((lastInteractionPacket.getAction() == InteractAction.ATTACK)) {
                RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
                if ((relativeMoveTracker.getLastAttackedEntity() instanceof Player)) {
                    this.pendingAttack = true;
                    RelativeMoveTracker relativeMoveTracker2 = this.playerData.getRelativeMoveTracker();
                    this.player = (Player) relativeMoveTracker2.getLastAttackedEntity();
                    return;
                }
            }
        } else {
            if (this.isMovementPacket(packetEvent) && this.pendingAttack && this.player != null) {
                PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                PlayerData playerData = playerDataStore.getPlayerData(this.player);
                if (playerData == null) {
                    return;
                }
                if (playerData.getPlayer() == null) {
                    return;
                }
                CollisionState collisionState = this.playerData.getCollisionState();
                double x = collisionState.getX();
                CollisionState collisionState2 = this.playerData.getCollisionState();
                double y = collisionState2.getY();
                CollisionState collisionState3 = this.playerData.getCollisionState();
                double z = collisionState3.getZ();
                Vector vector = new Vector(x, 0.0, z);
                CollisionState collisionState4 = playerData.getCollisionState();
                double x2 = collisionState4.getX();
                CollisionState collisionState5 = playerData.getCollisionState();
                double y2 = collisionState5.getY();
                CollisionState collisionState6 = playerData.getCollisionState();
                double z2 = collisionState6.getZ();
                Vector vector2 = new Vector(x2, 0.0, z2);
                double yDifference = Math.abs(y - y2);
                CollisionState collisionState7 = this.playerData.getCollisionState();
                double deltaXZ = collisionState7.getDeltaXZ();
                double amount2 = vector.distance(vector2) - 0.565;
                int groundTicks = this.playerData.getCollisionState().getGroundTicks();
                MotionState motionState = this.playerData.getMotionState();
                double adjustedEntityInteractionRange = motionState.getEntityInteractionRange() + (VulcanSettings.checksCombatReachAMaxReach - 3.0);
                adjustedEntityInteractionRange += 0.05;
                adjustedEntityInteractionRange += yDifference * 0.9;
                adjustedEntityInteractionRange += deltaXZ * 0.66;
                MotionState motionState2 = this.playerData.getMotionState();
                if (motionState2.isSprinting()) {
                    adjustedEntityInteractionRange += 0.32;
                }
                if (groundTicks < 3) {
                    adjustedEntityInteractionRange += 0.35;
                }
                MotionState motionState3 = this.playerData.getMotionState();
                if (motionState3.isSpeedEffect()) {
                    MotionState motionState4 = this.playerData.getMotionState();
                    adjustedEntityInteractionRange += motionState4.getSpeedAmplifier() * 0.125;
                }
                if ((this.playerData.getPlayer() != null) && (playerData.getPlayer() != null)) {
                    Player player = this.playerData.getPlayer();
                    yawDifference = CheckUtils.getYawDifference(player, playerData.getPlayer());
                } else {
                    yawDifference = 101.0;
                }
                double yawDifference2 = yawDifference;
                if (yawDifference2 > 100.0) {
                    adjustedEntityInteractionRange++;
                }
                boolean exempt;
                exempt = this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.PLUGIN_LOAD, ExemptType.WORLD_CHANGE, ExemptType.CREATIVE, ExemptType.VEHICLE });
                if (!this.isExempt(new ExemptType[] { ExemptType.SERVER_POSITION })) {
                    long currentTimeMillis2 = System.currentTimeMillis();
                    ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
                    if (!(currentTimeMillis2 - connectionTracker.getLastFastMovementAtMillis() >= 1500L)) {
                        adjustedEntityInteractionRange++;
                    }
                } else {
                    adjustedEntityInteractionRange++;
                }
                PlayerToggles playerToggles = playerData.getPlayerToggles();
                boolean anyExempt = playerToggles.isAnyExempt(new ExemptType[] { ExemptType.VEHICLE, ExemptType.BOAT });
                if (amount2 > adjustedEntityInteractionRange && amount2 < 6.0 && !exempt && !anyExempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail(amount2 + DETAIL_SEPARATOR + adjustedEntityInteractionRange);
                    }
                } else {
                    this.decayBuffer();
                }
                this.pendingAttack = false;
            }
            return;
        }
        this.player = null;
        this.pendingAttack = false;
    }

    @Override
    public void update() {
    }

    static {
        DETAIL_SEPARATOR = " > ";
    }
}
