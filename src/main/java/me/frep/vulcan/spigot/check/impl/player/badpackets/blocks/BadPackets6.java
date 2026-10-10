package me.frep.vulcan.spigot.check.impl.player.badpackets.blocks;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = '6', complexType = "Nuker", description = "Invalid Block Dig packets.")
public class BadPackets6 extends AbstractCheck {

    private int digPackets = 0;

    private static final String VIOLATION_DETAIL_PREFIX = "digs=";

    public BadPackets6(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isDiggingPacket(packetEvent)) {
            WrapperPlayClientPlayerDigging lastDiggingPacket = this.playerData.getLastDiggingPacket();
            if (lastDiggingPacket.getAction() == DiggingAction.START_DIGGING) {
                if (this.digPackets > 3) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail(VIOLATION_DETAIL_PREFIX + this.digPackets);
                    }
                } else {
                    this.decayBuffer();
                }
            }
        } else if (this.isArmAnimationPacket(packetEvent)) {
            this.digPackets = 0;
        }
    }

    @Override
    public void update() {
    }
}
