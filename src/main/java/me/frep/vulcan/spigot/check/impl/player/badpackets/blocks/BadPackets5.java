package me.frep.vulcan.spigot.check.impl.player.badpackets.blocks;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = '5', complexType = "Scaffold", description = "Invalid Block place.")
public class BadPackets5 extends AbstractCheck {

    public BadPackets5(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isBlockPlacementPacket(packetEvent)) {
            WrapperPlayClientPlayerBlockPlacement lastPlacementPacket = this.playerData.getLastPlacementPacket();
            Vector3f cursorPosition = lastPlacementPacket.getCursorPosition();
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.CLIENT_VERSION });
            if (exempt) {
                return;
            }
            float x = cursorPosition.getX();
            float y = cursorPosition.getY();
            float z = cursorPosition.getZ();
            if (x > 1.0F || y > 1.0F || z > 1.0F || x < 0.0F || y < 0.0F || z < 0.0F) {
                this.fail("x=" + x + " y=" + y + " z=" + z);
            }
        }
    }

    @Override
    public void update() {
    }
}
