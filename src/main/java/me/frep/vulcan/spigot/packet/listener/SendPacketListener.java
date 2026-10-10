package me.frep.vulcan.spigot.packet.listener;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserConnectEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.message.ViolationFormatter;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

/**
 * PacketEvents bridge: routes inbound/outbound packets to the per-player
 * processors and records a client's brand message when available.
 */
public final class SendPacketListener implements com.github.retrooper.packetevents.event.PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();
        if (user == null || user.getUUID() == null) {
            return;
        }
        UUID uuid = user.getUUID();
        if (event.getPacketType() == PacketType.Configuration.Client.PLUGIN_MESSAGE) {
            WrapperConfigClientPluginMessage message = new WrapperConfigClientPluginMessage(event);
            recordBrand(message.getChannelName(), message.getData(), uuid);
            return;
        }
        if (!(event.getPacketType() instanceof PacketType.Play.Client) || shouldIgnore(user)) {
            return;
        }
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(user);
        if (playerData == null) {
            user.closeConnection();
            return;
        }
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage message = new WrapperPlayClientPluginMessage(event);
            recordBrand(message.getChannelName(), message.getData(), uuid);
        }
        if (playerData.getPlayer() == null && event.getPlayer() != null) {
            playerData.setPlayer((Player) event.getPlayer());
        }
        PluginServices.INSTANCE.getPacketListener().handlePacket(playerData, event);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) {
            return;
        }
        User user = event.getUser();
        if (user == null || user.getUUID() == null || shouldIgnore(user)) {
            return;
        }
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            event.getTasksAfterSend().add(() -> initializeLogin(event));
        }
        if (!(event.getPacketType() instanceof PacketType.Play.Server)) {
            return;
        }
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(user);
        if (playerData == null) {
            user.closeConnection();
            return;
        }
        if (playerData.getPlayer() == null && event.getPlayer() != null) {
            playerData.setPlayer((Player) event.getPlayer());
        }
        PluginServices.INSTANCE.getEntityMetadataTracker().handlePacket(playerData, event);
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        if (event.getUser() != null && event.getUser().getUUID() != null) {
            PluginServices.INSTANCE.getPlayerDataStore().unregister(event.getUser().getUUID());
        }
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
        if (event.getUser() != null && event.getUser().getConnectionState() == ConnectionState.PLAY) {
            event.setCancelled(true);
        }
    }

    private static boolean shouldIgnore(User user) {
        return (VulcanSettings.settingsIgnoreFloodgate && PluginServices.INSTANCE.isFloodgateEnabled() && FloodgateApi.getInstance().isFloodgatePlayer(user.getUUID())) || (VulcanSettings.settingsIgnoreGeyserUuids && user.getUUID().toString().startsWith("00000")) || (VulcanSettings.settingsIgnoreGeyserPrefixes && user.getName() != null && user.getName().startsWith(VulcanSettings.settingsIgnoreGeyserPrefix));
    }

    private static void recordBrand(String channel, byte[] payload, UUID playerId) {
        if (channel == null || payload == null || payload.length == 0) {
            return;
        }
        if (!channel.equals("minecraft:brand") && !channel.equals("MC|Brand") && !channel.equals(" MC|Brand")) {
            return;
        }
        String brand = readMinecraftString(payload);
        String formatted = ViolationFormatter.capitalize(brand).replace(" (Velocity)", "");
        PluginServices.INSTANCE.getClientBrands().put(playerId, formatted);
    }

    /**
     * Minecraft plugin-message strings are prefixed with a VarInt byte length.
     */
    private static String readMinecraftString(byte[] payload) {
        int length = 0;
        int shift = 0;
        int offset = 0;
        while (offset < payload.length && offset < 5) {
            int next = payload[offset++] & 0xff;
            length |= (next & 0x7f) << shift;
            if ((next & 0x80) == 0) {
                if (length >= 0 && length <= payload.length - offset) {
                    return new String(payload, offset, length, StandardCharsets.UTF_8);
                }
                break;
            }
            shift += 7;
        }
        // Malformed length header: keep the remaining bytes as a best-effort brand.
        return new String(payload, Math.min(1, payload.length), Math.max(0, payload.length - 1), StandardCharsets.UTF_8);
    }

    private static void initializeLogin(PacketSendEvent event) {
        PluginServices.INSTANCE.getUserDataInitializer().initializePlayer(event.getUser());
    }
}
