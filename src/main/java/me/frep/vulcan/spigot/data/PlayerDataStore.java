package me.frep.vulcan.spigot.data;

import com.github.retrooper.packetevents.protocol.player.User;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.frep.vulcan.spigot.player.session.PlayerData;
import org.bukkit.entity.Player;

public final class PlayerDataStore {

    private final Map<UUID, PlayerData> playersById = new ConcurrentHashMap<>();

    public PlayerData getPlayerData(Player player2) {
        return this.playersById.get(player2.getUniqueId());
    }

    public PlayerData getPlayerData(User user) {
        return this.playersById.get(user.getUUID());
    }

    public void register(User user) {
        this.playersById.put(user.getUUID(), new PlayerData(user));
    }

    public void unregister(UUID uuid) {
        this.playersById.remove(uuid);
    }

    public void unregister(User user) {
        this.playersById.remove(user.getUUID());
    }

    public Collection<PlayerData> getPlayers() {
        return this.playersById.values();
    }
}
