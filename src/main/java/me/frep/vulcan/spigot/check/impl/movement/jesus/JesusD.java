package me.frep.vulcan.spigot.check.impl.movement.jesus;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;

@CheckInfo(name = "Jesus", type = 'D', complexType = "Jump", description = "Jumping on/in water.")
public class JesusD extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "deltaY=";

    public JesusD(PlayerData playerData) {
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
        boolean groundIsAir = ExemptionManager.isNewerThan1_13() ? collision.getGroundMaterial() != null && BlockUtils.isAir(collision.getGroundMaterial()) : collision.getGroundMaterialLegacy() != null && BlockUtils.isAir(collision.getGroundMaterialLegacy());
        boolean exempt = this.isExempt(new ExemptType[] { ExemptType.LILY_PAD, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.CAMPFIRE, ExemptType.CARPET, ExemptType.END_ROD, ExemptType.FENCE, ExemptType.BOAT, ExemptType.NOT_MOVING, ExemptType.CANCELLED_PLACE, ExemptType.CHAIN, ExemptType.SNOW, ExemptType.SWIMMING_JESUS, ExemptType.BED, ExemptType.TRAPDOOR, ExemptType.BOAT, ExemptType.FULLY_SUBMERGED, ExemptType.VELOCITY, ExemptType.HAPPY_GHAST, ExemptType.BUBBLE_COLUMN, ExemptType.SWIMMING_ON_OLD_VERSION, ExemptType.SPECTATOR, ExemptType.CLIMBABLE, ExemptType.NEAR_SOLID, ExemptType.SLIME, ExemptType.HONEY, ExemptType.CHUNK, ExemptType.SCAFFOLDING, ExemptType.SKULL }) || collision.isNearRepeater() || collision.isNearHeavyCore() || collision.isNearDripleaf() || collision.isExemptFlight() || collision.isExemptGliding() || collision.isExemptElytra() || collision.isExemptLevitation() || collision.isExemptVehicle() || collision.isExemptRiptide() || collision.isExemptTeleport();
        if (!collision.isInLiquid() || !groundIsAir || exempt) {
            this.decayBuffer();
            return;
        }
        if (deltaY <= 0.0) {
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
