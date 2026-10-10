package me.frep.vulcan.spigot.check.impl.player.badpackets.inventory;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'Q', complexType = "Hotbar", description = "Too big HeldItemSlot packet.")
public class BadPacketsQ extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX = "slot=";

    public BadPacketsQ(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isHeldItemChangePacket(packetEvent)) {
            WrapperPlayClientHeldItemChange lastHeldItemPacket = this.playerData.getLastHeldItemPacket();
            int slot = lastHeldItemPacket.getSlot();
            if (slot > 8) {
                this.fail(VIOLATION_DETAIL_PREFIX + slot);
            }
        }
    }

    @Override
    public void update() {
    }
}
