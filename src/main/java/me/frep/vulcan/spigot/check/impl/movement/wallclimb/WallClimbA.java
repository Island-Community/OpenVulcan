package me.frep.vulcan.spigot.check.impl.movement.wallclimb;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Material;

@CheckInfo(name = "Wall Climb", type = 'A', complexType = "Spider", description = "Climbing up a wall.")
public class WallClimbA extends AbstractCheck {

    private int wallAlignedTicks;

    public WallClimbA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!this.isPositionUpdatePacket(event)) {
            return;
        }
        MotionState motion = this.playerData.getMotionState();
        CollisionState collision = this.playerData.getCollisionState();
        double fractionalX = Math.abs(collision.getX() % 1.0);
        double fractionalZ = Math.abs(collision.getZ() % 1.0);
        double deltaY = collision.getDeltaY();
        if (motion.getGravity() != 0.08) {
            return;
        }
        if (motion.getTicksSinceGravityChange() < 30) {
            return;
        }
        if (Math.abs(deltaY - 0.20000004768371582) < 0.001) {
            return;
        }
        double brokenDistance = motion.getDistanceFromBrokenBlock();
        if (brokenDistance < 1.25 && brokenDistance > 0.0) {
            return;
        }
        boolean eligible = collision.isWallClimbEligible();
        boolean wallAligned = fractionalX > 0.67 && fractionalX < 0.73 || fractionalZ > 0.67 && fractionalZ < 0.73 || fractionalX > 0.27 && fractionalX < 0.33 || fractionalZ > 0.27 && fractionalZ < 0.33;
        this.wallAlignedTicks = wallAligned ? this.wallAlignedTicks + 1 : 0;
        Material ground = ExemptionManager.isNewerThan1_13() ? collision.getGroundMaterial() : collision.getGroundMaterialLegacy();
        if (ground != null && !BlockUtils.isAir(ground)) {
            this.wallAlignedTicks = 0;
        }
        if (collision.getCeilingMaterials() == null || collision.getFloorMaterials() == null) {
            return;
        }
        int ceilingAir = 0;
        int floorAir = 0;
        for (Material material : collision.getCeilingMaterials()) {
            if (BlockUtils.isAir(material))
                ceilingAir++;
        }
        for (Material material : collision.getFloorMaterials()) {
            if (BlockUtils.isAir(material))
                floorAir++;
        }
        boolean invalid = this.wallAlignedTicks > 5 && deltaY > 0.1 && ceilingAir == floorAir;
        boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIMBABLE, ExemptType.SCAFFOLDING, ExemptType.JUMP_BOOST, ExemptType.ELYTRA, ExemptType.SNOW, ExemptType.BLOCK_PLACE, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.GLIDING, ExemptType.LEVITATION, ExemptType.DEATH, ExemptType.HAPPY_GHAST, ExemptType.VELOCITY, ExemptType.RIPTIDE, ExemptType.CANCELLED_PLACE, ExemptType.NEAR_GROUND, ExemptType.POWDER_SNOW, ExemptType.BOAT, ExemptType.SLIME, ExemptType.LIQUID, ExemptType.END_ROD, ExemptType.SHULKER, ExemptType.SHULKER_BOX, ExemptType.FISHING_ROD, ExemptType.FENCE, ExemptType.WALL, ExemptType.PACKET_EXPLOSION, ExemptType.KELP, ExemptType.CARPET, ExemptType.BUBBLE_COLUMN, ExemptType.WATERLOGGED, ExemptType.EMPTIED_BUCKET, ExemptType.TRAPDOOR });
        if (invalid && !exempt && eligible) {
            if (this.increaseBuffer() > this.maximumBuffer) {
                this.fail("deltaY=" + deltaY + " ticks=" + this.wallAlignedTicks);
            }
        } else {
            this.decayBuffer();
        }
    }

    @Override
    public void update() {
    }
}
