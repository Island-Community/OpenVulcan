package me.frep.vulcan.spigot.check.impl.player.badpackets.sequence;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Bad Packets", type = 'M', complexType = "Post BlockPlace", description = "Post BlockPlace packets.")
public class BadPacketsM extends AbstractCheck {

    private long lastMovementAtMillis = 0L;

    private boolean placementAfterMovement = false;

    public BadPacketsM(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if (this.isMovementPacket(packetEvent)) {
            long delay = System.currentTimeMillis() - this.lastMovementAtMillis;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.CREATIVE, ExemptType.SPECTATOR, ExemptType.WEB, ExemptType.LIQUID, ExemptType.EMPTIED_BUCKET });
            if (this.playerData.getPlayer() == null) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getTicksSinceBucketUse() >= 50) {
                if (!this.playerData.getPlayer().getItemInHand().getType().toString().contains("BUCKET")) {
                    if ((ExemptionManager.isAtLeast1_9()) && (this.playerData.getPlayer().getInventory().getItemInOffHand().getType().toString().contains("BUCKET"))) {
                        condition = true;
                    } else {
                        condition = false;
                    }
                } else {
                    condition = true;
                }
            } else {
                condition = true;
            }
            boolean condition2 = condition;
            if (this.placementAfterMovement && !exempt && !condition2) {
                if (delay > 40L && delay < 100L) {
                    if (this.increaseBuffer() > this.maximumBuffer) {
                        this.fail("delay=" + delay);
                    }
                } else {
                    this.decayBuffer();
                }
                this.placementAfterMovement = false;
            }
            this.lastMovementAtMillis = System.currentTimeMillis();
        } else if (this.isBlockPlacementPacket(packetEvent)) {
            long currentTimeMillisDifference = System.currentTimeMillis() - this.lastMovementAtMillis;
            if (currentTimeMillisDifference < 5L) {
                this.placementAfterMovement = true;
            }
        }
    }

    @Override
    public void update() {
    }
}
