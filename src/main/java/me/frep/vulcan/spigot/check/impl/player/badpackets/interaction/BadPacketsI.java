package me.frep.vulcan.spigot.check.impl.player.badpackets.interaction;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'I', complexType = "Entity Action", description = "Sent EntityAction while attacking.")
public class BadPacketsI extends AbstractCheck {

    public BadPacketsI(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            boolean entityActionReceived = this.playerData.getMotionState().isEntityActionReceived();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SERVER_VERSION, ExemptType.CLIENT_VERSION, ExemptType.FAST, ExemptType.SPECTATOR, ExemptType.DEATH });
            if (entityActionReceived && !exempt) {
                this.fail();
            }
        }
    }

    @Override
    public void update() {
    }
}
