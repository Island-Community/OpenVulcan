package me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency;

import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerT;
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

@CheckInfo(name = "Auto Clicker", type = 'M', complexType = "Variance Difference", description = "Similar variance values.")
public class AutoClickerM extends AbstractCheck {

    private final Deque<Long> clickIntervals = Lists.newLinkedList();

    private double previousSquaredDeviations;

    private static final String VIOLATION_DETAIL_PREFIX = "difference=";

    public AutoClickerM(PlayerData playerData) {
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
        double previousSquaredDeviations2;
        previousSquaredDeviations2 = MathUtils.sumSquaredDeviations((Collection) deque2) / 1000.0;
        double previousSquaredDeviations2Difference = Math.abs(previousSquaredDeviations2 - this.previousSquaredDeviations);
        double previousSquaredDeviations2DifferenceDifference;
        int index = (previousSquaredDeviations2DifferenceDifference = previousSquaredDeviations2Difference - 5.28) == 0.0 ? 0 : (previousSquaredDeviations2DifferenceDifference < 0.0 ? -1 : 1);
        if (abstractChecks == null) {
            if (index >= 0) {
                this.decayBuffer();
                this.previousSquaredDeviations = previousSquaredDeviations2;
            } else {
                double previousSquaredDeviationsDifference;
                index = (previousSquaredDeviationsDifference = this.increaseBuffer() - this.previousSquaredDeviations) == 0.0 ? 0 : (previousSquaredDeviationsDifference < 0.0 ? -1 : 1);
                if (!(index <= 0)) {
                    this.fail(VIOLATION_DETAIL_PREFIX + previousSquaredDeviations2Difference);
                }
                this.previousSquaredDeviations = previousSquaredDeviations2;
            }
        } else {
            if (!(index <= 0)) {
                this.fail(VIOLATION_DETAIL_PREFIX + previousSquaredDeviations2Difference);
                this.decayBuffer();
            }
            this.previousSquaredDeviations = previousSquaredDeviations2;
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
