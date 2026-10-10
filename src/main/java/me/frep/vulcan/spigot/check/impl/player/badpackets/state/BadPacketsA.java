package me.frep.vulcan.spigot.check.impl.player.badpackets.state;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;
import java.util.Optional;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'A', complexType = "Abilities", description = "Spoofed abilities packets.")
public class BadPacketsA extends AbstractCheck {

    public BadPacketsA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isAbilitiesPacket(packetEvent)) {
            if (this.playerData.getCollisionState().isSetbackPending()) {
                return;
            }
            WrapperPlayClientPlayerAbilities wrapperPlayClientPlayerAbilities = new WrapperPlayClientPlayerAbilities((PacketReceiveEvent) packetEvent);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.DEATH });
            if (this.playerData.getPlayer() == null) {
                return;
            }
            if ((!this.playerData.getPlayer().getAllowFlight() && !exempt) && (!this.playerData.getCollisionState().isFlightAllowed())) {
                Optional flightAllowed = wrapperPlayClientPlayerAbilities.isFlightAllowed();
                if (ExemptionManager.isAtLeast1_9()) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("flying=" + wrapperPlayClientPlayerAbilities.isFlying());
                    } else {
                        this.decayBuffer();
                    }
                } else if (flightAllowed.isPresent() && (Boolean) flightAllowed.get()) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("flightAllowed=" + wrapperPlayClientPlayerAbilities.isFlightAllowed());
                    } else {
                        this.decayBuffer();
                    }
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
