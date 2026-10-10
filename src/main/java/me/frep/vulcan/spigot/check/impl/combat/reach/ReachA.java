package me.frep.vulcan.spigot.check.impl.combat.reach;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.player.settings.PlayerToggles;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.collection.tuple.Pair;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;

@CheckInfo(name = "Reach", type = 'A', complexType = "History", description = "Hit from too far away.")
public class ReachA extends AbstractCheck {

    private boolean pendingAttack = true;

    public ReachA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            if (lastInteractionPacket.getAction() != InteractAction.ATTACK) {
                return;
            }
            RelativeMoveTracker relativeMoveTracker = this.playerData.getRelativeMoveTracker();
            if (!(relativeMoveTracker.getLastAttackedEntity() instanceof Player)) {
                return;
            }
            this.pendingAttack = true;
        } else if (this.isMovementPacket(packetEvent) && this.pendingAttack) {
            TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
            int totalTicks = tickProcessor.getTotalTicks();
            int index = MathUtils.pingCompensationTicks(this.playerData);
            CollisionState collisionState = this.playerData.getCollisionState();
            double x = collisionState.getX();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double z = collisionState2.getZ();
            Vector vector = new Vector(x, 0.0, z);
            RelativeMoveTracker relativeMoveTracker2 = this.playerData.getRelativeMoveTracker();
            double distance2 = relativeMoveTracker2.getTargetPositions().stream().filter(sample -> ReachA.isRecentTargetPosition(totalTicks, index, sample)).mapToDouble(sample -> ReachA.calculateTargetDistance(vector, sample)).min().orElse(-1.0);
            if (distance2 == -1.0) {
                this.decreaseBuffer(0.025);
            } else {
                PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                RelativeMoveTracker relativeMoveTracker3 = this.playerData.getRelativeMoveTracker();
                PlayerData playerData = playerDataStore.getPlayerData(relativeMoveTracker3.getPlayer());
                if (playerData == null) {
                    return;
                }
                if (playerData.getPlayer() == null) {
                    return;
                }
                if (VulcanSettings.settingsIgnoreFlagsGeyserClientBrand && playerData.getClientBrand().toLowerCase().contains("geyser")) {
                    return;
                }
                if (VulcanSettings.settingsIgnoreGeyserUuids && playerData.getUniqueId().toString().startsWith("00000")) {
                    return;
                }
                if ((VulcanSettings.settingsIgnoreGeyserPrefixes) && (playerData.getPlayerName().startsWith(VulcanSettings.settingsIgnoreGeyserPrefix))) {
                    return;
                }
                if ((VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isLegacyFloodgateEnabled()) && (org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(playerData.getPlayer().getUniqueId()))) {
                    return;
                }
                if (VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isFloodgateEnabled()) {
                    FloodgateApi instance = FloodgateApi.getInstance();
                    if (instance.isFloodgatePlayer(playerData.getPlayer().getUniqueId())) {
                        return;
                    }
                }
                condition = ((this.playerData.getPlayer() != null) && (this.playerData.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) > 0));
                boolean condition2 = condition;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.VEHICLE, ExemptType.BOAT });
                PlayerToggles playerToggles = playerData.getPlayerToggles();
                boolean anyExempt = playerToggles.isAnyExempt(new ExemptType[] { ExemptType.VEHICLE, ExemptType.BOAT });
                MotionState motionState = this.playerData.getMotionState();
                double adjustedEntityInteractionRange = motionState.getEntityInteractionRange() + (VulcanSettings.checksCombatReachAMaxReach - 3.0);
                long currentTimeMillis2 = System.currentTimeMillis();
                ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
                if (currentTimeMillis2 - connectionTracker.getLastFastMovementAtMillis() < 1500L) {
                    adjustedEntityInteractionRange++;
                }
                if (this.isExempt(new ExemptType[] { ExemptType.LIQUID })) {
                    adjustedEntityInteractionRange += 0.1;
                }
                if (distance2 > adjustedEntityInteractionRange && distance2 < 8.0 && !condition2 && !exempt && !anyExempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("distance=" + distance2);
                    }
                } else {
                    this.decayBuffer();
                }
            }
            this.pendingAttack = false;
        }
    }

    @Override
    public void update() {
    }

    private static double calculateTargetDistance(Vector vector, Pair pair) {
        Vector vector2 = ((Location) pair.getFirst()).toVector().setY(0);
        return vector.distance(vector2) - 0.52;
    }

    private static boolean isRecentTargetPosition(int index, int index2, Pair pair) {
        return Math.abs(index - (Integer) pair.getSecond() - index2) < 3;
    }
}
