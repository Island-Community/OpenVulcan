package me.frep.vulcan.spigot.check.impl.combat.autoclicker.rate;

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

@CheckInfo(name = "Auto Clicker", type = 'O', complexType = "Spikes", description = "Impossible spike in CPS.")
public class AutoClickerO extends AbstractCheck {

    private final Deque<Long> clickIntervals;

    private double previousCps;

    public AutoClickerO(PlayerData playerData) {
        super(playerData);
        this.clickIntervals = Lists.newLinkedList();
        this.previousCps = -1.0;
        AutoClickerT.setAbstractChecks(new AbstractCheck[1]);
    }

    public void handlePacket(PacketEvent packetEvent) {
        int index;
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
            if (dequeSize != 10) {
                return;
            }
            deque2 = this.clickIntervals;
        } else {
            if (dequeSize != 10) {
                return;
            }
            deque2 = this.clickIntervals;
        }
        double previousCps2 = MathUtils.clicksPerSecond((Collection) deque2);
        if (abstractChecks == null) {
            if (this.previousCps > 0.0) {
                double previousCps2Difference;
                double average;
                previousCps2Difference = Math.abs(previousCps2 - this.previousCps);
                average = (this.previousCps + previousCps2) / 2.0;
                double averageDifference;
                index = (averageDifference = average - 9.25) == 0.0 ? 0 : (averageDifference < 0.0 ? -1 : 1);
                if (index <= 0) {
                    index = 0;
                } else {
                    double previousCps2DifferenceDifference;
                    index = (previousCps2DifferenceDifference = previousCps2Difference - 2.8) == 0.0 ? 0 : (previousCps2DifferenceDifference < 0.0 ? -1 : 1);
                    if (index > 0) {
                        index = 1;
                    } else {
                        index = 0;
                    }
                }
                int index2 = index;
                int index3 = index2;
                if (index2 == 0) {
                    this.decayBuffer();
                    this.previousCps = previousCps2;
                } else {
                    double previousCpsDifference;
                    index3 = (previousCpsDifference = this.increaseBuffer() - this.previousCps) == 0.0 ? 0 : (previousCpsDifference < 0.0 ? -1 : 1);
                    if (!(index3 <= 0)) {
                        this.fail("average=" + average + " difference=" + previousCps2Difference);
                    }
                    this.previousCps = previousCps2;
                }
            } else {
                this.previousCps = previousCps2;
            }
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
