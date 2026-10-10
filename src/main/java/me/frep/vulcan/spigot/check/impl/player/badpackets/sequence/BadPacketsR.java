package me.frep.vulcan.spigot.check.impl.player.badpackets.sequence;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'R', complexType = "Post HeldItemSlot", experimental = false, description = "Post HeldItemSlot packets.")
public class BadPacketsR extends AbstractCheck {

    private long lastMovementAtMillis = 0L;

    private boolean slotChangeAfterMovement = false;

    private static final String VIOLATION_DETAIL_PREFIX = "delay=";

    public BadPacketsR(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.SPECTATOR });
            if (this.slotChangeAfterMovement && !exempt) {
                if (currentTimeMillisDifference > 40L && currentTimeMillisDifference < 100L) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail(VIOLATION_DETAIL_PREFIX + currentTimeMillisDifference);
                    }
                } else {
                    this.decayBuffer();
                }
                this.slotChangeAfterMovement = false;
            }
            this.lastMovementAtMillis = System.currentTimeMillis();
        } else if (this.isHeldItemChangePacket(packetEvent)) {
            long currentTimeMillisDifference2 = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (currentTimeMillisDifference2 < 5L) {
                this.slotChangeAfterMovement = true;
            }
        }
    }

    @Override
    public void update() {
    }
}
