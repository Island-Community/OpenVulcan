package me.frep.vulcan.spigot.tracker.entity;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerExplosion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import java.util.concurrent.ThreadLocalRandom;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.confirmation.ConfirmationTracker;

public final class EntityMetadataTracker {

    public void handlePacket(PlayerData data, PacketSendEvent event) {
        if (data == null || event == null) {
            return;
        }
        Object packetType = event.getPacketType();
        data.getWorldBlockTracker().dispatchServerPacket(event);
        data.getEntityTracker().handleEvent(event);
        if (packetType == Server.JOIN_GAME) {
            WrapperPlayServerJoinGame joinGame = new WrapperPlayServerJoinGame(event);
            data.setEntityId(joinGame.getEntityId());
            event.getTasksAfterSend().add(() -> sendJoinConfirmation(data));
            return;
        }
        if (packetType == Server.PING) {
            WrapperPlayServerPing ping = new WrapperPlayServerPing(event);
            if (ping.getId() < 0) {
                data.getConfirmationTracker().recordSentConfirmation(ping.getId());
            }
            return;
        }
        if (packetType == Server.WINDOW_CONFIRMATION) {
            WrapperPlayServerWindowConfirmation confirmation = new WrapperPlayServerWindowConfirmation(event);
            if (confirmation.getActionId() < 0) {
                data.getConfirmationTracker().recordSentConfirmation(confirmation.getActionId());
            }
            return;
        }
        if (packetType == Server.EXPLOSION) {
            data.getVelocityTracker().handleExplosion(new WrapperPlayServerExplosion(event), event);
            return;
        }
        if (packetType == Server.ENTITY_VELOCITY) {
            var velocity = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity(event);
            if (velocity.getEntityId() == data.getEntityId())
                data.getVelocityTracker().handleVelocity(velocity, event);
            return;
        }
        if (packetType == Server.UPDATE_ATTRIBUTES) {
            var attributes = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes(event);
            if (attributes.getEntityId() == data.getEntityId())
                data.getMotionState().handleAttributes(attributes);
            return;
        }
        if (packetType == Server.ENTITY_EFFECT) {
            var effect = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect(event);
            if (effect.getEntityId() == data.getEntityId())
                data.getMotionState().handlePotionEffect(effect);
            return;
        }
        if (packetType == Server.REMOVE_ENTITY_EFFECT) {
            var effect = new com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect(event);
            if (effect.getEntityId() == data.getEntityId())
                data.getMotionState().handleRemovedPotionEffect(effect);
            return;
        }
        if (packetType == Server.PLAYER_POSITION_AND_LOOK) {
            data.getMotionState().handleServerPosition(new WrapperPlayServerPlayerPositionAndLook(event));
            data.getRelativeMoveTracker().clearTargetPositions();
            for (Object candidate : data.getChecks()) {
                if (candidate instanceof AbstractCheck check) {
                    check.handlePacket(event);
                }
            }
            return;
        }
        if (packetType == Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(event);
            if (metadata.getEntityId() == data.getEntityId())
                data.getMotionState().handlePlayerMetadata(metadata);
            return;
        }
        if (packetType == Server.PLAYER_ABILITIES) {
            data.getCollisionState().handlePlayerAbilities(new WrapperPlayServerPlayerAbilities(event));
            return;
        }
        if (packetType == Server.HELD_ITEM_CHANGE) {
            return;
        }
        if (packetType == Server.CHANGE_GAME_STATE) {
            data.getMotionState().handleGameState(new WrapperPlayServerChangeGameState(event));
            return;
        }
        if (packetType == Server.ENTITY_RELATIVE_MOVE) {
            data.getRelativeMoveTracker().handleRelativeMove(new WrapperPlayServerEntityRelativeMove(event));
            return;
        }
        if (packetType == Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            data.getRelativeMoveTracker().handleRelativeMoveAndRotation(new WrapperPlayServerEntityRelativeMoveAndRotation(event));
            return;
        }
        if (packetType == Server.SET_PASSENGERS) {
            data.getVehicleTracker().handlePassengers(new WrapperPlayServerSetPassengers(event));
        }
        // All other server packet types do not change tracked state here.
    }

    private static void sendJoinConfirmation(PlayerData data) {
        if (data.getUser() == null) {
            return;
        }
        int identifier = ThreadLocalRandom.current().nextInt(-11500, -11000);
        var confirmation = ExemptionManager.isAtLeast1_17() ? (com.github.retrooper.packetevents.wrapper.PacketWrapper<?>) new WrapperPlayServerPing(identifier) : new WrapperPlayServerWindowConfirmation(0, (short) identifier, false);
        if (VulcanSettings.connectionDebugMessages) {
            ExemptionManager.logColored("Join game PING sent with ID=" + identifier + " for " + data.getPlayerName());
        }
        ConfirmationTracker counter = data.getConfirmationTracker();
        counter.setJoinConfirmationId((short) identifier);
        data.getUser().sendPacket(confirmation);
    }
}
