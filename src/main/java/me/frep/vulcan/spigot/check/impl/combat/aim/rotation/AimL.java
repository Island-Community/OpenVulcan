package me.frep.vulcan.spigot.check.impl.combat.aim.rotation;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;

@CheckInfo(name = "Aim", type = 'L', complexType = "Direction", description = "Switching directions too quickly.")
public class AimL extends AbstractCheck {

    private float ratio;

    private int samePitchDirectionTicks;

    private static final String VIOLATION_DETAIL_PREFIX = "deltaPitch=";

    public AimL(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isRotationUpdatePacket(packetEvent)) && (this.hasRecentAttack(3))) {
            RotationTracker rotationTracker = this.playerData.getRotationTracker();
            float pitch = rotationTracker.getPitch();
            float previousPitch = this.playerData.getRotationTracker().getPreviousPitch();
            float ratio2 = pitch - previousPitch;
            if ((!(ratio2 < 0.0F) || !(this.ratio > 0.0F)) && (!(ratio2 > 0.0F) || !(this.ratio < 0.0F))) {
                this.samePitchDirectionTicks++;
            } else {
                this.samePitchDirectionTicks = 0;
            }
            boolean condition = this.samePitchDirectionTicks == 0 && Math.abs(ratio2) > 5.0F;
            if (condition) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail(VIOLATION_DETAIL_PREFIX + ratio2);
                }
            } else {
                this.setBuffer();
            }
            this.ratio = ratio2;
        }
    }

    @Override
    public void update() {
    }
}
