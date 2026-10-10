package me.frep.vulcan.spigot.tracker.velocity;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import java.util.LinkedList;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.confirmation.ConfirmationTracker;

public class VelocityTracker {

    private final PlayerData playerData;

    private final LinkedList<Vector3d> pendingVelocities = new LinkedList<>();

    private final LinkedList<Vector3d> pendingExplosions = new LinkedList<>();

    private boolean pendingVelocity = false;

    private boolean pendingExplosion = false;

    private double maximumHorizontalVelocity = 0.0;

    private double maximumVerticalVelocity = 0.0;

    private double velocityX = 0.0;

    private double velocityY = 0.0;

    private double velocityZ = 0.0;

    private double horizontalVelocity = 0.0;

    private double explosionX = 0.0;

    private double explosionY = 0.0;

    private double explosionZ = 0.0;

    private double horizontalExplosion = 0.0;

    private double combinedExplosionY = 0.0;

    private double combinedHorizontalExplosion = 0.0;

    private double lastExplosionY = 0.0;

    private double lastHorizontalExplosion = 0.0;

    private double lastVelocityY = 0.0;

    private int ticksSinceVelocity = 0;

    private int ticksSinceExplosion = 0;

    private int ticksSinceVelocityPacket = 0;

    private int ticksSinceLargeVelocity = 1000;

    private int ticksSinceUnloadedPosition = 1000;

    public VelocityTracker(PlayerData data) {
        this.playerData = data;
    }

    public void handleVelocity(WrapperPlayServerEntityVelocity packet, PacketSendEvent event) {
        ConfirmationTracker confirmations = this.playerData.getConfirmationTracker();
        confirmations.runAfterConfirmation(() -> this.queueEntityVelocity(packet));
        confirmations.runAfterNextConfirmation(this::discardAcknowledgedVelocity);
        event.getTasksAfterSend().add(() -> this.confirmVelocitySent());
    }

    public void handleExplosion(WrapperPlayServerExplosion packet, PacketSendEvent event) {
        Vector3f knockback = packet.getPlayerMotion();
        if ((knockback != null && (knockback.getX() != 0.0F || knockback.getY() != 0.0F || knockback.getZ() != 0.0F)) && (packet.getKnockback() != null)) {
            ConfirmationTracker confirmations = this.playerData.getConfirmationTracker();
            confirmations.runAfterConfirmation(() -> this.queueExplosionKnockback(packet));
            confirmations.runAfterNextConfirmation(this::discardAcknowledgedExplosion);
            event.getTasksAfterSend().add(() -> this.confirmExplosionSent());
        }
    }

    public void processMovement(WrapperPlayClientPlayerFlying packet) {
        if (!packet.hasPositionChanged()) {
            return;
        }
        this.pendingVelocity = !this.pendingVelocities.isEmpty();
        this.pendingExplosion = !this.pendingExplosions.isEmpty();
        for (Vector3d vector3d : this.pendingVelocities) {
            this.velocityX = vector3d.getX();
            this.velocityY = vector3d.getY();
            this.velocityZ = vector3d.getZ();
            this.horizontalVelocity = Math.hypot(this.velocityX, this.velocityZ);
            this.maximumVerticalVelocity = Math.max(this.maximumVerticalVelocity, vector3d.getY());
            this.maximumHorizontalVelocity = Math.max(this.maximumHorizontalVelocity, Math.hypot(vector3d.getX(), vector3d.getZ()));
            if (!(this.maximumHorizontalVelocity > 0.75))
                continue;
            this.ticksSinceLargeVelocity = 0;
        }
        this.combinedExplosionY = 0.0;
        this.combinedHorizontalExplosion = 0.0;
        for (Vector3d vector3d : this.pendingExplosions) {
            this.explosionX = vector3d.getX();
            this.explosionY = vector3d.getY();
            this.explosionZ = vector3d.getZ();
            this.horizontalExplosion = Math.hypot(this.explosionX, this.explosionZ);
            this.combinedExplosionY += this.explosionY;
            this.combinedHorizontalExplosion += this.horizontalExplosion;
        }
        if (this.combinedExplosionY > 0.0) {
            this.lastExplosionY = this.combinedExplosionY;
        }
        if (this.combinedHorizontalExplosion > 0.0) {
            this.lastHorizontalExplosion = this.combinedHorizontalExplosion;
        }
        if (packet.isOnGround()) {
            this.lastExplosionY = 0.0;
        }
        this.ticksSinceVelocity = this.pendingVelocity ? 0 : ++this.ticksSinceVelocity;
        this.ticksSinceExplosion = this.pendingExplosion ? 0 : ++this.ticksSinceExplosion;
        ++this.ticksSinceVelocityPacket;
        ++this.ticksSinceLargeVelocity;
        ++this.ticksSinceUnloadedPosition;
        double x = this.playerData.getCollisionState().getX();
        double y = this.playerData.getCollisionState().getY();
        double z = this.playerData.getCollisionState().getZ();
        if (this.playerData.getWorldBlockTracker().hasTrackedBlockNearby(x, y, z)) {
            this.ticksSinceUnloadedPosition = 0;
        }
    }

