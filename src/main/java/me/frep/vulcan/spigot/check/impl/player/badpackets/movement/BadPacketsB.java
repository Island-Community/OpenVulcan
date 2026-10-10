package me.frep.vulcan.spigot.check.impl.player.badpackets.movement;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'B', complexType = "Sequence", description = "More than 20 flying packets in a row.")
public class BadPacketsB extends AbstractCheck {

    private int stationaryMovementStreak;

    private static final String VIOLATION_DETAIL_PREFIX = "streak=";

    public BadPacketsB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            WrapperPlayClientPlayerFlying lastFlyingPacket = this.playerData.getLastFlyingPacket();
            if (!(lastFlyingPacket.hasPositionChanged())) {
                if (!((this.playerData.getPlayer() != null) && (this.playerData.getPlayer().isInsideVehicle()))) {
                    boolean exempt = this.isExempt(new ExemptType[] { ExemptType.SERVER_VERSION, ExemptType.CLIENT_VERSION });
                    if (++this.stationaryMovementStreak > 20 && !exempt) {
                        this.fail(VIOLATION_DETAIL_PREFIX + this.stationaryMovementStreak);
                    }
                    return;
                }
            }
        } else {
            if (this.isSteerVehiclePacket(packetEvent)) {
                this.stationaryMovementStreak = 0;
            }
            return;
        }
        this.stationaryMovementStreak = 0;
    }

    @Override
    public void update() {
    }
}
