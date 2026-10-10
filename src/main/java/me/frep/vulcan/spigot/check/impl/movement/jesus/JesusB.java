package me.frep.vulcan.spigot.check.impl.movement.jesus;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Jesus", type = 'B', complexType = "Motion", description = "Invalid Y-change in water.")
public class JesusB extends AbstractCheck {

    public JesusB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!this.isPositionUpdatePacket(event)) {
            return;
        }
        CollisionState collision = this.playerData.getCollisionState();
        double deltaY = collision.getDeltaY();
        double deltaXZ = collision.getDeltaXZ();
        if (collision.getY() - 250.0 > 0.0) {
            return;
        }
        boolean suspicious = !this.isExempt(new ExemptType[] { ExemptType.LILY_PAD, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.CAMPFIRE, ExemptType.CARPET, ExemptType.END_ROD, ExemptType.FENCE, ExemptType.BOAT, ExemptType.FLOWER_POT, ExemptType.SHULKER, ExemptType.CANCELLED_PLACE, ExemptType.CHAIN, ExemptType.SNOW, ExemptType.SWIMMING_JESUS, ExemptType.BED, ExemptType.TRAPDOOR, ExemptType.BOAT, ExemptType.FULLY_SUBMERGED, ExemptType.BUBBLE_COLUMN, ExemptType.VELOCITY, ExemptType.DEPTH_STRIDER, ExemptType.DOLPHINS_GRACE, ExemptType.SEA_PICKLE, ExemptType.SWIMMING_ON_OLD_VERSION, ExemptType.SPECTATOR, ExemptType.SOUL_SAND, ExemptType.NOT_MOVING, ExemptType.NEAR_SOLID, ExemptType.CHUNK, ExemptType.SKULL, ExemptType.CLIMBABLE, ExemptType.HAPPY_GHAST, ExemptType.SCAFFOLDING, ExemptType.SLOW_FALLING }) && !collision.isNearHeavyCore() && !collision.isExemptFlight() && !collision.isExemptGliding() && !collision.isExemptElytra() && !collision.isExemptLevitation() && !collision.isExemptVehicle() && !collision.isExemptRiptide() && !collision.isExemptTeleport() && !collision.isNearCandle() && !collision.isNearRepeater() && !collision.isNearDripleaf() && collision.getTicksSinceBoat() >= 20;
        if (!collision.isInLiquid() || !suspicious) {
            return;
        }
        double limit = 0.11;
        if (collision.getTicksSinceSolidFooting() < 10) {
            limit += 0.05;
        }
        if (Math.abs(deltaY) - 0.015 >= 0.0 || deltaXZ - limit <= 0.0) {
            this.decayBuffer();
            return;
        }
        if (this.increaseBuffer() > this.maximumBuffer) {
            this.fail("deltaY=" + deltaY + " deltaXZ=" + deltaXZ);
            this.playerData.getMotionState().setPendingSwimFix(true);
        }
    }

    @Override
    public void update() {
    }
}
