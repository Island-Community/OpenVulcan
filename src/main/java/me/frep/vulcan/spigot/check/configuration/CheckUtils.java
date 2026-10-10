package me.frep.vulcan.spigot.check.configuration;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.data.PlayerDataStore;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.util.scheduling.MainThreadExecutor;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CheckUtils {

    public static int getPing(Player player2) {
        return PacketEvents.getAPI().getPlayerManager().getPing(player2);
    }

    public static void sendPacket(PlayerData playerData3, PacketWrapper packetWrapper) {
        ProtocolManager protocolManager = PacketEvents.getAPI().getProtocolManager();
        protocolManager.sendPacket(playerData3.getUser().getChannel(), packetWrapper);
    }

    public static boolean isAtLeast1_17(PlayerData data) {
        return data.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17);
    }

    public static boolean isNewerThan1_9(PlayerData playerData3) {
        return playerData3.getUser().getClientVersion().isNewerThan(ClientVersion.V_1_9);
    }

    public static boolean isAtLeast1_13(PlayerData playerData3) {
        return playerData3.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13);
    }

    public static void sendPingPacket(PlayerData playerData3, int index) {
        sendPacket(playerData3, (PacketWrapper) new WrapperPlayServerPing(index));
    }

    public static boolean isSwimming(PlayerData data) {
        return data.getPlayer() != null && ExemptionManager.isNewerThan1_13() && data.getPlayer().isSwimming();
    }

    public static boolean isEnclosedInSolidBlocks(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterials() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterials().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if ((collisionState.getSurroundingMaterials().get(16)).isSolid()) {
                    collisionState = playerData3.getCollisionState();
                    if ((collisionState.getSurroundingMaterials().get(19)).isSolid()) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean isEnclosedInSolidBlocksLegacy(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterialsLegacy() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterialsLegacy().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if ((collisionState.getSurroundingMaterialsLegacy().get(16)).isSolid()) {
                    collisionState = playerData3.getCollisionState();
                    if ((collisionState.getSurroundingMaterialsLegacy().get(19)).isSolid()) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean touchesSolidBlocks(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterialsLegacy() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterialsLegacy().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (!(collisionState.getSurroundingMaterialsLegacy().get(16)).isSolid()) {
                    collisionState = playerData3.getCollisionState();
                    if (!(collisionState.getSurroundingMaterialsLegacy().get(19)).isSolid()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean hasDolphinsGrace(PlayerData playerData3) {
        if (playerData3.getPlayer() == null) {
            return false;
        }
        if ((ExemptionManager.isNewerThan1_13()) && (playerData3.getPlayer().hasPotionEffect(PotionEffectType.DOLPHINS_GRACE))) {
            return true;
        }
        return false;
    }

    public static boolean touchesBubbleColumn(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterials() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterials().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (!BlockUtils.isBubbleColumn(collisionState.getSurroundingMaterials().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (!BlockUtils.isBubbleColumn(collisionState.getSurroundingMaterials().get(19))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static double getYawDifference(Player player2, Player player3) {
        return Math.abs(180.0F - Math.abs(player2.getLocation().getYaw() - player3.getLocation().getYaw()));
    }

    public static List getFloorMaterials(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterials = collisionState.getSurroundingMaterials();
        if (!(packedMaterials != null && !packedMaterials.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterials.get(0));
        linkedList.add((Material) packedMaterials.get(1));
        linkedList.add((Material) packedMaterials.get(2));
        linkedList.add((Material) packedMaterials.get(12));
        linkedList.add((Material) packedMaterials.get(13));
        linkedList.add((Material) packedMaterials.get(14));
        linkedList.add((Material) packedMaterials.get(24));
        linkedList.add((Material) packedMaterials.get(25));
        linkedList.add((Material) packedMaterials.get(26));
        return linkedList;
    }

    public static List getHeadMaterials(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterials = collisionState.getSurroundingMaterials();
        if (!(packedMaterials != null && !packedMaterials.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterials.get(6));
        linkedList.add((Material) packedMaterials.get(7));
        linkedList.add((Material) packedMaterials.get(8));
        linkedList.add((Material) packedMaterials.get(18));
        linkedList.add((Material) packedMaterials.get(19));
        linkedList.add((Material) packedMaterials.get(20));
        linkedList.add((Material) packedMaterials.get(30));
        linkedList.add((Material) packedMaterials.get(31));
        linkedList.add((Material) packedMaterials.get(32));
        return linkedList;
    }

    public static List getHeadMaterialsLegacy(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterialsLegacy = collisionState.getSurroundingMaterialsLegacy();
        if (!(packedMaterialsLegacy != null && !packedMaterialsLegacy.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterialsLegacy.get(6));
        linkedList.add((Material) packedMaterialsLegacy.get(7));
        linkedList.add((Material) packedMaterialsLegacy.get(8));
        linkedList.add((Material) packedMaterialsLegacy.get(18));
        linkedList.add((Material) packedMaterialsLegacy.get(19));
        linkedList.add((Material) packedMaterialsLegacy.get(20));
        linkedList.add((Material) packedMaterialsLegacy.get(30));
        linkedList.add((Material) packedMaterialsLegacy.get(31));
        linkedList.add((Material) packedMaterialsLegacy.get(32));
        return linkedList;
    }

    public static List getFloorMaterialsLegacy(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterialsLegacy = collisionState.getSurroundingMaterialsLegacy();
        if (!(packedMaterialsLegacy != null && !packedMaterialsLegacy.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterialsLegacy.get(0));
        linkedList.add((Material) packedMaterialsLegacy.get(1));
        linkedList.add((Material) packedMaterialsLegacy.get(2));
        linkedList.add((Material) packedMaterialsLegacy.get(12));
        linkedList.add((Material) packedMaterialsLegacy.get(13));
        linkedList.add((Material) packedMaterialsLegacy.get(14));
        linkedList.add((Material) packedMaterialsLegacy.get(24));
        linkedList.add((Material) packedMaterialsLegacy.get(25));
        linkedList.add((Material) packedMaterialsLegacy.get(26));
        return linkedList;
    }

    public static List getBodyMaterials(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterials = collisionState.getSurroundingMaterials();
        if (!(packedMaterials != null && !packedMaterials.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterials.get(3));
        linkedList.add((Material) packedMaterials.get(4));
        linkedList.add((Material) packedMaterials.get(5));
        linkedList.add((Material) packedMaterials.get(6));
        linkedList.add((Material) packedMaterials.get(7));
        linkedList.add((Material) packedMaterials.get(8));
        linkedList.add((Material) packedMaterials.get(15));
        linkedList.add((Material) packedMaterials.get(16));
        linkedList.add((Material) packedMaterials.get(17));
        linkedList.add((Material) packedMaterials.get(18));
        linkedList.add((Material) packedMaterials.get(19));
        linkedList.add((Material) packedMaterials.get(20));
        linkedList.add((Material) packedMaterials.get(27));
        linkedList.add((Material) packedMaterials.get(28));
        linkedList.add((Material) packedMaterials.get(29));
        linkedList.add((Material) packedMaterials.get(30));
        linkedList.add((Material) packedMaterials.get(31));
        linkedList.add((Material) packedMaterials.get(32));
        return linkedList;
    }

    public static List getBodyMaterialsLegacy(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterialsLegacy = collisionState.getSurroundingMaterialsLegacy();
        if (!(packedMaterialsLegacy != null && !packedMaterialsLegacy.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterialsLegacy.get(3));
        linkedList.add((Material) packedMaterialsLegacy.get(4));
        linkedList.add((Material) packedMaterialsLegacy.get(5));
        linkedList.add((Material) packedMaterialsLegacy.get(6));
        linkedList.add((Material) packedMaterialsLegacy.get(7));
        linkedList.add((Material) packedMaterialsLegacy.get(8));
        linkedList.add((Material) packedMaterialsLegacy.get(15));
        linkedList.add((Material) packedMaterialsLegacy.get(16));
        linkedList.add((Material) packedMaterialsLegacy.get(17));
        linkedList.add((Material) packedMaterialsLegacy.get(18));
        linkedList.add((Material) packedMaterialsLegacy.get(19));
        linkedList.add((Material) packedMaterialsLegacy.get(20));
        linkedList.add((Material) packedMaterialsLegacy.get(27));
        linkedList.add((Material) packedMaterialsLegacy.get(28));
        linkedList.add((Material) packedMaterialsLegacy.get(29));
        linkedList.add((Material) packedMaterialsLegacy.get(30));
        linkedList.add((Material) packedMaterialsLegacy.get(31));
        linkedList.add((Material) packedMaterialsLegacy.get(32));
        return linkedList;
    }

    public static List getCeilingMaterialsLegacy(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterialsLegacy = collisionState.getSurroundingMaterialsLegacy();
        if (!(packedMaterialsLegacy != null && !packedMaterialsLegacy.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterialsLegacy.get(9));
        linkedList.add((Material) packedMaterialsLegacy.get(10));
        linkedList.add((Material) packedMaterialsLegacy.get(11));
        linkedList.add((Material) packedMaterialsLegacy.get(21));
        linkedList.add((Material) packedMaterialsLegacy.get(22));
        linkedList.add((Material) packedMaterialsLegacy.get(23));
        linkedList.add((Material) packedMaterialsLegacy.get(33));
        linkedList.add((Material) packedMaterialsLegacy.get(34));
        linkedList.add((Material) packedMaterialsLegacy.get(35));
        return linkedList;
    }

    public static List getCeilingMaterials(PlayerData playerData3) {
        LinkedList linkedList = new LinkedList();
        CollisionState collisionState = playerData3.getCollisionState();
        List packedMaterials = collisionState.getSurroundingMaterials();
        if (!(packedMaterials != null && !packedMaterials.isEmpty())) {
            return linkedList;
        }
        linkedList.add((Material) packedMaterials.get(9));
        linkedList.add((Material) packedMaterials.get(10));
        linkedList.add((Material) packedMaterials.get(11));
        linkedList.add((Material) packedMaterials.get(21));
        linkedList.add((Material) packedMaterials.get(22));
        linkedList.add((Material) packedMaterials.get(23));
        linkedList.add((Material) packedMaterials.get(33));
        linkedList.add((Material) packedMaterials.get(34));
        linkedList.add((Material) packedMaterials.get(35));
        return linkedList;
    }

    public static boolean isRiptiding(PlayerData playerData3) {
        if (playerData3.getPlayer() == null) {
            return false;
        }
        if ((ExemptionManager.isNewerThan1_13()) && (playerData3.getPlayer().isRiptiding())) {
            return true;
        }
        return false;
    }

    public static boolean isGliding(PlayerData playerData3) {
        if (playerData3.getPlayer() == null) {
            return false;
        }
        if ((ExemptionManager.isAtLeast1_9()) && (playerData3.getPlayer().isGliding())) {
            return true;
        }
        return false;
    }

    public static int getPotionAmplifier(Player player2, PotionEffectType potionEffectType) {
        int id = potionEffectType.getId();
        for (PotionEffect potionEffect : player2.getActivePotionEffects()) {
            if (potionEffect.getType().getId() == id) {
                return potionEffect.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public static boolean touchesIceLegacy(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterialsLegacy() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterialsLegacy().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (!BlockUtils.isIce(collisionState.getSurroundingMaterialsLegacy().get(13))) {
                    if (!BlockUtils.isIce(playerData3.getCollisionState().getSubGroundMaterialLegacy())) {
                        if (!BlockUtils.isIce(playerData3.getCollisionState().getDeepGroundMaterialLegacy())) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean touchesWeb(PlayerData data) {
        List<Material> materials = data.getCollisionState().getSurroundingMaterials();
        return materials != null && materials.size() > 19 && (BlockUtils.isWeb(materials.get(16)) || BlockUtils.isWeb(materials.get(19)));
    }

    public static boolean touchesWebLegacy(PlayerData data) {
        List<Material> materials = data.getCollisionState().getSurroundingMaterialsLegacy();
        return materials != null && materials.size() > 19 && (BlockUtils.isWeb(materials.get(16)) || BlockUtils.isWeb(materials.get(19)));
    }

    public static boolean touchesLiquid(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterials() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterials().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (!BlockUtils.isLiquid(collisionState.getSurroundingMaterials().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (!BlockUtils.isLiquid(collisionState.getSurroundingMaterials().get(19))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean touchesLiquidLegacy(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterialsLegacy() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterialsLegacy().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (!BlockUtils.isLiquid(collisionState.getSurroundingMaterialsLegacy().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (!BlockUtils.isLiquid(collisionState.getSurroundingMaterialsLegacy().get(19))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean touchesClimbable(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterials() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterials().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (BlockUtils.isClimbable(collisionState.getSurroundingMaterials().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (BlockUtils.isClimbable(collisionState.getSurroundingMaterials().get(19))) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean touchesClimbableLegacy(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterialsLegacy() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterialsLegacy().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (BlockUtils.isClimbable(collisionState.getSurroundingMaterialsLegacy().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (BlockUtils.isClimbable(collisionState.getSurroundingMaterialsLegacy().get(19))) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static int getSpeedPotionLevel(LivingEntity livingEntity) {
        for (PotionEffect potionEffect : livingEntity.getActivePotionEffects()) {
            if (potionEffect.getType().toString().toLowerCase().contains("speed")) {
                return potionEffect.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public static boolean isValidBlockPlacement(BlockPlaceEvent blockPlaceEvent) {
        Material material;
        BlockPlaceEvent blockPlaceEvent2 = blockPlaceEvent;
        Player player = blockPlaceEvent2.getPlayer();
        if (!blockPlaceEvent2.getBlock().getType().isSolid())
            return false;
        Material type = blockPlaceEvent2.getBlock().getType();
        if (BlockUtils.isDirtPath(type))
            return false;
        if (BlockUtils.isFarmland(blockPlaceEvent2.getBlock()))
            return false;
        if (BlockUtils.isSand(blockPlaceEvent2.getBlock().getType()))
            return false;
        if (BlockUtils.isConcretePowder(blockPlaceEvent2.getBlock().getType()))
            return false;
        if (BlockUtils.isGravel(blockPlaceEvent2.getBlock().getType())) {
            return false;
        }
        Player player2 = player;
        PlayerData playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player2);
        if (playerData == null)
            return false;
        if (ExemptionManager.isNewerThan1_13()) {
            material = playerData.getCollisionState().getSubGroundMaterial();
        } else {
            material = playerData.getCollisionState().getSubGroundMaterialLegacy();
        }
        BlockFace blockFace = blockPlaceEvent2.getBlockAgainst().getFace(blockPlaceEvent2.getBlock());
        double x = blockPlaceEvent2.getBlockAgainst().getX();
        double y = blockPlaceEvent2.getBlockAgainst().getY();
        double z = blockPlaceEvent2.getBlockAgainst().getZ();
        double x2 = playerData.getCollisionState().getX();
        double y2 = playerData.getCollisionState().getY();
        double z2 = playerData.getCollisionState().getZ();
        Double xDifference = null;
        Double zDifference = Math.abs(z - z2);
        xDifference = Math.abs(x - x2);
        double amount = MathUtils.hypot(xDifference, zDifference);
        List<Material> list = playerData.getCollisionState().getFloorMaterials();
        if (list == null) {
            return false;
        }
        int index = (int) list.stream().filter(material2 -> BlockUtils.isAir(material2)).count();
        if (material == null)
            return false;
        if (blockFace == null)
            return false;
        if (blockFace == BlockFace.UP)
            return false;
        if (!BlockUtils.isAir(material))
            return false;
        if (!(amount < 2.25))
            return false;
        if (!(y < y2))
            return false;
        if (index <= 6)
            return false;
        return true;
    }

    public static boolean isFullySubmerged(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterials() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterials().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (BlockUtils.isLiquid(collisionState.getSurroundingMaterials().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (BlockUtils.isLiquid(collisionState.getSurroundingMaterials().get(19))) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean isFullySubmergedLegacy(PlayerData playerData3) {
        CollisionState collisionState = playerData3.getCollisionState();
        if (collisionState.getSurroundingMaterialsLegacy() != null) {
            collisionState = playerData3.getCollisionState();
            if (!collisionState.getSurroundingMaterialsLegacy().isEmpty()) {
                collisionState = playerData3.getCollisionState();
                if (BlockUtils.isLiquid(collisionState.getSurroundingMaterialsLegacy().get(16))) {
                    collisionState = playerData3.getCollisionState();
                    if (BlockUtils.isLiquid(collisionState.getSurroundingMaterialsLegacy().get(19))) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static boolean holdsRiptideTrident(PlayerData playerData3) {
        if (playerData3.getPlayer() == null) {
            return false;
        }
        if (!ExemptionManager.isNewerThan1_13()) {
            return false;
        }
        if ((playerData3.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("TRIDENT")) && (playerData3.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.RIPTIDE) > 0)) {
            return true;
        }
        if ((playerData3.getPlayer().getInventory().getItemInOffHand().getType().toString().contains("TRIDENT")) && (playerData3.getPlayer().getInventory().getItemInOffHand().getEnchantmentLevel(Enchantment.RIPTIDE) > 0)) {
            return true;
        }
        return false;
    }

    public static void sendHotbarShuffle(Player player2) {
        int index = ThreadLocalRandom.current().nextInt(8);
        PlayerDataStore playerDataStore = PluginServices.INSTANCE.getPlayerDataStore();
        sendPacket(playerDataStore.getPlayerData(player2), (PacketWrapper) new WrapperPlayServerHeldItemChange(index));
    }

    public static float getMaxSpeed(PlayerData playerData3, float amount) {
        MotionState motionState = playerData3.getMotionState();
        float scaledSpeedAmplifier = motionState.getSpeedAmplifier() * 0.0675F;
        float adjustedAdjustedScaledWalkSpeedDifference = amount + (scaledSpeedAmplifier + (playerData3.getCollisionState().getWalkSpeed() - 0.2F) * 3.5F);
        if (!(adjustedAdjustedScaledWalkSpeedDifference < amount)) {
            return amount;
        }
        MotionState motionState2 = playerData3.getMotionState();
        return amount + motionState2.getSpeedAmplifier() * 0.0675F;
    }

    public static void rotatePlayerRandomly(PlayerData playerData) {
        PlayerData playerData2 = playerData;
        if (playerData2.getPlayer() == null) {
            return;
        }
        World world = playerData2.getPlayer().getWorld();
        double x = playerData2.getPlayer().getLocation().getX();
        double y = playerData2.getPlayer().getLocation().getY();
        double z = playerData2.getPlayer().getLocation().getZ();
        float amount = (float) ThreadLocalRandom.current().nextDouble(360.0) - 180.0f;
        float amount2 = (float) ThreadLocalRandom.current().nextDouble(180.0) - 90.0f;
        Location location = new Location(world, x, y, z, amount, amount2);
        if (PluginServices.INSTANCE.isFoliaServer()) {
            WrapperPlayServerPlayerPositionAndLook wrapperPlayServerPlayerPositionAndLook = new WrapperPlayServerPlayerPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), (byte) 0, 69, false);
            playerData2.getUser().sendPacket((PacketWrapper) wrapperPlayServerPlayerPositionAndLook);
        } else {
            Runnable value = (() -> CheckUtils.teleportRotatedPlayerData(playerData2, location));
            MainThreadExecutor.execute(value);
        }
    }

    public static void rotatePlayerRandomly(Player player) {
        PlayerData playerData;
        Player player4 = player;
        playerData = PluginServices.INSTANCE.getPlayerDataStore().getPlayerData(player4);
        if ((playerData == null)) {
            return;
        }
        if ((playerData.getPlayer() == null)) {
            return;
        }
        World world = player.getWorld();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        float amount = (float) ThreadLocalRandom.current().nextDouble(360.0) - 180.0f;
        float amount2 = (float) ThreadLocalRandom.current().nextDouble(180.0) - 90.0f;
        Location location = new Location(world, x, y, z, amount, amount2);
        if (PluginServices.INSTANCE.isFoliaServer()) {
            WrapperPlayServerPlayerPositionAndLook wrapperPlayServerPlayerPositionAndLook = new WrapperPlayServerPlayerPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), (byte) 0, 69, false);
            playerData.getUser().sendPacket((PacketWrapper) wrapperPlayServerPlayerPositionAndLook);
        } else {
            Runnable runnable = (() -> CheckUtils.teleportRotatedPlayer(player, location));
            MainThreadExecutor.execute(runnable);
        }
    }

    public static boolean isWearingNetheriteArmor(PlayerData playerData3) {
        if ((ExemptionManager.isAtLeast1_16()) && (playerData3.getPlayer() != null)) {
            if (playerData3.getPlayer().getInventory().getHelmet() != null) {
                return playerData3.getPlayer().getInventory().getHelmet().getType() == Material.NETHERITE_HELMET;
            }
            if (playerData3.getPlayer().getInventory().getChestplate() != null) {
                return playerData3.getPlayer().getInventory().getChestplate().getType() == Material.NETHERITE_CHESTPLATE;
            }
            if (playerData3.getPlayer().getInventory().getLeggings() != null) {
                return playerData3.getPlayer().getInventory().getLeggings().getType() == Material.NETHERITE_LEGGINGS;
            }
            if (playerData3.getPlayer().getInventory().getBoots() != null) {
                return playerData3.getPlayer().getInventory().getBoots().getType() == Material.NETHERITE_BOOTS;
            }
            return false;
        }
        return false;
    }

    private CheckUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static void teleportRotatedPlayer(Player player2, Location location2) {
        player2.teleport(location2, TeleportCause.UNKNOWN);
    }

    private static void teleportRotatedPlayerData(PlayerData playerData3, Location location2) {
        playerData3.getPlayer().teleport(location2, TeleportCause.UNKNOWN);
    }
}
