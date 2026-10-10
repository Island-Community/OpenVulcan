package me.frep.vulcan.spigot.check.impl.combat.killaura;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Kill Aura", type = 'A', complexType = "Post", description = "Post UseEntity packets.")
public class KillAuraA extends AbstractCheck {

    private long lastMovementAtMillis;

    private boolean attackAfterMovement;

    public KillAuraA(PlayerData playerData) {
        super(playerData);
        this.lastMovementAtMillis = 0L;
        this.attackAfterMovement = false;
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            long delay = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (this.attackAfterMovement) {
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SPECTATOR });
                if (delay > 40L && delay < 100L && !exempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("delay=" + delay);
                    }
                } else {
                    this.decayBuffer();
                }
                this.attackAfterMovement = false;
            }
            this.lastMovementAtMillis = System.currentTimeMillis();
        } else if (this.isAttackPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            if (lastInteractionPacket.getAction() != InteractAction.ATTACK) {
                return;
            }
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (currentTimeMillisDifference < 5L) {
                this.attackAfterMovement = true;
            }
        }
    }

    @Override
    public void update() {
    }
}
