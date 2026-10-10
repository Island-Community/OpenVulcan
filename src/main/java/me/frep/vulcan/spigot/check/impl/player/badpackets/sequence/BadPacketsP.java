package me.frep.vulcan.spigot.check.impl.player.badpackets.sequence;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'P', complexType = "Post Entity Action", description = "Post EntityAction packets.")
public class BadPacketsP extends AbstractCheck {

    private long lastMovementAtMillis = 0L;

    private boolean entityActionAfterMovement = false;

    public BadPacketsP(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (ExemptionManager.isAtLeast1_9()) {
                return;
            }
            if (this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.SPECTATOR })) {
                return;
            }
            if (this.entityActionAfterMovement) {
                if (currentTimeMillisDifference > 40L && currentTimeMillisDifference < 100L) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail();
                    }
                } else {
                    this.decayBuffer();
                }
                this.entityActionAfterMovement = false;
            }
            this.lastMovementAtMillis = System.currentTimeMillis();
        } else if (this.isEntityActionPacket(packetEvent)) {
            long currentTimeMillisDifference2 = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (currentTimeMillisDifference2 < 5L) {
                this.entityActionAfterMovement = true;
            }
        }
    }

    @Override
    public void update() {
    }
}
