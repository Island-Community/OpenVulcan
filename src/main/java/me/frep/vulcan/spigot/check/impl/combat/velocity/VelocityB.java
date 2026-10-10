package me.frep.vulcan.spigot.check.impl.combat.velocity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Velocity", type = 'B', complexType = "Horizontal", description = "Horizontal velocity modifications.")
public class VelocityB extends AbstractCheck {

    public VelocityB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean movementPacket = this.isMovementPacket(packetEvent);
        if (movementPacket) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            double velocityX = this.playerData.getVelocityTracker().getVelocityX();
            VelocityTracker velocityTracker = this.playerData.getVelocityTracker();
            double velocityXZ = MathUtils.hypot(velocityX, velocityTracker.getVelocityZ());
            if (velocityXZ == 0.01) {
                return;
            }
            double scaledDeltaXZ = deltaXZ / velocityXZ;
            VelocityTracker velocityTracker2 = this.playerData.getVelocityTracker();
            boolean empty = velocityTracker2.getPendingVelocities().isEmpty();
            if (!(scaledDeltaXZ <= 0.21)) {
                this.fail("percent=" + scaledDeltaXZ * 100.0 + "% deltaXZ=" + deltaXZ + " velocityXZ=" + velocityXZ);
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
