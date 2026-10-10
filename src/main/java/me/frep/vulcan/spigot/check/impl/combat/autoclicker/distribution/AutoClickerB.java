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

@CheckInfo(name = "Auto Clicker", type = 'B', complexType = "Deviation", description = "Too low standard deviation.")
public class AutoClickerB extends AbstractCheck {

    private final Deque<Long> clickIntervals = Lists.newLinkedList();

    public AutoClickerB(PlayerData playerData) {
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
        double deviation = MathUtils.rootSumSquaredDeviations((Collection) deque2);
        double average2 = MathUtils.average((Collection) this.clickIntervals);
        double deviationDifference;
        int index = (deviationDifference = deviation - 167.0) == 0.0 ? 0 : (deviationDifference < 0.0 ? -1 : 1);
        if (abstractChecks == null) {
            if (!(index >= 0)) {
                double maximumBufferDifference;
                index = (maximumBufferDifference = this.increaseBuffer() - this.maximumBuffer) == 0.0 ? 0 : (maximumBufferDifference < 0.0 ? -1 : 1);
                if (index > 0) {
                    this.fail("deviation=" + deviation + " average=" + average2);
                } else {
                    this.decayBuffer();
                }
            }
        } else if (index > 0) {
            this.fail("deviation=" + deviation + " average=" + average2);
            this.decayBuffer();
        } else {
            this.decayBuffer();
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
