package me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Auto Clicker", type = 'J', complexType = "Range", description = "Impossible consistency.")
public class AutoClickerJ extends AbstractCheck {

    private final List<Long> clickIntervals = Lists.newArrayList();

    private static final String VIOLATION_DETAIL_PREFIX = "range=";

    public AutoClickerJ(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        java.lang.Object items2;
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
        int itemsSize = (clickIntervalDifference = clickInterval - 5000L) == 0L ? 0 : (clickIntervalDifference < 0L ? -1 : 1);
        if (abstractChecks == null) {
            if (itemsSize > 0) {
                this.clickIntervals.clear();
                return;
            }
            this.clickIntervals.add(clickInterval);
            items2 = this.clickIntervals;
            itemsSize = this.clickIntervals.size();
            if (itemsSize != 10) {
                return;
            }
            Collections.sort(this.clickIntervals);
            items2 = this.clickIntervals.get(this.clickIntervals.size() - 1);
        } else {
            if (itemsSize != 10) {
                return;
            }
            Collections.sort(this.clickIntervals);
            items2 = this.clickIntervals.get(this.clickIntervals.size() - 1);
        }
        long items2Difference = (Long) items2 - (Long) this.clickIntervals.get(0);
        long items2DifferenceDifference;
        int index = (items2DifferenceDifference = items2Difference - 50L) == 0L ? 0 : (items2DifferenceDifference < 0L ? -1 : 1);
        if (abstractChecks == null) {
            if (index >= 0) {
                this.decayBuffer();
            } else {
                double maximumBufferDifference;
                index = (maximumBufferDifference = this.increaseBuffer() - this.maximumBuffer) == 0.0 ? 0 : (maximumBufferDifference < 0.0 ? -1 : 1);
                if (!(index <= 0)) {
                    this.fail(VIOLATION_DETAIL_PREFIX + items2Difference);
                }
            }
        } else if (!(index <= 0)) {
            this.fail(VIOLATION_DETAIL_PREFIX + items2Difference);
            this.decayBuffer();
        }
        this.clickIntervals.clear();
    }

    @Override
    public void update() {
    }
}
