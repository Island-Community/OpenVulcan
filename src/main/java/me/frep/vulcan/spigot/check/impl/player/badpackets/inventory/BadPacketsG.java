package me.frep.vulcan.spigot.check.impl.player.badpackets.inventory;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'G', complexType = "Hotbar", description = "Invalid HeldItemSlot packet.")
public class BadPacketsG extends AbstractCheck {

    private int lastSlot = -1;

    public BadPacketsG(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isHeldItemChangePacket(packetEvent)) {
            WrapperPlayClientHeldItemChange lastHeldItemPacket = this.playerData.getLastHeldItemPacket();
            int count2 = lastHeldItemPacket.getSlot();
            if (count2 == this.lastSlot) {
                this.fail("slot=" + count2 + " lastSlot=" + this.lastSlot);
            }
            this.lastSlot = count2;
        }
    }

    @Override
    public void update() {
    }
}