    public void update() {
    }

    public LinkedList getPendingVelocities() {
        return this.pendingVelocities;
    }

    public boolean isPendingVelocity() {
        return this.pendingVelocity;
    }

    public boolean isPendingExplosion() {
        return this.pendingExplosion;
    }

    public double getMaximumHorizontalVelocity() {
        return this.maximumHorizontalVelocity;
    }

    public double getMaximumVerticalVelocity() {
        return this.maximumVerticalVelocity;
    }

    public double getVelocityX() {
        return this.velocityX;
    }

    public double getVelocityY() {
        return this.velocityY;
    }

    public double getVelocityZ() {
        return this.velocityZ;
    }

    public double getCombinedExplosionY() {
        return this.combinedExplosionY;
    }

    public double getCombinedHorizontalExplosion() {
        return this.combinedHorizontalExplosion;
    }

    public double getLastExplosionY() {
        return this.lastExplosionY;
    }

    public double getLastHorizontalExplosion() {
        return this.lastHorizontalExplosion;
    }

    public double getLastVelocityY() {
        return this.lastVelocityY;
    }

    public int getTicksSinceVelocity() {
        return this.ticksSinceVelocity;
    }

    public int getTicksSinceExplosion() {
        return this.ticksSinceExplosion;
    }

    public int getTicksSinceVelocityPacket() {
        return this.ticksSinceVelocityPacket;
    }

    public int getTicksSinceLargeVelocity() {
        return this.ticksSinceLargeVelocity;
    }

    public int getTicksSinceUnloadedPosition() {
        return this.ticksSinceUnloadedPosition;
    }

    private void confirmExplosionSent() {
        this.playerData.getConfirmationTracker().sendConfirmation(false);
    }

    private void discardAcknowledgedExplosion() {
        CollisionState collisionState = this.playerData.getCollisionState();
        collisionState.queueAcknowledgedTask(this.pendingExplosions::poll);
    }

    private void queueExplosionKnockback(WrapperPlayServerExplosion wrapperPlayServerExplosion) {
        this.pendingExplosions.add(wrapperPlayServerExplosion.getKnockback());
    }

    private void confirmVelocitySent() {
        this.playerData.getConfirmationTracker().sendConfirmation(false);
    }

    private void discardAcknowledgedVelocity() {
        CollisionState collisionState = this.playerData.getCollisionState();
        collisionState.queueAcknowledgedTask(this.pendingVelocities::poll);
    }

    private void queueEntityVelocity(WrapperPlayServerEntityVelocity wrapperPlayServerEntityVelocity) {
        this.lastVelocityY = wrapperPlayServerEntityVelocity.getVelocity().getY();
        this.ticksSinceVelocityPacket = 0;
        this.pendingVelocities.add(wrapperPlayServerEntityVelocity.getVelocity());
    }
}
