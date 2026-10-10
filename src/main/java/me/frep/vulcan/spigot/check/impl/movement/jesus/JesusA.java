package me.frep.vulcan.spigot.check.impl.movement.jesus;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Jesus", type = 'A', complexType = "Ground", description = "Walking on water.")
public class JesusA extends AbstractCheck {

    public JesusA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!this.isPositionUpdatePacket(event)) {
            return;
        }
        CollisionState collision = this.playerData.getCollisionState();
        if (collision.getY() - 250.0 > 0.0) {
            return;
        }
        boolean suspicious = !this.isExempt(new ExemptType[] { ExemptType.LILY_PAD, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.CAMPFIRE, ExemptType.CARPET, ExemptType.END_ROD, ExemptType.FENCE, ExemptType.BOAT, ExemptType.HAPPY_GHAST, ExemptType.CANCELLED_PLACE, ExemptType.CHAIN, ExemptType.SNOW, ExemptType.SWIMMING_JESUS, ExemptType.BED, ExemptType.TRAPDOOR, ExemptType.BOAT, ExemptType.DOLPHINS_GRACE, ExemptType.FLOWER_POT, ExemptType.SHULKER, ExemptType.SPECTATOR, ExemptType.SCAFFOLDING, ExemptType.SKULL, ExemptType.SEA_PICKLE, ExemptType.NEAR_SOLID, ExemptType.NOT_MOVING, ExemptType.BLOCK_PLACE }) && !collision.isExemptFlight() && !collision.isExemptGliding() && !collision.isExemptLevitation() && !collision.isExemptVehicle() && !collision.isExemptRiptide() && !collision.isExemptTeleport() && !collision.isExemptChunk() && !collision.isNearHeavyCore() && !collision.isNearRepeater() && !collision.isNearCandle() && !collision.isNearDripleaf() && collision.getTicksSinceBoat() >= 20;
        if (!collision.isInLiquid() || !suspicious) {
            this.decayBuffer();
            return;
        }
        if (!collision.isOnGround() && !collision.isGroundAligned()) {
            this.decayBuffer();
            return;
        }
        if (collision.getDeltaXZ() - 0.1 <= 0.0) {
            this.decayBuffer();
            return;
        }
        if (this.increaseBuffer() > this.maximumBuffer) {
            this.fail();
            this.playerData.getMotionState().setPendingSwimFix(true);
        }
    }

    @Override
    public void update() {
    }
}
