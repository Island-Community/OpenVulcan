package me.frep.vulcan.spigot.check.impl.movement.nosaddle;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "No Saddle", type = 'A', complexType = "Spoof", description = "Controlling an entity without a saddle.")
public class NoSaddleA extends AbstractCheck {

    public NoSaddleA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof PlayerMoveEvent)
            checkVehicleMovement();
    }

    private void checkVehicleMovement() {
        if (ExemptionManager.isBefore1_9())
            return;
        Player player = playerData.getPlayer();
        if (player == null || !(player.getVehicle() instanceof Horse horse) || horse.isLeashed())
            return;
        AttributeInstance movementSpeed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (movementSpeed == null)
            return;
        double horizontalSpeed = playerData.getCollisionState().getVehicleHorizontalSpeed();
        double difference = movementSpeed.getBaseValue() * 2.5 + 0.1 - horizontalSpeed;
        if (horse.getInventory().getSaddle() == null && difference < 0.3 && horizontalSpeed > 0.15 && playerData.getCollisionState().getTicksInVehicle() > 5) {
            if (increaseBuffer() > maximumBuffer) {
                fail("diff=" + difference + " deltaXZ=" + horizontalSpeed);
                if (VulcanSettings.checksPlayerBadpacketsVKickOut)
                    MainThreadExecutor.execute(() -> {
                        Player currentPlayer = playerData.getPlayer();
                        if (currentPlayer != null)
                            currentPlayer.leaveVehicle();
                    });
            }
        } else
            decayBuffer();
    }
}
