package me.frep.vulcan.spigot.check.impl.combat.autoblock;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Auto Block", type = 'B', complexType = "Packet", description = "Attacked while sending BlockDig packet.")
public class AutoBlockB extends AbstractCheck {

    public AutoBlockB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            if (this.playerData.getLastInteractionPacket().getAction() == InteractAction.ATTACK) {
                boolean releaseUseItemReceived = this.playerData.getMotionState().isReleaseUseItemReceived();
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION });
                if (releaseUseItemReceived && !exempt) {
                    this.fail();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
