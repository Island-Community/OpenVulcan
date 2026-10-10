package me.frep.vulcan.spigot.check.impl.movement.vclip;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;
import me.frep.vulcan.spigot.util.minecraft.MaterialSets;

@CheckInfo(name = "VClip", type = 'A', complexType = "Clip", description = "Large vertical movement.")
public class VClipA extends AbstractCheck {

    public VClipA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getTicksExisted() < 30) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            int airTicks = collisionState.getAirTicks();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            MotionState motionState2 = this.playerData.getMotionState();
            if (motionState2.getGravity() > 0.1) {
                return;
            }
            if (airTicks < 5 && airTicks > 0) {
                double amount = (Double) MaterialSets.fallMotionTable.get(airTicks);
                double deltaYDifference = Math.abs(deltaY - amount);
                VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
                boolean recentVelocity = velocityTracker.getTicksSinceVelocity() < 50;
                boolean recentDamage = this.playerData.getMotionState().getTicksSinceDamage() < 20;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.VELOCITY, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.GLIDING, ExemptType.RIPTIDE, ExemptType.COLLIDING_VERTICALLY, ExemptType.HOPPER, ExemptType.SLIME, ExemptType.SCAFFOLDING, ExemptType.BOAT, ExemptType.TRAPDOOR, ExemptType.STAIRS, ExemptType.SHULKER, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.SLAB, ExemptType.ILLEGAL_BLOCK, ExemptType.TELEPORT, ExemptType.FENCE, ExemptType.JUMP_BOOST, ExemptType.HONEY, ExemptType.FISHING_ROD, ExemptType.WEB, ExemptType.FULLY_STUCK, ExemptType.SHULKER_BOX, ExemptType.ELYTRA, ExemptType.VEHICLE, ExemptType.BED, ExemptType.BLOCK_PLACE, ExemptType.SLEEPING, ExemptType.CANCELLED_BREAK, ExemptType.ANVIL, ExemptType.PISTON, ExemptType.TURTLE_EGG, ExemptType.DOOR, ExemptType.DEATH, ExemptType.PARTIALLY_STUCK, ExemptType.WALL, ExemptType.PLACED_WEB, ExemptType.ENDER_PEARL, ExemptType.LEVITATION, ExemptType.BUBBLE_COLUMN, ExemptType.FROZEN, ExemptType.BLOCK_BREAK, ExemptType.SWEET_BERRIES, ExemptType.EMPTIED_BUCKET, ExemptType.FARMLAND, ExemptType.LENIENT_SCAFFOLDING, ExemptType.ATTACK_DAMAGE, ExemptType.SEAGRASS, ExemptType.DRIPSTONE, ExemptType.WATERLOGGED, ExemptType.CHEST, ExemptType.SOUL_SAND, ExemptType.GLITCHED_BLOCKS_ABOVE, ExemptType.POWDER_SNOW });
                if (!recentVelocity && !recentDamage && !exempt && deltaY < 0.0 && deltaYDifference > 0.75) {
                    this.fail("deltaY=" + deltaY + " ticks=" + airTicks);
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
