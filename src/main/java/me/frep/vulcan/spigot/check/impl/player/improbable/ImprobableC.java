package me.frep.vulcan.spigot.check.impl.player.improbable;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Improbable", type = 'C', complexType = "Player", description = "Too many combined player violations.")
public class ImprobableC extends AbstractCheck {

    private long lastViolationAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX = "playerViolations=";

    public ImprobableC(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent)) {
            int playerViolations = this.playerData.getPlayerViolations();
            if (System.currentTimeMillis() - this.lastViolationAtMillis >= 60000L && playerViolations > VulcanSettings.checksPlayerImprobableCMaxPlayerViolations) {
                this.fail(VIOLATION_DETAIL_PREFIX + playerViolations);
                this.lastViolationAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }
}
