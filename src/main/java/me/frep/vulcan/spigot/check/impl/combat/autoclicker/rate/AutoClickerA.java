package me.frep.vulcan.spigot.check.impl.combat.autoclicker.rate;

import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerT;
import com.github.retrooper.packetevents.event.PacketEvent;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Deque;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Auto Clicker", type = 'A', complexType = "Limit", description = "Left clicking too quickly.")
public class AutoClickerA extends AbstractCheck {

    private final Deque<Long> clickIntervals;

    private long lastClickAtMillis;

    public AutoClickerA(PlayerData playerData) {
        super(playerData);
        this.clickIntervals = Lists.newLinkedList();
        this.lastClickAtMillis = 0L;
    }

    public void handlePacket(PacketEvent packetEvent) {
        long lastClickAtMillis2;
        double amount;
        double clicksPerSecond2;
        boolean armAnimationPacket;
        AbstractCheck[] abstractCheckArray;
        abstractCheckArray = AutoClickerT.getAbstractChecks();
        armAnimationPacket = this.isArmAnimationPacket((packetEvent));
        if ((abstractCheckArray == null)) {
            if (!armAnimationPacket)
                return;
            armAnimationPacket = this.isExempt((new ExemptType[] { ExemptType.AUTOCLICKER_NON_DIG }));
        }
        if (armAnimationPacket)
            return;
        lastClickAtMillis2 = System.currentTimeMillis();
        long lastClickAtMillis2Difference = lastClickAtMillis2 - this.lastClickAtMillis;
        this.clickIntervals.add(lastClickAtMillis2Difference);
        if ((abstractCheckArray == null)) {
            if ((this.clickIntervals.size() == 20)) {
                Collection<Long> clickIntervals2 = this.clickIntervals;
                clicksPerSecond2 = MathUtils.clicksPerSecond(clickIntervals2);
                double clicksPerSecond2Difference = clicksPerSecond2 - (double) VulcanSettings.checksCombatAutoclickerAMaxCps;
                amount = clicksPerSecond2Difference == 0.0 ? 0 : (clicksPerSecond2Difference > 0.0 ? 1 : -1);
                if (!(amount <= 0)) {
                    double clicksPerSecond2Difference2 = clicksPerSecond2 - 5.0;
                    amount = clicksPerSecond2Difference2 == 0.0 ? 0 : (clicksPerSecond2Difference2 > 0.0 ? 1 : -1);
                    if (amount > 0) {
                        this.fail("cps=" + clicksPerSecond2);
                    }
                }
                this.clickIntervals.clear();
            }
        }
        this.lastClickAtMillis = lastClickAtMillis2;
    }

    @Override
    public void update() {
    }
}
