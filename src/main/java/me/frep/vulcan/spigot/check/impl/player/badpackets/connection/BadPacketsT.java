package me.frep.vulcan.spigot.check.impl.player.badpackets.connection;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'T', complexType = "Keep Alive", experimental = false, description = "Invalid KeepAlive packets.")
public class BadPacketsT extends AbstractCheck {

    private long lastKeepAliveId = -1L;

    public BadPacketsT(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isKeepAlivePacket(packetEvent)) {
            WrapperPlayClientKeepAlive wrapperPlayClientKeepAlive = new WrapperPlayClientKeepAlive((PacketReceiveEvent) packetEvent);
            long timestamp2 = wrapperPlayClientKeepAlive.getId();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.JOINED });
            if ((timestamp2 == this.lastKeepAliveId || timestamp2 == 0L) && !exempt) {
                this.fail("id=" + timestamp2 + " lastId=" + this.lastKeepAliveId);
            }
            this.lastKeepAliveId = timestamp2;
        }
    }

    @Override
    public void update() {
    }
}
