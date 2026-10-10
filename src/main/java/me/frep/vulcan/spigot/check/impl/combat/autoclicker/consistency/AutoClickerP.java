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

@CheckInfo(name = "Auto Clicker", type = 'P', complexType = "Identical", description = "Identical statistical values.")
public class AutoClickerP extends AbstractCheck {

    private final Deque<Long> clickIntervals = Lists.newLinkedList();

    private double previousDeviation;

    private double previousSkewness;

    private double previousKurtosis;

    public AutoClickerP(PlayerData playerData) {
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
            if (dequeSize != 15) {
                return;
            }
            deque2 = this.clickIntervals;
        } else {
            if (dequeSize != 15) {
                return;
            }
            deque2 = this.clickIntervals;
        }
        double deviation = MathUtils.rootSumSquaredDeviations((Collection) deque2);
        double skewness = MathUtils.skewness((Collection) this.clickIntervals);
        double kurtosis = MathUtils.kurtosis((Collection) this.clickIntervals);
        if (abstractChecks == null) {
            boolean identical = deviation - this.previousDeviation == 0.0 && skewness - this.previousSkewness == 0.0 && kurtosis - this.previousKurtosis == 0.0;
            if (!identical) {
                this.setBuffer();
            } else {
                double bufferDifference = this.increaseBuffer() - this.previousDeviation;
                if (!(bufferDifference <= 0.0)) {
                    this.fail();
                }
            }
        } else {
            double deviationDifference = deviation - this.previousDeviation;
            if (!(deviationDifference <= 0.0)) {
                this.fail();
                this.setBuffer();
            }
        }
        this.previousDeviation = deviation;
        this.previousSkewness = skewness;
        this.previousKurtosis = kurtosis;
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
