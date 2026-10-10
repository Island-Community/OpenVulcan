package me.frep.vulcan.spigot.check.impl.player.timer;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.vehicle.VehicleTracker;

@CheckInfo(name = "Timer", type = 'D', complexType = "Balance", description = "Increased game speed.")
public class TimerD extends AbstractCheck {

    private long lastMovementAtMillis;

    private long balanceMillis;

    private int movementsSinceBalanceReset;

    public TimerD(PlayerData playerData) {
        super(playerData);
        this.lastMovementAtMillis = 0L;
        this.balanceMillis = 0L;
        this.movementsSinceBalanceReset = 0;
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (!((!this.isMovementPacket(packetEvent)) && (!this.isVehicleMovePacket(packetEvent)))) {
            if (!this.isSetbackPending()) {
                long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.JOINED, ExemptType.TPS, ExemptType.VEHICLE });
                if (this.lastMovementAtMillis != 0L && !exempt) {
                    this.balanceMillis += 50L;
                    this.balanceMillis -= currentTimeMillisDifference;
                    if (this.balanceMillis > VulcanSettings.checksPlayerTimerDMaxBalance) {
                        VehicleTracker vehicleTracker = this.playerData.getVehicleTracker();
                        if (vehicleTracker.getEntityType() == null) {
                            if (this.increaseBuffer() > this.maximumBuffer) {
                                this.fail("balance=" + this.balanceMillis + " delay=" + currentTimeMillisDifference);
                            } else {
                                this.decayBuffer();
                            }
                            this.balanceMillis = -200L;
                        }
                    }
                    if (this.playerData.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                        if (this.movementsSinceBalanceReset % 400 == 0) {
                            this.balanceMillis = -200L;
                            this.movementsSinceBalanceReset = 0;
                        }
                    } else if (this.movementsSinceBalanceReset % 3000 == 0) {
                        this.balanceMillis = -200L;
                        this.movementsSinceBalanceReset = 0;
                    }
                }
                this.movementsSinceBalanceReset++;
                this.lastMovementAtMillis = System.currentTimeMillis();
                return;
            }
        }
        if (this.isServerPositionPacket(packetEvent)) {
            if (ExemptionManager.isAtLeast1_9()) {
                this.balanceMillis -= 150L;
            } else {
                this.balanceMillis -= 50L;
            }
        }
    }

    @Override
    public void update() {
    }
}
