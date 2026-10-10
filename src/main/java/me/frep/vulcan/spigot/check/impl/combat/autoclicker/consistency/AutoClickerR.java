package me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency;

import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerT;
import com.github.retrooper.packetevents.event.PacketEvent;
import java.util.Collection;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.collection.list.EvictingList;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Auto Clicker", type = 'R', complexType = "Consistency", description = "Impossible consistency.")
public class AutoClickerR extends AbstractCheck {

    private final EvictingList<Long> clickIntervals = new EvictingList<>(30);

    public AutoClickerR(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        double average2;
        long index2;
        AbstractCheck[] abstractCheckArray;
        long longValue4;
        int index;
        boolean armAnimationPacket;
        abstractCheckArray = AutoClickerT.getAbstractChecks();
        armAnimationPacket = this.isArmAnimationPacket((packetEvent));
        if ((abstractCheckArray == null)) {
            if (!armAnimationPacket)
                return;
            ExemptType[] value = new ExemptType[] { ExemptType.AUTOCLICKER };
            armAnimationPacket = this.isExempt(value);
            if (armAnimationPacket)
                return;
        } else if (armAnimationPacket)
            return;
        long clickInterval = this.playerData.getClickTimingData().getClickInterval();
        long clickIntervalDifference = clickInterval - 5000L;
        index = clickIntervalDifference == 0L ? 0 : (clickIntervalDifference < 0L ? -1 : 1);
        if (abstractCheckArray == null) {
            if (index > 0) {
                this.clickIntervals.clear();
                return;
            }
            this.clickIntervals.add(clickInterval);
            index = this.clickIntervals.isFull() ? 1 : 0;
        }
        if ((abstractCheckArray == null)) {
            if (index == 0)
                return;
            index = (int) this.clickIntervals.stream().filter(longValue -> AutoClickerR.isLongClickInterval(longValue)).count();
        }
        index2 = index;
        Collection<Long> clickIntervals2 = this.clickIntervals;
        average2 = MathUtils.average(clickIntervals2);
        longValue4 = index2;
        if ((abstractCheckArray == null)) {
            if ((longValue4 == 0L)) {
                double maximumBufferDifference = this.increaseBuffer() - this.maximumBuffer;
                longValue4 = maximumBufferDifference == 0.0 ? 0 : (maximumBufferDifference > 0.0 ? 1 : -1);
                if (longValue4 <= 0)
                    return;
                Object adjustedAverage2 = null;
                adjustedAverage2 = "outliers=" + (int) index2 + " average=" + average2;
                this.fail(adjustedAverage2);
                return;
            }
        } else {
            if (longValue4 <= 0)
                return;
            Object adjustedAverage22 = null;
            adjustedAverage22 = "outliers=" + (int) index2 + " average=" + average2;
            this.fail(adjustedAverage22);
        }
        this.decayBuffer();
    }

    @Override
    public void update() {
    }

    private static boolean isLongClickInterval(Long longValue) {
        AbstractCheck[] abstractChecks = AutoClickerT.getAbstractChecks();
        long longValue2;
        int index = (longValue2 = longValue - 150L) == 0L ? 0 : (longValue2 < 0L ? -1 : 1);
        if (abstractChecks == null) {
            index = index > 0 ? 1 : 0;
        }
        return (index != 0);
    }
}
