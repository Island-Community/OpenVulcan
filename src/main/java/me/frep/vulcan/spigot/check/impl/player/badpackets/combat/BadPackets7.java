package me.frep.vulcan.spigot.check.impl.player.badpackets.combat;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = '7', complexType = "No Swing", description = "Hitting without swinging.")
public class BadPackets7 extends AbstractCheck {

    private boolean swingAvailable;

    private boolean swung;

    private static final String VIOLATION_DETAIL_PREFIX = "swung=";

    public BadPackets7(PlayerData playerData) {
        super(playerData);
        this.swingAvailable = this.playerData.getClientVersion().isNewerThan(ClientVersion.V_1_8);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.playerData.getClientVersion().isOlderThan(ClientVersion.V_1_9)) && (ExemptionManager.isAtLeast1_9())) {
            return;
        }
        if (!this.isExempt(new ExemptType[] { ExemptType.SPECTATOR })) {
            if (this.isArmAnimationPacket(packetEvent)) {
                this.swingAvailable = this.swung = true;
            }
            if (this.isAttackPacket(packetEvent)) {
                WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
                if (lastInteractionPacket.getAction() == InteractAction.ATTACK) {
                    if (this.playerData.isNewerThan1_9() ? !this.swingAvailable : !this.swung) {
                        this.fail(VIOLATION_DETAIL_PREFIX + this.swung);
                    }
                }
                this.swingAvailable = this.swung;
            }
        }
    }

    @Override
    public void update() {
    }
}
