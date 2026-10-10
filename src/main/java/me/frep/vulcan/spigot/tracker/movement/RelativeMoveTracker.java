package me.frep.vulcan.spigot.tracker.movement;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.frep.vulcan.spigot.collection.list.EvictingList;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.collection.tuple.Pair;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RelativeMoveTracker {

    private final PlayerData playerData;

    private int ticksSinceAttack = 100;

    private int lastAttackGivenTick;

    private int lastAttackTakenTick;

    private int lastItemFrameInteractTick;

    private Entity lastAttackedEntity;

    private Player player;

    private Player previousTarget;

    private double attackDistance;

    private double targetDistance;

    private final EvictingList<Pair> targetPositions = new EvictingList<>(20);

    public RelativeMoveTracker(PlayerData data) {
        this.playerData = data;
    }

    public void handleInteract(WrapperPlayClientInteractEntity interaction) {
        if (this.playerData.getPlayer() == null) {
            return;
        }
        Entity target = SpigotReflectionUtil.getEntityById(this.playerData.getPlayer().getWorld(), interaction.getEntityId());
        if (target instanceof ItemFrame) {
            this.lastItemFrameInteractTick = PluginServices.INSTANCE.getTickProcessor().getTotalTicks();
        }
        if (interaction.getAction() == InteractAction.ATTACK) {
            this.lastAttackedEntity = target;
            if (this.lastAttackedEntity != null && this.playerData.getPlayer() != null) {
                this.attackDistance = this.playerData.getPlayer().getLocation().toVector().setY(0).distance(this.lastAttackedEntity.getLocation().toVector().setY(0)) - 0.42;
            }
            if (SpigotReflectionUtil.getEntityById(this.playerData.getPlayer().getWorld(), interaction.getEntityId()) instanceof Player) {
                this.previousTarget = this.player == null ? (Player) target : this.player;
                this.player = (Player) target;
                if (this.player != this.previousTarget) {
                    this.targetPositions.clear();
                }
            }
            this.ticksSinceAttack = 0;
        }
    }

    public void handleRelativeMove(WrapperPlayServerEntityRelativeMove packet) {
        if (this.player == null || this.player.getEntityId() != packet.getEntityId()) {
            return;
        }
        PlayerDataStore store = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData targetData = store.getPlayerData(this.player);
        if (targetData == null || targetData.getPlayer() == null) {
            return;
        }
        int currentTick = PluginServices.INSTANCE.getTickProcessor().getTotalTicks();
        World world = targetData.getPlayer().getWorld();
        CollisionState collision = targetData.getCollisionState();
        Location position = new Location(world, collision.getX() - packet.getDeltaX(), collision.getY() - packet.getDeltaY(), collision.getZ() - packet.getDeltaZ());
        this.targetPositions.add(new Pair(position, currentTick));
    }

    public void handleRelativeMoveAndRotation(WrapperPlayServerEntityRelativeMoveAndRotation packet) {
        if (this.player == null || this.player.getEntityId() != packet.getEntityId()) {
            return;
        }
        PlayerDataStore store = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData targetData = store.getPlayerData(this.player);
        if (targetData == null || targetData.getPlayer() == null) {
            return;
        }
        int currentTick = PluginServices.INSTANCE.getTickProcessor().getTotalTicks();
        World world = targetData.getPlayer().getWorld();
        CollisionState collision = targetData.getCollisionState();
        Location position = new Location(world, collision.getX() - packet.getDeltaX(), collision.getY() - packet.getDeltaY(), collision.getZ() - packet.getDeltaZ());
        this.targetPositions.add(new Pair(position, currentTick));
    }

    public void clearTargetPositions() {
        this.targetPositions.clear();
    }

    public void recordAttackGiven() {
        this.lastAttackGivenTick = PluginServices.INSTANCE.getTickProcessor().getTotalTicks();
    }

    public void recordAttackTaken() {
        this.lastAttackTakenTick = PluginServices.INSTANCE.getTickProcessor().getTotalTicks();
    }

    public int getTicksSinceAttackGiven() {
        TickProcessor ticks = PluginServices.INSTANCE.getTickProcessor();
        return ticks.getTotalTicks() - this.lastAttackGivenTick;
    }

    public int getTicksSinceAttackTaken() {
        TickProcessor ticks = PluginServices.INSTANCE.getTickProcessor();
        return ticks.getTotalTicks() - this.lastAttackTakenTick;
    }

    public void processMovement() {
        ++this.ticksSinceAttack;
        if (!this.targetPositions.isEmpty() && this.player != null) {
            int currentTick = PluginServices.INSTANCE.getTickProcessor().getTotalTicks();
            int latencyTicks = MathUtils.pingCompensationTicks(this.playerData);
            double x = this.playerData.getCollisionState().getX();
            double z = this.playerData.getCollisionState().getZ();
            Vector origin = new Vector(x, 0.0, z);
            this.targetDistance = this.playerData.getRelativeMoveTracker().getTargetPositions().stream().filter(entry -> RelativeMoveTracker.isRecentTargetPosition(currentTick, latencyTicks, entry)).mapToDouble(entry -> RelativeMoveTracker.calculateTargetDistance(origin, entry)).min().orElse(-1.0);
        }
    }

    public int getTicksSinceAttack() {
        return this.ticksSinceAttack;
    }

    public int getLastItemFrameInteractTick() {
        return this.lastItemFrameInteractTick;
    }

    public Entity getLastAttackedEntity() {
        return this.lastAttackedEntity;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getAttackDistance() {
        return this.attackDistance;
    }

    public double getTargetDistance() {
        return this.targetDistance;
    }

    public EvictingList<Pair> getTargetPositions() {
        return this.targetPositions;
    }

    private static double calculateTargetDistance(Vector origin, Pair entry) {
        Vector target = ((Location) entry.getFirst()).toVector().setY(0);
        return origin.distance(target) - 0.52;
    }

    private static boolean isRecentTargetPosition(int currentTick, int latencyTicks, Pair entry) {
        return Math.abs(currentTick - (Integer) entry.getSecond() - latencyTicks) < 3;
    }
}
