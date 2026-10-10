package me.frep.vulcan.spigot.check.impl.player.badpackets.sequence;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'N', complexType = "Post BlockDig", description = "Post BlockDig packets.")
public class BadPacketsN extends AbstractCheck {

    private long lastMovementAtMillis = 0L;

    private boolean diggingAfterMovement = false;

    private static final String VIOLATION_DETAIL_PREFIX = "delay=";

    public BadPacketsN(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (this.diggingAfterMovement) {
                if (currentTimeMillisDifference > 40L && currentTimeMillisDifference < 100L) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail(VIOLATION_DETAIL_PREFIX + currentTimeMillisDifference);
                    }
                } else {
                    this.decayBuffer();
                }
                this.diggingAfterMovement = false;
            }
            this.lastMovementAtMillis = System.currentTimeMillis();
        } else if (this.isDiggingPacket(packetEvent)) {
            long currentTimeMillisDifference2 = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (currentTimeMillisDifference2 < 5L) {
                this.diggingAfterMovement = true;
            }
        }
    }

    @Override
    public void update() {
    }
}
