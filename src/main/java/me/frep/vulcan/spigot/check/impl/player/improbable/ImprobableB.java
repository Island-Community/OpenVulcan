package me.frep.vulcan.spigot.check.impl.player.improbable;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Improbable", type = 'B', complexType = "Movement", experimental = false, description = "Too many combined movement violations.")
public class ImprobableB extends AbstractCheck {

    private long lastViolationAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX = "movementViolations=";

    public ImprobableB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent)) {
            int movementViolations = this.playerData.getMovementViolations();
            if (System.currentTimeMillis() - this.lastViolationAtMillis >= 60000L && movementViolations > VulcanSettings.checksPlayerImprobableBMaxMovementViolations) {
                this.fail(VIOLATION_DETAIL_PREFIX + movementViolations);
                this.lastViolationAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }
}
