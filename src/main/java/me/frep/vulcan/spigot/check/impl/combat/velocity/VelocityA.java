package me.frep.vulcan.spigot.check.impl.combat.velocity;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Velocity", type = 'A', complexType = "Vertical", description = "Vertical velocity modifications.")
public class VelocityA extends AbstractCheck {

    private double firstTickResponse;

    private double secondTickResponse;

    public VelocityA(PlayerData playerData) {
        super(playerData);
        this.firstTickResponse = -1.0;
        this.secondTickResponse = -1.0;
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent) && this.playerData.getVelocityTracker().getTicksSinceVelocityPacket() < 3) {
            double deltaY = this.playerData.getCollisionState().getDeltaY();
            double lastVelocityY = this.playerData.getVelocityTracker().getLastVelocityY();
            double amount = 0.999;
            boolean exempt = this.isExempt((new ExemptType[] { ExemptType.COLLIDING_HORIZONTALLY }));
            if (exempt) {
                double firstTickResponseDifference = this.firstTickResponse - amount;
                double amount3 = firstTickResponseDifference == 0.0 ? 0 : (firstTickResponseDifference < 0.0 ? -1 : 1);
                if (amount3 > 0) {
                    this.fail("tick1=" + this.firstTickResponse + " tick2=" + this.secondTickResponse + " deltaY=" + deltaY + " velocity=" + lastVelocityY);
                    this.decayBuffer();
                }
                this.firstTickResponse = -1.0;
                this.secondTickResponse = -1.0;
            }
        }
    }

    @Override
    public void update() {
    }
}
