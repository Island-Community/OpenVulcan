package me.frep.vulcan.spigot.check.impl.player.badpackets.interaction;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'F', complexType = "Steer Vehicle", description = "Invalid Steer Vehicle packet.")
public class BadPacketsF extends AbstractCheck {

    public BadPacketsF(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isSteerVehiclePacket(packetEvent)) {
            WrapperPlayClientSteerVehicle wrapperPlayClientSteerVehicle = new WrapperPlayClientSteerVehicle((PacketReceiveEvent) packetEvent);
            float forward = wrapperPlayClientSteerVehicle.getForward();
            float sideways = wrapperPlayClientSteerVehicle.getSideways();
            boolean condition = Math.abs(forward) > 0.98F || Math.abs(sideways) > 0.98F;
            if (condition) {
                this.fail("forward=" + forward + " side=" + sideways);
            }
        }
    }

    @Override
    public void update() {
    }
}
