package me.frep.vulcan.spigot.check.impl.player.improbable;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Improbable", type = 'F', complexType = "Scaffold", description = "Too many combined Scaffold violations.")
public class ImprobableF extends AbstractCheck {

    private long lastViolationAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX;

    public ImprobableF(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isPositionUpdatePacket(packetEvent)) {
            int scaffoldViolations = this.playerData.getScaffoldViolations();
            if (System.currentTimeMillis() - this.lastViolationAtMillis >= 60000L && scaffoldViolations > VulcanSettings.checksPlayerImprobableFMaxScaffoldViolations) {
                this.fail(VIOLATION_DETAIL_PREFIX + scaffoldViolations);
                this.lastViolationAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }

    static {
        VIOLATION_DETAIL_PREFIX = "scaffoldViolations=";
    }
}
