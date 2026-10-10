package me.frep.vulcan.spigot.check.impl.player.badpackets.combat;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = '9', complexType = "No Swing", description = "Hitting without swinging.")
public class BadPackets9 extends AbstractCheck {

    private boolean swung = false;

    private boolean attacked = false;

    public BadPackets9(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((!this.playerData.getClientVersion().isOlderThan(ClientVersion.V_1_21_2)) && (!this.isExempt(new ExemptType[] { ExemptType.SPECTATOR }))) {
            if (this.isArmAnimationPacket(packetEvent)) {
                this.swung = true;
            } else if (this.isAttackPacket(packetEvent)) {
                WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
                if (lastInteractionPacket.getAction() != InteractAction.ATTACK) {
                    return;
                }
                this.attacked = true;
            } else if (this.isClientTickEndPacket(packetEvent)) {
                if (this.attacked && !this.swung) {
                    this.fail("attacked=" + this.attacked + " swung=" + this.swung);
                }
                this.attacked = false;
                this.swung = false;
            }
            return;
        }
    }

    @Override
    public void update() {
    }
}
