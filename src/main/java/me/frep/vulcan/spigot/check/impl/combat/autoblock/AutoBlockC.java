package me.frep.vulcan.spigot.check.impl.combat.autoblock;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Auto Block", type = 'C', complexType = "Order", description = "Attacked while sending BlockDig packet.")
public class AutoBlockC extends AbstractCheck {

    private static final String SWORD_MATERIAL_SUFFIX = "SWORD";

    public AutoBlockC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            if (this.playerData.getLastInteractionPacket().getAction() == InteractAction.ATTACK) {
                if (this.playerData.getPlayer() == null) {
                    return;
                }
                boolean holdingSword = this.playerData.getPlayer().getItemInHand().getType().toString().contains(SWORD_MATERIAL_SUFFIX);
                boolean diggingPacketReceived = this.playerData.getMotionState().isDiggingPacketReceived();
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION });
                if (diggingPacketReceived && holdingSword && !exempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail();
                    }
                } else {
                    this.decayBuffer();
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
