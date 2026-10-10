package me.frep.vulcan.spigot.check.impl.combat.criticals;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Criticals", type = 'A', complexType = "Ground", description = "Tried to hit Critical hit on ground.")
public class CriticalsA extends AbstractCheck {

    public CriticalsA(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if ((this.isAttackPacket(packetEvent)) && (this.getAttackTicks() < 2)) {
            double deltaY;
            double yRemainder;
            boolean condition;
            CollisionState collisionState = this.playerData.getCollisionState();
            deltaY = Math.abs(collisionState.getDeltaY());
            CollisionState collisionState2 = this.playerData.getCollisionState();
            yRemainder = collisionState2.getY() % 0.015625;
            condition = (this.isExempt(new ExemptType[] { ExemptType.LIQUID, ExemptType.TRAPDOOR, ExemptType.COLLIDING_VERTICALLY, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.ENDER_PEARL, ExemptType.CHORUS_FRUIT, ExemptType.ELYTRA, ExemptType.SERVER_POSITION, ExemptType.CARPET, ExemptType.VEHICLE, ExemptType.FARMLAND, ExemptType.GLIDING, ExemptType.SLAB })) || (((this.playerData.getCollisionState()).isLantern()) || (((this.playerData.getCollisionState()).isFlowerPot()) || ((this.playerData.getCollisionState().isNearDirtPath()) || (this.playerData.getCollisionState().isNearCactus()))));
            boolean condition2 = condition;
            if (Math.abs(deltaY - 7.504558659832128E-4) < 0.001) {
                return;
            }
            boolean condition3 = deltaY < 0.001 && deltaY > 0.0 && yRemainder < 1.0E-5 || deltaY == 0.0625;
            if (condition3 && !condition2) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("deltaY=" + deltaY + " modulo=" + yRemainder);
                }
            } else {
                this.decayBuffer();
            }
        }
    }

    @Override
    public void update() {
    }
}
