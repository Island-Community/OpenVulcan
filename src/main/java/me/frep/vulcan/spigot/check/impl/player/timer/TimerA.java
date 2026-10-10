package me.frep.vulcan.spigot.check.impl.player.timer;

import com.github.retrooper.packetevents.event.PacketEvent;
import java.util.Collection;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.collection.list.EvictingList;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;

@CheckInfo(name = "Timer", type = 'A', complexType = "Average", description = "Increased game speed.")
public class TimerA extends AbstractCheck {

    private final EvictingList movementIntervals = new EvictingList(50);

    private long lastMovementAtMillis;

    public TimerA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (!((!this.isMovementPacket(packetEvent)) && (!this.isVehicleMovePacket(packetEvent)))) {
            if (!this.isSetbackPending()) {
                long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.JOINED, ExemptType.TPS, ExemptType.VEHICLE });
                if (currentTimeMillisDifference > 4L && !exempt) {
                    this.movementIntervals.add(currentTimeMillisDifference);
                }
                if (this.movementIntervals.isFull()) {
                    double average2 = MathUtils.average((Collection) this.movementIntervals);
                    double scaledAverage2 = 50.0 / average2;
                    double speed = scaledAverage2 * 100.0;
                    double checksPlayerTimerAMaxSpeed2 = VulcanSettings.checksPlayerTimerAMaxSpeed;
                    if (this.isExempt(new ExemptType[] { ExemptType.WEB })) {
                        checksPlayerTimerAMaxSpeed2 += 0.05;
                    }
                    if (scaledAverage2 >= checksPlayerTimerAMaxSpeed2) {
                        if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("speed=" + speed + "% delay=" + currentTimeMillisDifference);
                        }
                    } else {
                        this.decayBuffer();
                    }
                }
                this.lastMovementAtMillis = System.currentTimeMillis();
                return;
            }
        }
        if (this.isServerPositionPacket(packetEvent)) {
            this.movementIntervals.add(150L);
        } else if (this.playerData.isAtLeast1_17()) {
            if ((!this.isBlockPlacementPacket(packetEvent)) && (!this.isUseItemPacket(packetEvent))) {
                return;
            }
            this.movementIntervals.add(50L);
        }
    }

    @Override
    public void update() {
    }
}
