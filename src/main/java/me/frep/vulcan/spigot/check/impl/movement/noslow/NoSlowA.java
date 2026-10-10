package me.frep.vulcan.spigot.check.impl.movement.noslow;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "No Slow", type = 'A', complexType = "Packet", description = "Placing while digging")
public class NoSlowA extends AbstractCheck {

    public NoSlowA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isDiggingPacket(packetEvent)) {
            WrapperPlayClientPlayerDigging lastDiggingPacket = this.playerData.getLastDiggingPacket();
            boolean itemUseReceived = this.playerData.getMotionState().isItemUseReceived();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CLIENT_VERSION, ExemptType.SERVER_VERSION });
            boolean condition = lastDiggingPacket.getAction() == DiggingAction.RELEASE_USE_ITEM;
            if (itemUseReceived && condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail();
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
