package me.frep.vulcan.spigot.check.impl.player.scaffold;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction.Action;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = 'F', complexType = "Packet", description = "Invalid rotations")
public class ScaffoldF extends AbstractCheck {

    private long lastPlacementAtMillis;

    private long placementIntervalMillis;

    private long lastSwingAtMillis;

    private long swingIntervalMillis;

    private boolean lastPlacementValid;

    private boolean swungSinceMovement;

    public ScaffoldF(PlayerData playerData) {
        super(playerData);
    }

    public void handlePacket(PacketEvent packetEvent) {
        if (this.isEntityActionPacket(packetEvent)) {
            WrapperPlayClientEntityAction wrapperPlayClientEntityAction = this.playerData.getLastEntityActionPacket();
            if (wrapperPlayClientEntityAction.getAction() == Action.START_SNEAKING || wrapperPlayClientEntityAction.getAction() == Action.STOP_SNEAKING) {
                this.swingIntervalMillis = System.currentTimeMillis() - this.lastSwingAtMillis;
                this.swungSinceMovement = true;
                this.lastSwingAtMillis = System.currentTimeMillis();
            }
        }
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled()) {
                return;
            }
            if (BlockUtils.isScaffolding(blockPlaceEvent.getBlock().getType()) || !blockPlaceEvent.getBlock().getType().isSolid()) {
                return;
            }
            this.lastPlacementValid = CheckUtils.isValidBlockPlacement(blockPlaceEvent);
            this.placementIntervalMillis = System.currentTimeMillis() - this.lastPlacementAtMillis;
            this.lastPlacementAtMillis = System.currentTimeMillis();
            CollisionState collisionState = this.playerData.getCollisionState();
            double deltaXZ = collisionState.getDeltaXZ();
            double maxSpeed = CheckUtils.getMaxSpeed(this.playerData, 0.27F);
            boolean exempt = this.isExempt(new ExemptType[] { ExemptType.LIQUID, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.VEHICLE });
            boolean condition = this.swungSinceMovement && this.lastPlacementValid && deltaXZ > maxSpeed && this.placementIntervalMillis < 400L;
            if (condition && !exempt) {
                if (this.increaseBuffer() > this.maximumBuffer) {
                    this.fail("delay=" + this.placementIntervalMillis + " delta=" + deltaXZ + " sd=" + this.swingIntervalMillis);
                }
            } else {
                this.decayBuffer();
            }
            this.swungSinceMovement = false;
        }
    }
}
