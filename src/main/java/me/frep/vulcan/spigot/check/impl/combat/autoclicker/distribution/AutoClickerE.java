package me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Deque;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Auto Clicker", type = 'E', complexType = "Variance", description = "Too low variance.")
public class AutoClickerE extends AbstractCheck {

    private final Deque<Long> clickIntervals = Lists.newLinkedList();

    private static final String VIOLATION_DETAIL_PREFIX = "variance=";

    public AutoClickerE(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        java.util.Deque<Long> deque2;
        AbstractCheck[] abstractChecks;
        abstractChecks = AutoClickerT.getAbstractChecks();
        boolean armAnimationPacket = this.isArmAnimationPacket(packetEvent);
        if (abstractChecks == null) {
            if (!armAnimationPacket) {
                return;
            }
            armAnimationPacket = this.isExempt(new ExemptType[] { ExemptType.AUTOCLICKER });
            if (armAnimationPacket) {
                return;
            }
        } else if (armAnimationPacket) {
            return;
        }
        PlayerData playerData = this.playerData;
        ClickTimingData clickTimingData = playerData.getClickTimingData();
        long clickInterval = clickTimingData.getClickInterval();
        long clickIntervalDifference;
        int dequeSize = (clickIntervalDifference = clickInterval - 5000L) == 0L ? 0 : (clickIntervalDifference < 0L ? -1 : 1);
        if (abstractChecks == null) {
            if (dequeSize > 0) {
                this.clickIntervals.clear();
                return;
            }
            this.clickIntervals.add(clickInterval);
            deque2 = this.clickIntervals;
            dequeSize = this.clickIntervals.size();
            if (dequeSize != 50) {
                return;
            }
            deque2 = this.clickIntervals;
        } else {
            if (dequeSize != 50) {
                return;
            }
            deque2 = this.clickIntervals;
        }
        double amount = MathUtils.sumSquaredDeviations((Collection) deque2);
        double amount2 = amount / 1000.0;
        double amount3;
        int index = (amount3 = amount2 - 28.2) == 0.0 ? 0 : (amount3 < 0.0 ? -1 : 1);
        if (abstractChecks == null) {
            if (index >= 0) {
                this.decayBuffer();
            } else {
                double maximumBufferDifference;
                index = (maximumBufferDifference = this.increaseBuffer() - this.maximumBuffer) == 0.0 ? 0 : (maximumBufferDifference < 0.0 ? -1 : 1);
                if (!(index <= 0)) {
                    this.fail(VIOLATION_DETAIL_PREFIX + amount2);
                }
            }
        } else if (!(index <= 0)) {
            this.fail(VIOLATION_DETAIL_PREFIX + amount2);
            this.decayBuffer();
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
