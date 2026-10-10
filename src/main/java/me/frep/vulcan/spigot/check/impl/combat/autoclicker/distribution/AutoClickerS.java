package me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.collection.list.EvictingList;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Auto Clicker", type = 'S', complexType = "Distinct", description = "Checks for amount of distinct delays.")
public class AutoClickerS extends AbstractCheck {

    private final EvictingList<Long> clickIntervals = new EvictingList<>(25);

    private static final String VIOLATION_DETAIL_PREFIX = "distinct=";

    public AutoClickerS(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
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
        int index = (clickIntervalDifference = clickInterval - 5000L) == 0L ? 0 : (clickIntervalDifference < 0L ? -1 : 1);
        if (abstractChecks == null) {
            if (index > 0) {
                this.clickIntervals.clear();
                return;
            }
            this.clickIntervals.add(clickInterval);
            index = (this.clickIntervals.isFull() ? 1 : 0);
        }
        if (abstractChecks == null) {
            if (index == 0) {
                return;
            }
            index = MathUtils.distinctCount(this.clickIntervals);
        }
        int index2;
        index2 = index;
        int index3 = index2;
        if (abstractChecks == null) {
            if (!(index2 >= 6)) {
                double maximumBufferDifference;
                index3 = (maximumBufferDifference = this.increaseBuffer() - this.maximumBuffer) == 0.0 ? 0 : (maximumBufferDifference < 0.0 ? -1 : 1);
                if (index3 <= 0) {
                    return;
                }
                this.fail(VIOLATION_DETAIL_PREFIX + index2);
                return;
            }
        } else {
            if (index3 <= 0) {
                return;
            }
            this.fail(VIOLATION_DETAIL_PREFIX + index2);
        }
        this.decayBuffer();
    }

    @Override
    public void update() {
    }
}
