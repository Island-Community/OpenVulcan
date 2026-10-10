package me.frep.vulcan.spigot.check.impl.player.badpackets.blocks;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;

@CheckInfo(name = "Bad Packets", type = '8', complexType = "Nuker", description = "Invalid Block Dig packets.")
public class BadPackets8 extends AbstractCheck {

    private int digPackets;

    private long lastDigPacketAtMillis;

    public BadPackets8(PlayerData playerData) {
        super(playerData);
        this.digPackets = 0;
        this.lastDigPacketAtMillis = -1L;
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.playerData.isAtLeast1_21_2()) {
            if (this.isDiggingPacket(packetEvent)) {
                WrapperPlayClientPlayerDigging lastDiggingPacket = this.playerData.getLastDiggingPacket();
                switch(lastDiggingPacket.getAction()) {
                    case DROP_ITEM:
                    case DROP_ITEM_STACK:
                    case RELEASE_USE_ITEM:
                    case SWAP_ITEM_WITH_OFFHAND:
                    case CANCELLED_DIGGING:
                        return;
                    default:
                        if (++this.digPackets > 3) {
                            if (this.increaseBuffer() > this.maximumBuffer) {
                                this.fail("digs=" + this.digPackets);
                            }
                        } else {
                            this.decayBuffer();
                        }
                }
            } else if (this.isClientTickEndPacket(packetEvent)) {
                this.digPackets = 0;
            }
        } else if (this.isDiggingPacket(packetEvent)) {
            WrapperPlayClientPlayerDigging lastDiggingPacket2 = this.playerData.getLastDiggingPacket();
            switch(lastDiggingPacket2.getAction()) {
                case DROP_ITEM:
                case DROP_ITEM_STACK:
                case RELEASE_USE_ITEM:
                case SWAP_ITEM_WITH_OFFHAND:
                case CANCELLED_DIGGING:
                    return;
                default:
                    long currentTimeMillis2 = System.currentTimeMillis();
                    long delay = currentTimeMillis2 - this.lastDigPacketAtMillis;
                    ConnectionTracker connectionTracker = this.playerData.getConnectionTracker();
                    boolean belowCurrentTimeMillis2DifferenceLimit = currentTimeMillis2 - connectionTracker.getLastFastMovementAtMillis() < 1000L;
                    if (delay < 10L && !belowCurrentTimeMillis2DifferenceLimit) {
                        if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("delay=" + delay);
                        }
                    } else {
                        this.decayBuffer();
                    }
                    this.lastDigPacketAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }
}
