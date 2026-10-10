package me.frep.vulcan.spigot.check.impl.player.badpackets.inventory;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'J', complexType = "Held Item Slot", description = "Sent HeldItemSlot while placing.")
public class BadPacketsJ extends AbstractCheck {

    public BadPacketsJ(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isHeldItemChangePacket(packetEvent)) {
            boolean itemUseReceived = this.playerData.getMotionState().isItemUseReceived();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION, ExemptType.SERVER_VERSION, ExemptType.CREATIVE, ExemptType.SPECTATOR });
            if (!exempt && itemUseReceived) {
                this.fail();
            }
        }
    }

    @Override
    public void update() {
    }
}
