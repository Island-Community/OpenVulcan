package me.frep.vulcan.spigot.check.impl.combat.criticals;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Criticals", type = 'B', complexType = "Modulo", description = "Tried to hit Critical hit on ground.")
public class CriticalsB extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX;

    public CriticalsB(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isAttackPacket(packetEvent)) && (this.getAttackTicks() < 2)) {
            CollisionState collisionState = this.playerData.getCollisionState();
            double yRemainder = collisionState.getY() % 0.015625;
            boolean condition = yRemainder < 1.0E-5 && yRemainder > 0.0;
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.LIQUID, ExemptType.TRAPDOOR, ExemptType.COLLIDING_VERTICALLY, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.ENDER_PEARL, ExemptType.CHORUS_FRUIT, ExemptType.SERVER_POSITION, ExemptType.VEHICLE, ExemptType.FLIGHT, ExemptType.SLAB, ExemptType.STAIRS });
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail(VIOLATION_DETAIL_PREFIX + yRemainder);
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }

    static {
        VIOLATION_DETAIL_PREFIX = "modulo=";
    }
}
