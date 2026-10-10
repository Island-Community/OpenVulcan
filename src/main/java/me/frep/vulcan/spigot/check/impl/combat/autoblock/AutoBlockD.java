package me.frep.vulcan.spigot.check.impl.combat.autoblock;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;

@CheckInfo(name = "Auto Block", type = 'D', complexType = "Order", description = "Invalid attack order.")
public class AutoBlockD extends AbstractCheck {

    private boolean interacted;

    private boolean attacked;

    public AutoBlockD(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            this.attacked = false;
            this.interacted = false;
        } else if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity interaction = this.playerData.getLastInteractionPacket();
            if (interaction.getAction() == InteractAction.ATTACK) {
                this.attacked = true;
            }
            if (interaction.getAction() == InteractAction.INTERACT || interaction.getAction() == InteractAction.INTERACT_AT) {
                this.interacted = true;
            }
        } else if (this.isBlockPlacementPacket(packetEvent)) {
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION });
            if (!exempt && this.attacked && !this.interacted) {
                this.fail("a=" + this.attacked + " i=" + this.interacted);
            }
        }
    }

    @Override
    public void update() {
    }
}
