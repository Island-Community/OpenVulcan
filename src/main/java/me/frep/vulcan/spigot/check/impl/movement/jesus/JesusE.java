package me.frep.vulcan.spigot.check.impl.movement.jesus;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Jesus", type = 'E', complexType = "Speed", description = "Moving too quickly on water.")
public class JesusE extends AbstractCheck {

    public JesusE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!this.isPositionUpdatePacket(event)) {
            return;
        }
        CollisionState collision = this.playerData.getCollisionState();
        double deltaXZ = collision.getDeltaXZ();
        boolean exempt = this.isExempt(new ExemptType[] { ExemptType.LILY_PAD, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.CAMPFIRE, ExemptType.CARPET, ExemptType.END_ROD, ExemptType.FENCE, ExemptType.FLOWER_POT, ExemptType.HAPPY_GHAST, ExemptType.NOT_MOVING, ExemptType.CANCELLED_PLACE, ExemptType.CHAIN, ExemptType.SNOW, ExemptType.SWIMMING_JESUS, ExemptType.BED, ExemptType.TRAPDOOR, ExemptType.BOAT, ExemptType.FULLY_SUBMERGED, ExemptType.BUBBLE_COLUMN, ExemptType.VELOCITY, ExemptType.DEPTH_STRIDER, ExemptType.DOLPHINS_GRACE, ExemptType.FENCE, ExemptType.FENCE_GATE, ExemptType.SWIMMING_ON_OLD_VERSION, ExemptType.SPECTATOR, ExemptType.BLOCK_PLACE, ExemptType.CLIMBABLE, ExemptType.NEAR_SOLID, ExemptType.SLOW_FALLING, ExemptType.CHUNK, ExemptType.WALL, ExemptType.SCAFFOLDING, ExemptType.SLIME, ExemptType.SKULL }) || collision.isNearDripleaf() || collision.isNearHeavyCore() || collision.isNearRepeater() || collision.isExemptFlight() || collision.isExemptGliding() || collision.isExemptElytra() || collision.isExemptLevitation() || collision.isExemptVehicle() || collision.isExemptRiptide() || collision.isExemptTeleport() || collision.isNearCandle();
        double maxSpeed = CheckUtils.getMaxSpeed(this.playerData, 0.166F);
        int ticksSinceSolidFooting = collision.getTicksSinceSolidFooting();
        if (ticksSinceSolidFooting < 10 || collision.getTicksSinceSwimming() < 20) {
            maxSpeed += 0.35;
        }
        if (ExemptionManager.isAtLeast1_9() && collision.getTicksSinceEntityCollision() < 20) {
            maxSpeed += 0.2;
        }
        if (!collision.isInLiquid() || exempt) {
            this.decayBuffer();
            return;
        }
        if (deltaXZ - maxSpeed <= 0.0) {
            return;
        }
        if (this.increaseBuffer() > this.maximumBuffer) {
            this.fail("deltaXZ=" + deltaXZ + " max=" + maxSpeed);
            this.playerData.getMotionState().setPendingSwimFix(true);
        }
    }

    @Override
    public void update() {
    }
}
