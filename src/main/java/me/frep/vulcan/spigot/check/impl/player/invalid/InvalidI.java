package me.frep.vulcan.spigot.check.impl.player.invalid;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Invalid", type = 'I', complexType = "Invalid Y movement", description = "Impossible Y change.")
public class InvalidI extends AbstractCheck {

    public InvalidI(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent event) {
        if (!isPositionUpdatePacket(event))
            return;
        if (isExempt(new ExemptType[] { ExemptType.FIREBALL, ExemptType.VEHICLE, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.RIPTIDE, ExemptType.HONEY, ExemptType.GLIDING, ExemptType.LEVITATION, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.FLIGHT, ExemptType.JUMP_BOOST_RAN_OUT, ExemptType.ELYTRA, ExemptType.BUBBLE_COLUMN, ExemptType.WALL, ExemptType.CREATIVE, ExemptType.SHULKER_BOX, ExemptType.ANVIL, ExemptType.SLIME, ExemptType.PISTON, ExemptType.TURTLE_EGG, ExemptType.DOOR, ExemptType.DEATH, ExemptType.ENDER_PEARL, ExemptType.PLACED_SLIME, ExemptType.CHORUS_FRUIT, ExemptType.MYTHIC_MOB, ExemptType.BLOCK_BREAK, ExemptType.FISHING_ROD, ExemptType.SLEEPING, ExemptType.SHULKER }) || playerData.getVelocityTracker().isPendingVelocity() || playerData.getVelocityTracker().isPendingExplosion())
            return;
        double delta = playerData.getCollisionState().getDeltaY();
        if (Math.abs(delta) > 2.5)
            fail("deltaY=" + delta);
        else
            decayBuffer();
    }

    @Override
    public void update() {
    }
}
