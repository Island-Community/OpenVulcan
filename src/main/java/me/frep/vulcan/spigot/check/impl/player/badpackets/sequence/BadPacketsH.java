package me.frep.vulcan.spigot.check.impl.player.badpackets.sequence;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'H', complexType = "Packet Order", description = "Invalid attack packet order.")
public class BadPacketsH extends AbstractCheck {

    private boolean swung;

    private static final String VIOLATION_DETAIL_PREFIX = "swung=";

    public BadPacketsH(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SERVER_VERSION, ExemptType.CLIENT_VERSION, ExemptType.FAST });
            if (lastInteractionPacket.getAction() == InteractAction.ATTACK && !exempt && !this.swung) {
                this.fail(VIOLATION_DETAIL_PREFIX + this.swung);
            }
        } else if (this.isArmAnimationPacket(packetEvent)) {
            this.swung = true;
        } else if (this.isMovementPacket(packetEvent)) {
            this.swung = false;
        }
    }

    @Override
    public void update() {
    }
}
