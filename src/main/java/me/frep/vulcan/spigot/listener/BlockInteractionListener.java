package me.frep.vulcan.spigot.listener;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.util.minecraft.ChatColorUtils;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.NumberConversions;

public class BlockInteractionListener implements Listener {

    public final UUID uniqueId = UUID.fromString("662a6b8d-da3e-4c1c-8813-96ea6097278d");

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleEvent(EntitySpawnEvent entitySpawnEvent) {
        if (entitySpawnEvent.getEntity() instanceof Firework) {
            Firework entity2 = (Firework) entitySpawnEvent.getEntity();
            if (entity2.getShooter() instanceof Player) {
                Player shooter = (Player) entity2.getShooter();
                PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
                PlayerData playerData2 = playerDataStore.getPlayerData(shooter);
                if (playerData2 == null) {
                    return;
                }
                CollisionState collisionState2 = playerData2.getCollisionState();
                collisionState2.setTicksSinceFirework(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleEvent(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        Player player2 = player;
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player2);
        if (playerData == null) {
            return;
        }
        playerData.getMotionState().updateHeldItem();
        Block block7 = blockPlaceEvent.getBlock();
        if (BlockUtils.isSlime(block7)) {
            playerData.getMotionState().setTicksSinceSlimePlace();
        }
        if (blockPlaceEvent.isCancelled()) {
            playerData.getMotionState().recordCancelledPlace();
        }
        Material type = blockPlaceEvent.getBlock().getType();
        if (BlockUtils.isIce(type)) {
            playerData.getMotionState().recordIcePlace();
        }
        Material type2 = blockPlaceEvent.getBlock().getType();
        if (BlockUtils.isClimbable(type2)) {
            playerData.getMotionState().recordClimbablePlace();
        }
        Material type3 = blockPlaceEvent.getBlock().getType();
        if (BlockUtils.isWeb(type3)) {
            playerData.getMotionState().recordWebPlace();
        }
        if (playerData.getCollisionState().isFrozen()) {
            if (player.getVehicle() != null) {
                player.leaveVehicle();
            }
            blockPlaceEvent.setCancelled(true);
        }
        Material type4 = blockPlaceEvent.getBlock().getType();
        if (BlockUtils.isScaffolding(type4)) {
            playerData.getMotionState().recordPlacedBlock(blockPlaceEvent);
        }
        if (blockPlaceEvent.getBlock().getType().toString().contains("SOUL")) {
            playerData.getMotionState().setTicksSinceSoulPlace(0);
        }
        playerData.getMotionState().recordBlockPlace();
        BlockPlaceEvent blockPlaceEvent2 = blockPlaceEvent;
        boolean validBlockPlacement = CheckUtils.isValidBlockPlacement(blockPlaceEvent2);
        Boolean validBlockPlacement2 = validBlockPlacement;
        playerData.getMotionState().setLastPlacementValid(validBlockPlacement2);
        playerData.getChecks().forEach(abstractCheck -> BlockInteractionListener.dispatchPlaceCheck(blockPlaceEvent, abstractCheck));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleEvent(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        Player player2 = player;
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player2);
        if (playerData == null) {
            return;
        }
        Double x = null;
        Double y = null;
        Double z = (double) blockBreakEvent.getBlock().getZ();
        y = (double) blockBreakEvent.getBlock().getY();
        x = (double) blockBreakEvent.getBlock().getX();
        playerData.getMotionState().recordBlockBreak(x, y, z);
        if (blockBreakEvent.isCancelled()) {
            playerData.getMotionState().setLastCancelledBreakTick();
        }
        if (playerData.getCollisionState().isFrozen()) {
            blockBreakEvent.setCancelled(true);
        }
        if (blockBreakEvent.getBlock().getType().toString().equals("NETHERRACK")) {
        }
        playerData.getChecks().forEach(abstractCheck -> BlockInteractionListener.dispatchBreakCheck(blockBreakEvent, abstractCheck));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerPickupItemEvent playerPickupItemEvent) {
        Player player2 = playerPickupItemEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null) {
            MotionState motionState = playerData2.getMotionState();
            motionState.setLastItemPickupTick();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerItemConsumeEvent playerItemConsumeEvent) {
        Player player2 = playerItemConsumeEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if ((playerData2 != null) && (playerItemConsumeEvent.getItem().toString().contains("CHORUS"))) {
            playerData2.getMotionState().recordChorusFruit();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerBedEnterEvent playerBedEnterEvent) {
        Player player2 = playerBedEnterEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null) {
            MotionState motionState = playerData2.getMotionState();
            motionState.setLastBedEnterTick();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerBedLeaveEvent playerBedLeaveEvent) {
        Player player2 = playerBedLeaveEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null) {
            MotionState motionState = playerData2.getMotionState();
            motionState.setLastBedLeaveTick();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerInteractEvent playerInteractEvent) {
        PlayerData playerData;
        Player player;
        player = playerInteractEvent.getPlayer();
        Player player3 = player;
        playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player3);
        if (playerData == null) {
            return;
        }
        if (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK) {
            playerData.getChecks().forEach(abstractCheck -> BlockInteractionListener.dispatchInteractCheck(playerInteractEvent, abstractCheck));
        }
        if ((playerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR)) {
            if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if (playerInteractEvent.getItem() != null) {
                    Material type5 = playerInteractEvent.getItem().getType();
                    if (BlockUtils.isGlassBottle(type5)) {
                        playerData.getMotionState().setTicksSinceBottleUse(0);
                    }
                }
            }
        } else if (playerInteractEvent.getItem() != null) {
            Material type6 = playerInteractEvent.getItem().getType();
            if (BlockUtils.isGlassBottle(type6)) {
                playerData.getMotionState().setTicksSinceBottleUse(0);
            }
        }
        if ((playerInteractEvent.getClickedBlock() != null) && (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Material type7 = playerInteractEvent.getClickedBlock().getType();
            if (BlockUtils.isNoteBlock(type7)) {
                playerData.getClickTimingData().resetPlacementTime();
            }
        }
        if ((playerInteractEvent.getAction() != Action.LEFT_CLICK_BLOCK)) {
            if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && (playerInteractEvent.getItem() != null) && (playerInteractEvent.getItem().getType().toString().contains("BUCKET"))) {
                    playerData.getMotionState().setTicksSinceBucketUse(0);
                }
                if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && (playerInteractEvent.getItem() != null) && (playerInteractEvent.getItem().getType().toString().contains("_BUCKET")) && (playerInteractEvent.getClickedBlock() != null) && (!BlockUtils.isAir(playerInteractEvent.getClickedBlock().getType()))) {
                    double x = playerInteractEvent.getClickedBlock().getX();
                    double y = playerInteractEvent.getClickedBlock().getY();
                    double z = playerInteractEvent.getClickedBlock().getZ();
                    double x2 = player.getLocation().getX();
                    double y2 = player.getLocation().getY();
                    double z2 = player.getLocation().getZ();
                    Double xDifference = null;
                    Double yDifference = null;
                    Double zDifference = null;
                    zDifference = Math.abs(z - z2);
                    yDifference = Math.abs(y - y2);
                    xDifference = Math.abs(x - x2);
                    double amount = MathUtils.length3(xDifference, yDifference, zDifference);
                    if (amount < 2.25) {
                        playerData.getMotionState().setTicksSinceBucketEmpty(0);
                    }
                }
                playerData.getMotionState().recordDig();
            }
        } else {
            if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && (playerInteractEvent.getItem() != null) && (playerInteractEvent.getItem().getType().toString().contains("BUCKET"))) {
                playerData.getMotionState().setTicksSinceBucketUse(0);
            }
            if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && (playerInteractEvent.getItem() != null) && (playerInteractEvent.getItem().getType().toString().contains("_BUCKET")) && (playerInteractEvent.getClickedBlock() != null) && (!BlockUtils.isAir(playerInteractEvent.getClickedBlock().getType()))) {
                double x = playerInteractEvent.getClickedBlock().getX();
                double y = playerInteractEvent.getClickedBlock().getY();
                double z = playerInteractEvent.getClickedBlock().getZ();
                double x2 = player.getLocation().getX();
                double y2 = player.getLocation().getY();
                double z2 = player.getLocation().getZ();
                Double xDifference2 = null;
                Double yDifference2 = null;
                Double zDifference2 = null;
                zDifference2 = Math.abs(z - z2);
                yDifference2 = Math.abs(y - y2);
                xDifference2 = Math.abs(x - x2);
                double amount = MathUtils.length3(xDifference2, yDifference2, zDifference2);
                if (amount < 2.25) {
                    playerData.getMotionState().setTicksSinceBucketEmpty(0);
                }
            }
            playerData.getMotionState().recordDig();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerDeathEvent playerDeathEvent) {
        Player entity2 = playerDeathEvent.getEntity();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(entity2);
        if (playerData2 != null) {
            MotionState motionState = playerData2.getMotionState();
            motionState.setTicksSinceDeath();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleEvent(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player && !entityDamageEvent.isCancelled()) {
            Player entity2 = (Player) entityDamageEvent.getEntity();
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData2 = playerDataStore.getPlayerData(entity2);
            if (playerData2 != null) {
                CollisionState collisionState2 = playerData2.getCollisionState();
                if (collisionState2.isFrozen()) {
                    entityDamageEvent.setCancelled(true);
                }
                MotionState motionState = playerData2.getMotionState();
                motionState.recordDamageCause(entityDamageEvent.getCause());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerInteractEntityEvent playerInteractEntityEvent) {
        Player player2 = playerInteractEntityEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null) {
            switch(playerInteractEntityEvent.getRightClicked().getType()) {
                case VILLAGER:
                case COW:
                case CHICKEN:
                case WOLF:
                    playerData2.getClickTimingData().resetDigTime();
                    break;
                case ITEM_FRAME:
                    playerData2.getClickTimingData().resetSwingTime();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleEvent(ProjectileLaunchEvent projectileLaunchEvent) {
        {
            PlayerData playerData;
            if ((projectileLaunchEvent.getEntity() instanceof Arrow)) {
                Arrow arrow = (Arrow) projectileLaunchEvent.getEntity();
                if (arrow.getShooter() == null)
                    return;
                if (!(arrow.getShooter() instanceof Player))
                    return;
                Player player = (Player) arrow.getShooter();
                Player player3 = player;
                playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player3);
                if ((playerData == null)) {
                    return;
                }
                if ((playerData.getPlayer() == null)) {
                    return;
                }
                playerData.getChecks().forEach(abstractCheck -> BlockInteractionListener.dispatchLaunchCheck(projectileLaunchEvent, abstractCheck));
                return;
            }
        }
        if (projectileLaunchEvent.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) projectileLaunchEvent.getEntity();
            if ((snowball.getShooter() != null) && (snowball.getShooter() instanceof Player)) {
                Player player = (Player) snowball.getShooter();
                Player player4 = player;
                PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player4);
                if (playerData == null) {
                    return;
                }
                playerData.getMotionState().setLastProjectileLaunchTick();
            }
        } else if (projectileLaunchEvent.getEntity() instanceof Egg) {
            Egg egg = (Egg) projectileLaunchEvent.getEntity();
            if ((egg.getShooter() != null) && (egg.getShooter() instanceof Player)) {
                Player player = (Player) egg.getShooter();
                Player player5 = player;
                PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player5);
                if (playerData == null) {
                    return;
                }
                playerData.getMotionState().setLastProjectileLaunchTick();
            }
        } else if (projectileLaunchEvent.getEntity() instanceof ThrownExpBottle) {
            ThrownExpBottle thrownExpBottle = (ThrownExpBottle) projectileLaunchEvent.getEntity();
            if ((thrownExpBottle.getShooter() != null) && (thrownExpBottle.getShooter() instanceof Player)) {
                Player player = (Player) thrownExpBottle.getShooter();
                Player player6 = player;
                PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player6);
                if (playerData == null) {
                    return;
                }
                playerData.getMotionState().setLastProjectileLaunchTick();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handlePlayer(PlayerTeleportEvent playerTeleportEvent) {
        Player player2 = playerTeleportEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null && playerTeleportEvent.getTo() != null) {
            if (playerTeleportEvent.getCause() == TeleportCause.ENDER_PEARL) {
                playerData2.getMotionState().recordEnderPearl();
                CollisionState collisionState2 = playerData2.getCollisionState();
                if (collisionState2.isFrozen()) {
                    playerTeleportEvent.setCancelled(true);
                }
            }
            if (playerTeleportEvent.getCause() != TeleportCause.UNKNOWN && playerTeleportEvent.getCause() != TeleportCause.ENDER_PEARL) {
                playerData2.getMotionState().recordTeleport();
            }
            if (ExemptionManager.isNewerThan1_13()) {
                int index = NumberConversions.floor(playerTeleportEvent.getTo().getX());
                int index2 = NumberConversions.floor(playerTeleportEvent.getTo().getY());
                int index3 = NumberConversions.floor(playerTeleportEvent.getTo().getZ());
                if (playerTeleportEvent.getTo().getWorld() != null && playerTeleportEvent.getTo().getWorld().isChunkLoaded(index >> 4, index3 >> 4)) {
                    World world2 = playerTeleportEvent.getTo().getWorld();
                    List surroundingMaterials = BlockUtils.getSurroundingMaterials(world2, index, index2, index3, 1);
                    CollisionState collisionState3 = playerData2.getCollisionState();
                    collisionState3.setSurroundingMaterials(surroundingMaterials);
                    CollisionState collisionState4 = playerData2.getCollisionState();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerChangedWorldEvent playerChangedWorldEvent) {
        Player player2 = playerChangedWorldEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null) {
            MotionState motionState = playerData2.getMotionState();
            motionState.setTicksSinceWorldChange();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerFishEvent playerFishEvent) {
        if (playerFishEvent.getCaught() instanceof Player) {
            Player caught = (Player) playerFishEvent.getCaught();
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData2 = playerDataStore.getPlayerData(caught);
            if (playerData2 == null) {
                return;
            }
            CollisionState collisionState2 = playerData2.getCollisionState();
            collisionState2.recordFishingHook();
        } else if (playerFishEvent.getCaught() instanceof Boat) {
            Boat caught2 = (Boat) playerFishEvent.getCaught();
            Map hookedBoatTicks = PluginServices.INSTANCE.getHookedBoatTicks();
            Integer entityId = caught2.getEntityId();
            TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
            hookedBoatTicks.put(entityId, tickProcessor.getTotalTicks());
        }
        Player player2 = playerFishEvent.getPlayer();
        PlayerDataStore playerDataStore2 = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData3 = playerDataStore2.getPlayerData(player2);
        if (playerData3 != null) {
            MotionState motionState = playerData3.getMotionState();
        }
    }

    @EventHandler
    public void handleEvent(BlockDamageEvent blockDamageEvent) {
        Player player2 = blockDamageEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if ((playerData2 != null) && (blockDamageEvent.getInstaBreak())) {
            MotionState motionState = playerData2.getMotionState();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleEvent(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
            Player damager2 = (Player) entityDamageByEntityEvent.getDamager();
            PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData2 = playerDataStore.getPlayerData(damager2);
            if (playerData2 != null && System.currentTimeMillis() - playerData2.getLastCancelHitsAtMillis() < VulcanSettings.cancelHitsDuration) {
                entityDamageByEntityEvent.setDamage(0.0);
                if (VulcanSettings.cancelHitsCancelEvent) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            }
        }
        if (entityDamageByEntityEvent.getEntity() instanceof Player) {
            Player entity2 = (Player) entityDamageByEntityEvent.getEntity();
            if (entity2.hasMetadata("NPC") || entity2.hasMetadata("npc")) {
                return;
            }
            PlayerDataStore playerDataStore2 = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData playerData3 = playerDataStore2.getPlayerData(entity2);
            if (playerData3 == null) {
                return;
            }
            EntityType type = entityDamageByEntityEvent.getDamager().getType();
            playerData3.getMotionState().recordEntityDamage(type);
        }
        if (entityDamageByEntityEvent.getEntity() instanceof Player && entityDamageByEntityEvent.getDamager() instanceof Player && !entityDamageByEntityEvent.isCancelled()) {
            Player damager = (Player) entityDamageByEntityEvent.getDamager();
            Player victim = (Player) entityDamageByEntityEvent.getEntity();
            PlayerDataStore store = PluginServices.INSTANCE.getPlayerDataStore();
            PlayerData damagerData = store.getPlayerData(damager);
            PlayerData victimData = store.getPlayerData(victim);
            if (damagerData != null && victimData != null) {
                if (victimData.getCollisionState().isFrozen() || damagerData.getCollisionState().isFrozen()) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
                if (entityDamageByEntityEvent.getCause() == DamageCause.ENTITY_ATTACK) {
                    damagerData.getRelativeMoveTracker().recordAttackGiven();
                    victimData.getRelativeMoveTracker().recordAttackTaken();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayer(PlayerMoveEvent playerMoveEvent) {
        if ((playerMoveEvent.getFrom().getX() == playerMoveEvent.getTo().getX()) && (playerMoveEvent.getFrom().getY() == playerMoveEvent.getTo().getY()) && (playerMoveEvent.getFrom().getZ() == playerMoveEvent.getTo().getZ())) {
            return;
        }
        Player player = playerMoveEvent.getPlayer();
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        if (playerMoveEvent.isCancelled()) {
            playerData.getMotionState().setTicksSinceCancelledMove(0);
        }
        CollisionState collisionState = playerData.getCollisionState();
        if (collisionState.isFrozen()) {
            String messagesFrozen2 = VulcanSettings.messagesFrozen;
            player.sendMessage(ChatColorUtils.colorize(messagesFrozen2));
            playerMoveEvent.setTo(new Location(player.getWorld(), playerData.getCollisionState().getSetbackX(), playerData.getCollisionState().getSetbackY(), playerData.getCollisionState().getSetbackZ(), playerData.getRotationTracker().getYaw(), playerData.getRotationTracker().getPitch()));
        }
        if (ExemptionManager.isNewerThan1_13()) {
            Waterlogged waterlogged;
            if ((playerData.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) && (playerData.getMotionState().isPendingSwimFix())) {
                Boolean value = false;
                playerData.getMotionState().setPendingSwimFix(value);
                player.setSwimming(true);
            }
            int index = NumberConversions.floor(playerMoveEvent.getTo().getX());
            int index2 = NumberConversions.floor(playerMoveEvent.getTo().getY());
            int index3 = NumberConversions.floor(playerMoveEvent.getTo().getZ());
            World world2 = null;
            Integer value2 = null;
            Integer value3 = null;
            Integer value4 = null;
            Integer value5 = 1;
            value4 = index3;
            value3 = index2;
            value2 = index;
            world2 = playerMoveEvent.getTo().getWorld();
            List list = BlockUtils.getSurroundingMaterials(world2, value2, value3, value4, value5);
            List list3 = list;
            playerData.getCollisionState().setSurroundingMaterials(list3);
            World world3 = null;
            Integer value6 = null;
            Integer value7 = null;
            Integer value8 = index3;
            value7 = index2;
            value6 = index;
            world3 = playerMoveEvent.getTo().getWorld();
            List list2 = BlockUtils.getDeepFloorMaterials(world3, value6, value7, value8);
            List list22 = list2;
            playerData.getCollisionState().setDeepFloorMaterials(list22);
            Block block = playerMoveEvent.getTo().getBlock();
            Block block2 = playerMoveEvent.getTo().clone().subtract(0.0, 1.0, 0.0).getBlock();
            Block block3 = playerMoveEvent.getTo().clone().subtract(0.0, 2.0, 0.0).getBlock();
            Block block4 = playerMoveEvent.getTo().clone().subtract(0.0, 3.0, 0.0).getBlock();
            Block block5 = playerMoveEvent.getTo().clone().add(0.0, 1.0, 0.0).getBlock();
            Block block6 = playerMoveEvent.getTo().clone().add(0.0, 2.0, 0.0).getBlock();
            if (block2.getBlockData() instanceof Waterlogged) {
                waterlogged = (Waterlogged) block2.getBlockData();
                if (waterlogged.isWaterlogged()) {
                    playerData.getCollisionState().recordFeetWaterlogged();
                }
            }
            if (block.getBlockData() instanceof Waterlogged) {
                waterlogged = (Waterlogged) block.getBlockData();
                if (waterlogged.isWaterlogged()) {
                    playerData.getCollisionState().recordFeetWaterlogged();
                }
            }
            if (block5.getBlockData() instanceof Waterlogged) {
                waterlogged = (Waterlogged) block5.getBlockData();
                if (waterlogged.isWaterlogged()) {
                    playerData.getCollisionState().recordHeadWaterlogged();
                }
            }
            if (block6.getBlockData() instanceof Waterlogged) {
                waterlogged = (Waterlogged) block6.getBlockData();
                if (waterlogged.isWaterlogged()) {
                    playerData.getCollisionState().recordHeadWaterlogged();
                }
            }
            playerData.getCollisionState().setGroundMaterial(block2.getType());
            playerData.getCollisionState().setSubGroundMaterial(block3.getType());
            Material type = block4.getType();
            playerData.getCollisionState().setDeepGroundMaterial(type);
            if (player.getVehicle() != null) {
                List list4 = list;
                playerData.getCollisionState().setAreaScanMaterials(list4);
            }
        }
        if (PluginServices.INSTANCE.isFoliaServer()) {
            playerData.getCollisionState().scanFoliaEntities();
        } else {
            List list = player.getNearbyEntities(1.5, 2.0, 1.5);
            collisionState.setNearbyEntities(list);
        }
        playerData.getChecks().forEach(abstractCheck -> BlockInteractionListener.dispatchMoveCheck(playerMoveEvent, abstractCheck));
    }

    @EventHandler
    public void handlePlayer(PlayerKickEvent playerKickEvent) {
        Player player2 = playerKickEvent.getPlayer();
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        PlayerData playerData2 = playerDataStore.getPlayerData(player2);
        if (playerData2 != null) {
            CollisionState collisionState2 = playerData2.getCollisionState();
            if (collisionState2.isFrozen() && playerKickEvent.getReason().toLowerCase().contains("not enabled")) {
                playerKickEvent.setCancelled(true);
            }
        }
    }

    private static void dispatchMoveCheck(PlayerMoveEvent playerMoveEvent2, AbstractCheck abstractCheck) {
        abstractCheck.handleEvent(playerMoveEvent2);
    }

    private static void dispatchLaunchCheck(ProjectileLaunchEvent projectileLaunchEvent2, AbstractCheck abstractCheck) {
        abstractCheck.handleEvent(projectileLaunchEvent2);
    }

    private static void dispatchInteractCheck(PlayerInteractEvent playerInteractEvent2, AbstractCheck abstractCheck) {
        abstractCheck.handleEvent(playerInteractEvent2);
    }

    private static void dispatchBreakCheck(BlockBreakEvent blockBreakEvent2, AbstractCheck abstractCheck) {
        abstractCheck.handleEvent(blockBreakEvent2);
    }

    private static void dispatchPlaceCheck(BlockPlaceEvent blockPlaceEvent2, AbstractCheck abstractCheck) {
        abstractCheck.handleEvent(blockPlaceEvent2);
    }
}
