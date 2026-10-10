package me.frep.vulcan.spigot.world.block;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class BlockUtils {

    public static List<Material> getSurroundingMaterials(World world, int x, int y, int z, int radius) {
        List<Material> materials = new LinkedList<>();
        if (world == null) {
            return materials;
        }
        for (int blockX = x - radius; blockX <= x + radius; blockX++) {
            for (int blockY = y - radius; blockY <= y + radius + 1; blockY++) {
                for (int blockZ = z - radius; blockZ <= z + radius; blockZ++) {
                    if (world.isChunkLoaded(blockX >> 4, blockZ >> 4)) {
                        materials.add(world.getBlockAt(blockX, blockY, blockZ).getType());
                    } else {
                        materials.add(Material.SPONGE);
                    }
                }
            }
        }
        return materials;
    }

    public static List<Material> getDeepFloorMaterials(World world, int x, int y, int z) {
        List<Material> materials = new LinkedList<>();
        if (world == null) {
            return materials;
        }
        materials.add(world.getBlockAt(x + 1, y - 2, z).getType());
        materials.add(world.getBlockAt(x - 1, y - 2, z).getType());
        materials.add(world.getBlockAt(x, y - 2, z + 1).getType());
        materials.add(world.getBlockAt(x, y - 2, z - 1).getType());
        materials.add(world.getBlockAt(x + 1, y - 2, z + 1).getType());
        materials.add(world.getBlockAt(x - 1, y - 2, z + 1).getType());
        materials.add(world.getBlockAt(x - 1, y - 2, z - 1).getType());
        materials.add(world.getBlockAt(x, y - 2, z).getType());
        materials.add(world.getBlockAt(x + 1, y - 2, z - 1).getType());
        return materials;
    }

    public static Material getMaterial(World world, int x, int y, int z) {
        if (world == null) {
            return Material.SPONGE;
        }
        return world.isChunkLoaded(x >> 4, z >> 4) ? world.getBlockAt(x, y, z).getType() : Material.SPONGE;
    }

    public static boolean isCake(Material material2) {
        return material2.toString().contains("CAKE");
    }

    public static boolean isAir(Material material) {
        String name = material.name();
        return name.equals("AIR") || name.equals("CAVE_AIR") || name.equals("VOID_AIR");
    }

    public static boolean isDriedGhast(Material material2) {
        return material2.toString().equals("DRIED_GHAST");
    }

    public static boolean isHeavyCore(Material material2) {
        return material2.toString().equals("HEAVY_CORE");
    }

    public static boolean isSolid(Material material2) {
        return material2.isSolid();
    }

    public static boolean isSign(Material material2) {
        return material2.toString().contains("SIGN");
    }

    public static boolean isRepeater(Material material2) {
        return material2.toString().equals("REPEATER") || material2.toString().equals("DIODE");
    }

    public static boolean isBlueIce(Material material2) {
        return material2.toString().equals("BLUE_ICE");
    }

    public static boolean isSculk(Material material2) {
        return material2.toString().contains("SCULK");
    }

    public static boolean isSculkSensor(Material material2) {
        return material2.toString().equals("SCULK_SENSOR");
    }

    public static boolean isFlowerPot(Material material2) {
        return material2.toString().equals("FLOWER_POT");
    }

    public static boolean isConduit(Material material2) {
        return material2.toString().equals("CONDUIT");
    }

    public static boolean isBamboo(Material material2) {
        return material2.toString().contains("BAMBOO");
    }

    public static boolean isIronBars(Material material2) {
        return material2.toString().equals("IRON_BARS");
    }

    public static boolean isLava(Material material2) {
        return material2.toString().contains("LAVA");
    }

    public static boolean isPressurePlate(Material material2) {
        return material2.toString().contains("PLATE");
    }

    public static boolean isGlassPane(Block block2) {
        return block2.getType().toString().contains("PANE");
    }

    public static boolean isRail(Material material2) {
        return material2.toString().contains("RAIL");
    }

    public static boolean isGlass(Material material2) {
        return material2.toString().contains("GLASS") && !material2.toString().contains("PANE");
    }

    public static boolean isPortalFrame(Material material2) {
        return material2.toString().contains("FRAME") && material2.toString().contains("FRAME");
    }

    public static boolean isGlassPane(Material material2) {
        return material2.toString().contains("PANE");
    }

    public static boolean isPowderSnow(Material material2) {
        return material2.toString().equals("POWDER_SNOW");
    }

    public static boolean isSeagrass(Material material2) {
        return material2.toString().contains("GRASS") && material2.toString().contains("SEA");
    }

    public static boolean isNoteBlock(Material material2) {
        return material2.toString().equals("NOTE_BLOCK");
    }

    public static boolean isKelp(Material material2) {
        return material2.toString().contains("KELP");
    }

    public static boolean isAmethyst(Material material2) {
        return material2.toString().contains("AMETHYST");
    }

    public static boolean isDripstone(Material material2) {
        return material2.toString().contains("DRIPSTONE");
    }

    public static boolean isShulkerBox(Material material2) {
        return material2.toString().contains("SHULKER");
    }

    public static boolean isEndRod(Material material2) {
        return material2.toString().equals("END_ROD");
    }

    public static boolean isCactus(Material material2) {
        return material2.toString().equalsIgnoreCase("CACTUS");
    }

    public static boolean isCauldron(Material material2) {
        return material2.toString().equals("CAULDRON");
    }

    public static boolean isFenceGate(Material material2) {
        return material2.toString().contains("GATE");
    }

    public static boolean isGrowable(Material material2) {
        return material2.toString().contains("GROWABLE");
    }

    public static boolean isSweetBerryBush(Material material2) {
        return material2.toString().equalsIgnoreCase("SWEET_BERRY_BUSH");
    }

    public static boolean isPiston(Material material2) {
        return material2.toString().contains("PISTON");
    }

    public static boolean isDoor(Material material2) {
        return material2.toString().contains("DOOR");
    }

    public static boolean isHopper(Material material2) {
        return material2.toString().equals("HOPPER");
    }

    public static boolean isChain(Block block2) {
        return block2.getType().toString().contains("CHAIN");
    }

    public static boolean isChain(Material material2) {
        return material2.toString().equals("CHAIN");
    }

    public static boolean isClimbable(Block block2) {
        return block2.getType().toString().contains("LADDER") || block2.getType().toString().contains("VINE") || block2.getType().toString().equalsIgnoreCase("SCAFFOLDING");
    }

    public static boolean isClimbable(Material material2) {
        return material2.toString().equals("LADDER") || material2.toString().contains("VINE") || material2.toString().equals("SCAFFOLDING");
    }

    public static boolean isSnow(Material material2) {
        return material2.toString().equals("SNOW");
    }

    public static boolean isLiquid(Block block2) {
        return block2.getType().toString().contains("LAVA") || block2.getType().toString().contains("WATER") || block2.getType().toString().contains("BUBBLE");
    }

    public static boolean isLiquid(Material material2) {
        return material2.toString().contains("LAVA") || material2.toString().contains("WATER") || material2.toString().contains("BUBBLE");
    }

    public static boolean isSeaPickle(Block block2) {
        return block2.getType().toString().contains("PICKLE");
    }

    public static boolean isSeaPickle(Material material2) {
        return material2.toString().equals("SEA_PICKLE");
    }

    public static boolean isTurtleEgg(Block block2) {
        return block2.getType().toString().contains("TURTLE") && block2.getType().toString().contains("EGG");
    }

    public static boolean isTurtleEgg(Material material2) {
        return material2.toString().equals("TURTLE_EGG");
    }

    public static boolean isLectern(Block block2) {
        return block2.getType().toString().contains("LECTERN");
    }

    public static boolean isLectern(Material material2) {
        return material2.toString().equals("LECTERN");
    }

    public static boolean isWeb(Block block2) {
        return block2.getType().toString().contains("WEB");
    }

    public static boolean isWeb(Material material2) {
        return material2.toString().equals("WEB") || material2.toString().equals("COBWEB");
    }

    public static boolean isAnvil(Block block2) {
        return block2.getType().toString().contains("ANVIL");
    }

    public static boolean isAnvil(Material material2) {
        return material2.toString().equals("ANVIL") || material2.toString().equals("DAMAGED_ANVIL") || material2.toString().equals("CHIPPED_ANVIL");
    }

    public static boolean isSlime(Block block2) {
        return block2.getType().toString().contains("SLIME");
    }

    public static boolean isTnt(Material material2) {
        return material2.toString().equals("TNT");
    }

    public static boolean isSlimeBlock(Material material2) {
        return material2.toString().equals("SLIME_BLOCK");
    }

    public static boolean isBoat(Block block2) {
        return block2.getType().toString().contains("BOAT");
    }

    public static boolean isFarmland(Block block2) {
        return block2.getType().toString().equals("FARMLAND") || block2.getType().toString().contains("FARMLAND");
    }

    public static boolean isFarmland(Material material2) {
        return material2.toString().contains("FARMLAND");
    }

    public static boolean isWall(Material material2) {
        return material2.toString().contains("WALL") && !material2.toString().contains("SIGN");
    }

    public static boolean isSweetBerries(Block block2) {
        return block2.getType().toString().contains("SWEET");
    }

    public static boolean isGlassBottle(Material material2) {
        return material2.toString().equals("GLASS_BOTTLE");
    }

    public static boolean isSweetBerries(Material material2) {
        return material2.toString().equals("SWEET_BERRY_BUSH");
    }

    public static boolean isLilyPad(Block block2) {
        return block2.getType().toString().contains("LILY");
    }

    public static boolean isLilyPad(Material material2) {
        return material2.toString().equals("LILY_PAD") || material2.toString().equals("WATER_LILY");
    }

    public static boolean isBed(Material material2) {
        return material2.toString().contains("BED") && !material2.toString().equals("BEDROCK");
    }

    public static boolean isPortalFrame(Block block2) {
        return block2.getType().toString().contains("FRAME");
    }

    public static boolean isEndPortalFrame(Material material2) {
        return material2.toString().equals("END_PORTAL_FRAME") || material2.toString().equals("ENDER_PORTAL_FRAME");
    }

    public static boolean isFence(Block block2) {
        return block2.getType().toString().contains("FENCE");
    }

    public static boolean isFence(Material material2) {
        return material2.toString().contains("FENCE");
    }

    public static boolean isDaylightDetector(Block block2) {
        return block2.getType().toString().contains("DAYLIGHT");
    }

    public static boolean isDaylightDetector(Material material2) {
        return material2.toString().equals("DAYLIGHT_DETECTOR");
    }

    public static boolean isStairs(Block block2) {
        return block2.getType().toString().contains("STAIR");
    }

    public static boolean isStairs(Material material2) {
        return material2.toString().contains("STAIR");
    }

    public static boolean isSlab(Block block2) {
        return block2.getType().toString().contains("SLAB") || block2.getType().toString().contains("STEP");
    }

    public static boolean isSlab(Material material2) {
        return material2.toString().contains("SLAB") || material2.toString().contains("STEP");
    }

    public static boolean isTrapdoor(Material material2) {
        return material2.toString().contains("TRAP") && material2.toString().contains("DOOR");
    }

    public static boolean isSkull(Block block2) {
        return block2.getType().toString().contains("SKULL") || block2.getType().toString().contains("HEAD");
    }

    public static boolean isSkull(Material material2) {
        return material2.toString().contains("SKULL") || material2.toString().contains("HEAD");
    }

    public static boolean isHoney(Block block2) {
        return block2.getType().toString().contains("HONEY");
    }

    public static boolean isHoney(Material material2) {
        return material2.toString().equals("HONEY_BLOCK");
    }

    public static boolean isBubbleColumn(Block block2) {
        return block2.getType().toString().contains("BUBBLE");
    }

    public static boolean isBubbleColumn(Material material2) {
        return material2.toString().equals("BUBBLE_COLUMN");
    }

    public static boolean isScaffolding(Block block2) {
        return block2.getType().toString().equals("SCAFFOLDING");
    }

    public static boolean isScaffolding(Material material2) {
        return material2.toString().equals("SCAFFOLDING");
    }

    public static boolean isCampfire(Block block2) {
        return block2.getType().toString().contains("CAMPFIRE");
    }

    public static boolean isCampfire(Material material2) {
        return material2.toString().equals("CAMPFIRE") || material2.toString().equals("SOUL_CAMPFIRE");
    }

    public static boolean isBrewingStand(Block block2) {
        return block2.getType().toString().contains("BREWING");
    }

    public static boolean isBrewingStand(Material material2) {
        return material2.toString().equals("BREWING_STAND");
    }

    public static boolean isFrostedIce(Material material2) {
        return material2.toString().equals("FROSTED_ICE");
    }

    public static boolean isCarpet(Block block2) {
        return block2.getType().toString().contains("CARPET");
    }

    public static boolean isCarpet(Material material2) {
        return material2.toString().contains("CARPET");
    }

    public static boolean isIce(Material material2) {
        return material2.toString().equals("ICE") || material2.toString().equals("PACKED_ICE") || material2.toString().equals("BLUE_ICE") || material2.toString().equals("FROSTED_ICE");
    }

    public static boolean isTripwire(Block block2) {
        return block2.getType().toString().contains("WIRE") || block2.getType().toString().contains("STRING");
    }

    public static boolean isBell(Block block2) {
        return block2.getType().toString().contains("BELL");
    }

    public static boolean isBell(Material material2) {
        return material2.toString().equals("BELL");
    }

    public static boolean isSoulBlock(Block block2) {
        return block2.getType().toString().contains("SOUL");
    }

    public static boolean isSoulSand(Material material2) {
        return material2.toString().equalsIgnoreCase("SOUL_SAND");
    }

    public static boolean isSoulSandOrSoil(Material material2) {
        return material2.toString().equals("SOUL_SAND") || material2.toString().equalsIgnoreCase("SOUL_SOIL");
    }

    public static boolean isChest(Material material2) {
        return material2.toString().contains("CHEST") || material2.toString().equalsIgnoreCase("CHEST");
    }

    public static boolean isDeepslate(Material material2) {
        return material2.toString().contains("DEEPSLATE");
    }

    public static boolean isDirtPath(Material material2) {
        return material2.toString().contains("PATH");
    }

    public static boolean isSand(Material material2) {
        return material2.toString().contains("SAND") && !material2.toString().contains("STONE");
    }

    public static boolean isGravel(Material material2) {
        return material2.toString().contains("GRAVEL");
    }

    public static boolean isLantern(Material material2) {
        return material2.toString().contains("LANTERN");
    }

    public static boolean isConcretePowder(Material material2) {
        return material2.toString().contains("CONCRETE") && material2.toString().contains("POWDER");
    }

    public static boolean isStrippedWood(Material material2) {
        return material2.toString().contains("STRIPPED");
    }

    public static boolean isEnchantingTable(Material material2) {
        return material2.toString().contains("ENCHANT");
    }

    public static boolean isCandle(Material material2) {
        return material2.toString().contains("CANDLE");
    }

    public static boolean isDripleaf(Material material2) {
        return material2.toString().contains("DRIP") && material2.toString().contains("LEAF");
    }

    public static boolean isRespawnAnchor(Material material2) {
        return material2.toString().contains("ANCHOR");
    }

    public static boolean isGlowstone(Material material2) {
        return material2.toString().equals("GLOWSTONE");
    }

    private BlockUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
