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

@CheckInfo(name = "Auto Clicker", type = 'K', complexType = "Average Difference", description = "Similar average values.")
public class AutoClickerK extends AbstractCheck {

    private final Deque<Long> clickIntervals = Lists.newLinkedList();

    private double previousAverage;

    private static final String VIOLATION_DETAIL_PREFIX = "difference=";

    public AutoClickerK(PlayerData playerData) {
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
        double previousAverage2;
        previousAverage2 = MathUtils.average((Collection) deque2);
        double previousAverage2Difference = Math.abs(previousAverage2 - this.previousAverage);
        double previousAverage2DifferenceDifference;
        int index = (previousAverage2DifferenceDifference = previousAverage2Difference - 2.56) == 0.0 ? 0 : (previousAverage2DifferenceDifference < 0.0 ? -1 : 1);
        if (abstractChecks == null) {
            if (index >= 0) {
                this.decayBuffer();
                this.previousAverage = previousAverage2;
            } else {
                double previousAverageDifference;
                index = (previousAverageDifference = this.increaseBuffer() - this.previousAverage) == 0.0 ? 0 : (previousAverageDifference < 0.0 ? -1 : 1);
                if (!(index <= 0)) {
                    this.fail(VIOLATION_DETAIL_PREFIX + previousAverage2Difference);
                }
                this.previousAverage = previousAverage2;
            }
        } else {
            if (!(index <= 0)) {
                this.fail(VIOLATION_DETAIL_PREFIX + previousAverage2Difference);
                this.decayBuffer();
            }
            this.previousAverage = previousAverage2;
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
