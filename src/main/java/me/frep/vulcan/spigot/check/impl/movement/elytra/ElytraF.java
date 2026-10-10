package me.frep.vulcan.spigot.check.impl.movement.elytra;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction.Action;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Elytra", type = 'F', complexType = "Packet", description = "Sending Elytra packets too quickly.")
public class ElytraF extends AbstractCheck {

    private long lastElytraStartAtMillis;

    public ElytraF(PlayerData playerData) {
        super(playerData);
        this.lastElytraStartAtMillis = 0L;
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isEntityActionPacket(packetEvent)) && (!this.isTeleporting())) {
            if (ExemptionManager.isBefore1_9()) {
                return;
            }
            WrapperPlayClientEntityAction wrapperPlayClientEntityAction = this.playerData.getLastEntityActionPacket();
            if (wrapperPlayClientEntityAction.getAction() == Action.START_FLYING_WITH_ELYTRA) {
                long delay = System.currentTimeMillis() - this.lastElytraStartAtMillis;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FAST });
                if (delay < 110L && !exempt) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("delay=" + delay);
                    }
                } else {
                    this.decayBuffer();
                }
                this.lastElytraStartAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }
}
