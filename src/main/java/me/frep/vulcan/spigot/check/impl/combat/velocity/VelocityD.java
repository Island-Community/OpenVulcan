package me.frep.vulcan.spigot.check.impl.combat.velocity;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Velocity", type = 'D', complexType = "Horizontal", description = "Horizontal velocity modifications.", experimental = true)
public class VelocityD extends AbstractCheck {

    public VelocityD(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isMovementPacket(packetEvent)) {
            WrapperPlayClientInteractEntity lastInteractionPacket = this.playerData.getLastInteractionPacket();
            lastInteractionPacket.getAction();
        }
    }

    @Override
    public void update() {
    }

    public float getRatio(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        double movementSpeed = motionState.getMovementSpeed();
        MotionState motionState2 = playerData.getMotionState();
        movementSpeed += motionState2.getSpeedAmplifier() * 0.2 * movementSpeed;
        return (float) movementSpeed;
    }
}
