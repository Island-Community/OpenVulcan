package me.frep.vulcan.spigot.check.impl.combat.killaura;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Kill Aura", type = 'D', complexType = "Multi Aura", experimental = false, description = "Attacked two entities at once.")
public class KillAuraD extends AbstractCheck {

    private int lastTargetEntityId;

    private int targetSwitches;

    public KillAuraD(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            if (lastInteractionPacket.getAction() != InteractAction.ATTACK) {
                return;
            }
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION, ExemptType.SERVER_VERSION });
            int count3 = lastInteractionPacket.getEntityId();
            if (count3 != this.lastTargetEntityId && !exempt && ++this.targetSwitches > 1) {
                this.fail("id=" + count3 + " lastId=" + this.lastTargetEntityId);
            }
            this.lastTargetEntityId = count3;
        } else if (this.isMovementPacket(packetEvent)) {
            this.targetSwitches = 0;
        }
    }

    @Override
    public void update() {
    }
}
