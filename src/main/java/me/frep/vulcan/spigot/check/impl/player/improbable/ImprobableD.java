package me.frep.vulcan.spigot.check.impl.player.improbable;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Improbable", type = 'D', complexType = "Auto Clicker", description = "Too many combined autoclicker violations.")
public class ImprobableD extends AbstractCheck {

    private long lastViolationAtMillis;

    private static final String VIOLATION_DETAIL_PREFIX = "autoClickerViolations=";

    public ImprobableD(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isArmAnimationPacket(packetEvent)) && (!this.isExempt(new ExemptType[] { ExemptType.AUTOCLICKER }))) {
            int autoClickerViolations = this.playerData.getAutoClickerViolations();
            if (System.currentTimeMillis() - this.lastViolationAtMillis >= 60000L && autoClickerViolations > VulcanSettings.checksPlayerImprobableDMaxAutoclickerViolations) {
                this.fail(VIOLATION_DETAIL_PREFIX + autoClickerViolations);
                this.lastViolationAtMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void update() {
    }
}
