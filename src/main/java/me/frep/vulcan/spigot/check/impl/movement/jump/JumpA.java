package me.frep.vulcan.spigot.check.impl.movement.jump;

import com.github.retrooper.packetevents.event.PacketEvent;
import java.util.Iterator;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.minecraft.MaterialSets;

@CheckInfo(name = "Jump", type = 'A', complexType = "Motion", description = "Invalid jump motion.")
public class JumpA extends AbstractCheck {

    public JumpA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        double amount;
        double deltaY2;
        byte ticks = (byte) (this.isPositionUpdatePacket(packetEvent) ? 1 : 0);
        CollisionState collisionState = this.playerData.getCollisionState();
        double deltaY = collisionState.getDeltaY();
        int index2;
        if (ticks == 6) {
            double deltaYDifference;
            index2 = (deltaYDifference = deltaY - 0.033) == 0.0 ? 0 : (deltaYDifference < 0.0 ? -1 : 1);
        } else {
            MotionState motionState = this.playerData.getMotionState();
            double jumpStrengthDifference;
            index2 = (jumpStrengthDifference = motionState.getJumpStrength() - 0.42) == 0.0 ? 0 : (jumpStrengthDifference < 0.0 ? -1 : 1);
        }
        if (index2 >= 20) {
            MotionState motionState2 = this.playerData.getMotionState();
            if (motionState2.getGravity() > 0.08) {
                boolean nearGround = this.playerData.getCollisionState().isNearGround();
                if (!VulcanSettings.ghostBlocksFixGhostWaterFix && nearGround) {
                    Iterator iterator2 = MaterialSets.jumpMotionValues.iterator();
                    if (iterator2.hasNext()) {
                        double amount5 = (Double) iterator2.next();
                        deltaY2 = Math.abs(deltaY);
                        amount = 0.0;
                    } else {
                        deltaY2 = (Double) MaterialSets.jumpBoostTable.get(Integer.valueOf(ticks));
                        MotionState motionState3 = this.playerData.getMotionState();
                        amount = motionState3.getJumpBoostAmplifier() * 0.1F;
                    }
                } else {
                    deltaY2 = (Double) MaterialSets.jumpBoostTable.get(Integer.valueOf(ticks));
                    MotionState motionState3 = this.playerData.getMotionState();
                    amount = motionState3.getJumpBoostAmplifier() * 0.1F;
                }
                double adjustedDeltaY2 = deltaY2 + amount;
                double deltaYDifference2 = Math.abs(deltaY - adjustedDeltaY2);
                long currentTimeMillisDifferenceDifference;
                int index3 = (currentTimeMillisDifferenceDifference = System.currentTimeMillis() - this.playerData.getJoinTime() - 30000L) == 0L ? 0 : (currentTimeMillisDifferenceDifference < 0L ? -1 : 1);
                this.playerData.getMotionState().getTicksSinceDamage();
                this.isExempt(new ExemptType[] { ExemptType.VELOCITY, ExemptType.COLLIDING_VERTICALLY, ExemptType.HOPPER, ExemptType.SLIME, ExemptType.SCAFFOLDING, ExemptType.BOAT, ExemptType.TRAPDOOR, ExemptType.STAIRS, ExemptType.SHULKER, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.SLAB, ExemptType.ILLEGAL_BLOCK, ExemptType.JUMP_BOOST_RAN_OUT, ExemptType.FENCE, ExemptType.JUMP_BOOST, ExemptType.HONEY, ExemptType.FISHING_ROD, ExemptType.WEB, ExemptType.FULLY_STUCK, ExemptType.SHULKER_BOX, ExemptType.VEHICLE, ExemptType.BED, ExemptType.SEA_PICKLE, ExemptType.CHAIN, ExemptType.SLEEPING, ExemptType.CANCELLED_BREAK, ExemptType.ANVIL, ExemptType.PISTON, ExemptType.TURTLE_EGG, ExemptType.DOOR, ExemptType.DEATH, ExemptType.PARTIALLY_STUCK, ExemptType.WALL, ExemptType.PLACED_WEB, ExemptType.BUBBLE_COLUMN, ExemptType.FROZEN, ExemptType.KELP, ExemptType.SNOW, ExemptType.BLOCK_BREAK, ExemptType.SWEET_BERRIES, ExemptType.EMPTIED_BUCKET, ExemptType.CONDUIT, ExemptType.FARMLAND, ExemptType.LENIENT_SCAFFOLDING, ExemptType.ATTACK_DAMAGE, ExemptType.SEAGRASS, ExemptType.DRIPSTONE, ExemptType.WATERLOGGED, ExemptType.CHEST, ExemptType.SOUL_SAND, ExemptType.GLITCHED_BLOCKS_ABOVE, ExemptType.POWDER_SNOW, ExemptType.SIGN, ExemptType.SEA_PICKLE, ExemptType.PACKET_EXPLOSION });
                this.playerData.getMotionState().isLastPlacementValid();
                double deltaYDifference3;
                int index4 = (deltaYDifference3 = deltaY - 0.0) == 0.0 ? 0 : (deltaYDifference3 < 0.0 ? -1 : 1);
                if (index4 > 0 || deltaYDifference2 > 0.5) {
                    this.fail("deltaY=" + deltaY + " ticks=" + ticks + " difference=" + deltaYDifference2);
                    this.decayBuffer();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
