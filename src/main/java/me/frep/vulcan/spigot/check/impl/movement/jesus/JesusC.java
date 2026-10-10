package me.frep.vulcan.spigot.check.impl.movement.jesus;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Jesus", type = 'C', complexType = "Y Motion", description = "Invalid Y-Change in water.")
public class JesusC extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public JesusC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!this.isPositionUpdatePacket(event)) {
            return;
        }
        CollisionState collision = this.playerData.getCollisionState();
        double deltaY = collision.getDeltaY();
        if (collision.getY() - 250.0 > 0.0) {
            return;
        }
        boolean suspicious = !this.isExempt(new ExemptType[] { ExemptType.LILY_PAD, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.CAMPFIRE, ExemptType.CARPET, ExemptType.END_ROD, ExemptType.FENCE, ExemptType.BOAT, ExemptType.WEB, ExemptType.HAPPY_GHAST, ExemptType.CANCELLED_PLACE, ExemptType.CHAIN, ExemptType.SNOW, ExemptType.SWIMMING_JESUS, ExemptType.BED, ExemptType.TRAPDOOR, ExemptType.BOAT, ExemptType.FULLY_SUBMERGED, ExemptType.VELOCITY, ExemptType.BUBBLE_COLUMN, ExemptType.SWIMMING_ON_OLD_VERSION, ExemptType.SOUL_SAND, ExemptType.CLIMBABLE, ExemptType.SPECTATOR, ExemptType.NEAR_SOLID, ExemptType.SKULL, ExemptType.FLOWER_POT, ExemptType.CHUNK, ExemptType.NOT_MOVING, ExemptType.SCAFFOLDING }) && !collision.isNearHeavyCore() && !collision.isNearRepeater() && !collision.isExemptFlight() && !collision.isExemptGliding() && !collision.isExemptElytra() && !collision.isExemptLevitation() && !collision.isExemptVehicle() && !collision.isExemptRiptide() && !collision.isExemptTeleport() && !collision.isWeb() && !collision.isNearCandle() && !collision.isNearDripleaf();
        if (!collision.isInLiquid() || !suspicious) {
            this.decayBuffer();
            return;
        }
        if (Math.abs(deltaY) - 0.001 >= 0.0) {
            this.decayBuffer();
            return;
        }
        if (this.increaseBuffer() > this.maximumBuffer) {
            this.fail(VIOLATION_DETAIL_PREFIX + deltaY);
            this.playerData.getMotionState().setPendingSwimFix(true);
        }
    }

    @Override
    public void update() {
    }
}
