package me.frep.vulcan.spigot.packet.listener;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.player.session.PlayerData;

public final class PacketListener {

    public void handlePacket(PlayerData data, PacketReceiveEvent event) {
        var type = event.getPacketType();
        boolean flying = WrapperPlayClientPlayerFlying.isFlying(type);
        if (type == Client.PONG)
            handleConfirmation(data, new WrapperPlayClientPong(event).getId());
        else if (type == Client.WINDOW_CONFIRMATION)
            handleConfirmation(data, new WrapperPlayClientWindowConfirmation(event).getActionId());
        else if (flying) {
            if (data.getPlayer() == null)
                return;
            WrapperPlayClientPlayerFlying movement = new WrapperPlayClientPlayerFlying(event);
            data.setLastFlyingPacket(movement);
            if (movement.hasRotationChanged())
                data.getRotationTracker().handleRotation(movement.getLocation().getYaw(), movement.getLocation().getPitch());
            data.getCollisionState().processMovement(movement);
            data.getMotionState().processMovement(movement);
            data.getVelocityTracker().processMovement(movement);
            data.getConnectionTracker().processMovement();
            data.getRelativeMoveTracker().processMovement();
            if (data.isHandshakeComplete())
                data.getConfirmationTracker().checkResponseTimeout();
        } else if (type == Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interaction = new WrapperPlayClientInteractEntity(event);
            data.setLastInteractionPacket(interaction);
            data.getMotionState().handleEntityInteraction(interaction);
            data.getRelativeMoveTracker().handleInteract(interaction);
        } else if (type == Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction action = new WrapperPlayClientEntityAction(event);
            data.setLastEntityActionPacket(action);
            data.getMotionState().handleEntityAction(action);
        } else if (type == Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging digging = new WrapperPlayClientPlayerDigging(event);
            data.setLastDiggingPacket(digging);
            data.getMotionState().handleDigging(digging);
            data.getClickTimingData().recordDig();
        } else if (type == Client.PLAYER_BLOCK_PLACEMENT) {
            data.setLastPlacementPacket(new WrapperPlayClientPlayerBlockPlacement(event));
            data.getClickTimingData().recordPlacement();
        } else if (type == Client.CLIENT_STATUS)
            data.getMotionState().setLastRespawnTick(new WrapperPlayClientClientStatus(event));
        else if (type == Client.HELD_ITEM_CHANGE) {
            data.setLastHeldItemPacket(new WrapperPlayClientHeldItemChange(event));
        } else if (type == Client.ANIMATION)
            data.getClickTimingData().recordSwing();
        else if (type == Client.KEEP_ALIVE)
            data.getConnectionTracker().recordKeepAlive();
        data.getWorldBlockTracker().handleBlockPlace(event);
        for (AbstractCheck check : data.getChecks()) check.handlePacket(event);
        if (flying && data.isMitigationPending()) {
            data.getCollisionState().runAcknowledgedTasks();
            data.setMitigationPending(false);
        }
    }

    private static void handleConfirmation(PlayerData data, int id) {
        data.getRawConfirmationIds().add(id);
        if (!data.isHandshakeComplete() && id == data.getConfirmationTracker().getJoinConfirmationId())
            data.setHandshakeComplete(true);
        else if (id < 0 && data.isHandshakeComplete()) {
            data.getConfirmationTracker().handleConfirmationReply(id);
            if (!data.isConnectionKickQueued())
                data.getRepliedConfirmationIds().add(id);
        }
    }
}
