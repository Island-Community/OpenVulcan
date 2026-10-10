package me.frep.vulcan.spigot.check.impl.player.improbable;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Improbable", type = 'E', complexType = "Total", description = "Too many combined violations.")
public class ImprobableE extends AbstractCheck {

    private long lastViolationAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX = "totalViolations=";

    public ImprobableE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            int totalViolations = this.playerData.getTotalViolations();
            if (System.currentTimeMillis() - this.lastViolationAtMillis >= 60000L && totalViolations > VulcanSettings.checksPlayerImprobableEMaxTotalViolations) {
                this.fail(VIOLATION_DETAIL_PREFIX + totalViolations);
                this.lastViolationAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }
}
