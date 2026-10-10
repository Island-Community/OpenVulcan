package me.frep.vulcan.spigot.check.impl.player.badpackets.sequence;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'X', complexType = "Post ArmAnimation", experimental = false, description = "Post ArmAnimation packets.")
public class BadPacketsX extends AbstractCheck {

    private long lastMovementAtMillis = 0L;

    private boolean swingAfterMovement = false;

    public BadPacketsX(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION, ExemptType.SERVER_VERSION })) {
                return;
            }
            if (this.swingAfterMovement) {
                if (currentTimeMillisDifference > 40L && currentTimeMillisDifference < 100L) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail();
                    }
                } else {
                    this.decayBuffer();
                }
                this.swingAfterMovement = false;
            }
            this.lastMovementAtMillis = System.currentTimeMillis();
        } else if (this.isArmAnimationPacket(packetEvent)) {
            long currentTimeMillisDifference2 = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (currentTimeMillisDifference2 < 5L) {
                this.swingAfterMovement = true;
            }
        }
    }

    @Override
    public void update() {
    }
}
