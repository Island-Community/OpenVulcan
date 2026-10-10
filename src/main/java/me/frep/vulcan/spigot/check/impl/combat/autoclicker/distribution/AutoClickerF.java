package me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.google.common.collect.Lists;
import java.util.Deque;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Auto Clicker", type = 'F', complexType = "Distinct", description = "Not enough distinct values.")
public class AutoClickerF extends AbstractCheck {

    private final Deque<Long> clickIntervals = Lists.newLinkedList();

    private static final String VIOLATION_DETAIL_PREFIX = "distinct=";

    public AutoClickerF(PlayerData playerData) {
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
        int dequeSize = (clickIntervalDifference = clickInterval - 5000L) == 0L ? 0 : (clickIntervalDifference < 0L ? -1 : 1);
        if (abstractChecks == null) {
            if (dequeSize > 0) {
                this.clickIntervals.clear();
                return;
            }
            this.clickIntervals.add(clickInterval);
            dequeSize = this.clickIntervals.size();
        }
        if (abstractChecks == null) {
            if (dequeSize != 50) {
                return;
            }
            dequeSize = MathUtils.distinctCount(this.clickIntervals);
        }
        int dequeSize2 = dequeSize;
        int dequeSize22 = dequeSize2;
        if (abstractChecks == null) {
            if (dequeSize2 >= 13) {
                this.decayBuffer();
            } else {
                double maximumBufferDifference;
                dequeSize22 = (maximumBufferDifference = this.increaseBuffer() - this.maximumBuffer) == 0.0 ? 0 : (maximumBufferDifference < 0.0 ? -1 : 1);
                if (!(dequeSize22 <= 0)) {
                    this.fail(VIOLATION_DETAIL_PREFIX + dequeSize2);
                }
            }
        } else if (!(dequeSize22 <= 0)) {
            this.fail(VIOLATION_DETAIL_PREFIX + dequeSize2);
            this.decayBuffer();
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
