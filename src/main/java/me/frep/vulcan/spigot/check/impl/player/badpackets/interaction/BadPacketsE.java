package me.frep.vulcan.spigot.check.impl.player.badpackets.interaction;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'E', complexType = "Interact", description = "Interacted with themselves.")
public class BadPacketsE extends AbstractCheck {

    public BadPacketsE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SPECTATOR });
            if (lastInteractionPacket.getEntityId() == this.playerData.getEntityId() && !exempt) {
                this.fail();
            }
        }
    }

    @Override
    public void update() {
    }
}
