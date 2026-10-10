package me.frep.vulcan.spigot.util.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import org.bukkit.Material;

public final class MaterialSets {

    public static final List<Double> jumpMotionValues = new ArrayList<>();

    public static final Map jumpBoostTable = new HashMap();

    public static final Map fallMotionTable = new HashMap();

    public static final Map sensitivityTable = new HashMap();

    public static final Map blockHardness = new HashMap();

    private MaterialSets() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static Material[] createMaterialArray(int index) {
        return new Material[index];
    }

    static {
        Material[] materialArray = Arrays.stream(Material.values()).filter(Material::isBlock).filter(Material::isSolid).toArray(MaterialSets::createMaterialArray);
        if (ExemptionManager.isNewerThan1_13()) {
            ArrayList<java.lang.String> arrayList = new ArrayList();
            arrayList.add("BED");
            arrayList.add("ICE");
            arrayList.add("CAKE");
            arrayList.add("PACKED_ICE");
            arrayList.add("FIRE");
            arrayList.add("CHEST");
            arrayList.add("IRON_DOOR");
            arrayList.add("WET_SPONGE");
            arrayList.add("BLUE_ICE");
            arrayList.add("BAMBOO_SAPLING");
            arrayList.add("SPONGE");
            arrayList.add("INFESTED_DEEPSLATE");
            arrayList.add("JACK_O_LANTERN");
            arrayList.add("CARVED_PUMPKIN");
            arrayList.add("OAK_DOOR");
            arrayList.add("DEEPSLATE");
            arrayList.add("WEB");
            arrayList.add("COBWEB");
            arrayList.add("HAY_BLOCK");
            arrayList.add("WET_SPONGE");
            arrayList.add("LECTERN");
            arrayList.add("SPRUCE_DOOR");
            arrayList.add("PUMPKIN");
            arrayList.add("INFESTED_STONE");
            arrayList.add("SPRUCE_TRAPDOOR");
            arrayList.add("OAK_DOOR");
            arrayList.add("SMOOTH_BRICKw");
            arrayList.add("NETHER_WART_BLOCK");
            arrayList.add("HAY_BALE");
            arrayList.add("WARPED_WART_BLOCK");
            arrayList.add("MELON_BLOCK");
            arrayList.add("SHROOMLIGHT");
            arrayList.add("BAMBOO");
            arrayList.add("GLASS");
            arrayList.add("MELON");
            arrayList.add("TURTLE_EGG");
            arrayList.add("FROSTED_ICE");
            arrayList.add("SPAWNER");
            arrayList.add("DRIED_KELP_BLOCK");
            arrayList.add("FIRE");
            for (Material material : materialArray) {
                if (!(material.getHardness() < 0.5) && !arrayList.contains(material.name())) {
                    blockHardness.put(material, material.getHardness());
                }
            }
            arrayList.clear();
        } else {
            for (Material material2 : materialArray) {
                String text = material2.name();
                switch(text) {
                    case "DIRT":
                    case "HAY_BLOCK":
                    case "MAGMA":
                    case "PISTON_BASE":
                    case "PISTON_EXTENSION":
                    case "PISTON_MOVING_PIECE":
                    case "PISTON_STICKY_BASE":
                    case "SAND":
                    case "SOUL_SAND":
                    case "CONCRETE_POWDER":
                        blockHardness.put(material2, 0.5F);
                        break;
                    case "TRAP_DOOR":
                    case "ACACIA_DOOR":
                    case "BIRCH_DOOR":
                    case "DARK_OAK_DOOR":
                    case "JUNGLE_DOOR":
                    case "OAK_DOOR":
                    case "SPRUCE_DOOR":
                    case "COAL_ORE":
                    case "REDSTONE_ORE":
                    case "QUARTZ_ORE":
                    case "LAPIS_ORE":
                    case "IRON_ORE":
                    case "GOLD_ORE":
                    case "EMERALD_ORE":
                    case "DIAMOND_ORE":
                    case "BEACON":
                    case "GOLD_BLOCK":
                    case "HOPPER":
                    case "LAPIS_BLOCK":
                    case "OBSERVER":
                        blockHardness.put(material2, 3.0F);
                        break;
                    case "IRON_TRAPDOOR":
                    case "IRON_DOOR":
                    case "IRON_DOOR_BLOCK":
                    case "ANVIL":
                    case "COAL_BLOCK":
                    case "DIAMOND_BLOCK":
                    case "EMERALD_BLOCK":
                    case "ENCHANTMENT_TABLE":
                    case "IRON_BARDING":
                    case "IRON_BLOCK":
                    case "REDSTONE_BLOCK":
                        blockHardness.put(material2, 5.0F);
                        break;
                    case "ACACIA_FENCE":
                    case "ACACIA_FENCE_GATE":
                    case "BIRCH_FENCE":
                    case "BIRCH_FENCE_GATE":
                    case "DARK_OAK_FENCE":
                    case "DARK_OAK_FENCE_GATE":
                    case "JUNGLE_FENCE":
                    case "JUNGLE_FENCE_GATE":
                    case "FENCE":
                    case "FENCE_GATE":
                    case "SPRUCE_FENCE":
                    case "SPRUCE_FENCE_GATE":
                    case "LOG":
                    case "LOG_2":
                    case "WOOD":
                    case "ACACIA_STAIRS":
                    case "SMOOTH_STAIRS":
                    case "BIRCH_WOOD_STAIRS":
                    case "BRICK_STAIRS":
                    case "COBBLESTONE_STAIRS":
                    case "DARK_OAK_STAIRS":
                    case "JUNGLE_WOOD_STAIRS":
                    case "NETHER_BRICK_STAIRS":
                    case "WOOD_STAIRS":
                    case "SPRUCE_WOOD_STAIRS":
                    case "ACACIA_SLAB":
                    case "PURPUR_SLAB":
                    case "STONE_SLAB2":
                    case "BRICK":
                    case "CAULDRON":
                    case "COBBLESTONE":
                    case "COBBLE_WALL":
                    case "JUKEBOX":
                    case "MOSSY_COBBLESTONE":
                    case "NETHER_BRICK":
                    case "RED_NETHER_BRICK":
                    case "BLACK_SHULKER_BOX":
                    case "BLUE_SHULKER_BOX":
                    case "BROWN_SHULKER_BOX":
                    case "CYAN_SHULKER_BOX":
                    case "GRAY_SHULKER_BOX":
                    case "GREEN_SHULKER_BOX":
                    case "LIGHT_BLUE_SHULKER_BOX":
                    case "LIGHT_GRAY_SHULKER_BOX":
                    case "LIME_SHULKER_BOX":
                    case "MAGENTA_SHULKER_BOX":
                    case "ORANGE_SHULKER_BOX":
                    case "PINK_SHULKER_BOX":
                    case "PURPLE_SHULKER_BOX":
                    case "RED_SHULKER_BOX":
                    case "SHULKER_BOX":
                    case "WHITE_SHULKER_BOX":
                    case "YELLOW_SHULKER_BOX":
                        blockHardness.put(material2, 2.0F);
                        break;
                    case "PURPUR_STAIRS":
                    case "BOOKSHELF":
                    case "PURPUR_BLOCK":
                    case "PURPUR_PILLAR":
                    case "STONE":
                        blockHardness.put(material2, 1.5F);
                        break;
                    case "QUARTZ_STAIRS":
                    case "RED_SANDSTONE_STAIRS":
                    case "SANDSTONE_STAIRS":
                    case "RED_SANDSTONE":
                    case "SANDSTONE":
                    case "WOOL":
                    case "END_BRICKS":
                    case "NOTE_BLOCK":
                    case "QUARTZ_BLOCK":
                        blockHardness.put(material2, 0.8F);
                        break;
                    case "ACTIVATOR_RAIL":
                    case "RAILS":
                    case "POWERED_RAIL":
                    case "DETECTOR_RAIL":
                        blockHardness.put(material2, 0.7F);
                        break;
                    case "NETHER_WART_BLOCK":
                    case "SIGN":
                    case "WALL_SIGN":
                        blockHardness.put(material2, 1.0F);
                        break;
                    case "ENDER_CHEST":
                        blockHardness.put(material2, 22.5F);
                        break;
                    case "OBSIDIAN":
                        blockHardness.put(material2, 50.0F);
                        break;
                    case "DISPENSER":
                    case "DROPPER":
                    case "FURNACE":
                        blockHardness.put(material2, 3.5F);
                        break;
                    case "SPONGE":
                    case "CLAY":
                    case "GRASS":
                    case "GRAVEL":
                    case "MYCEL":
                        blockHardness.put(material2, 0.6F);
                        break;
                    case "GRASS_PATH":
                        blockHardness.put(material2, 0.65F);
                        break;
                    case "BLACK_GLAZED_TERRACOTTA":
                    case "BLUE_GLAZED_TERRACOTTA":
                    case "BROWN_GLAZED_TERRACOTTA":
                    case "CYAN_GLAZED_TERRACOTTA":
                    case "GRAY_GLAZED_TERRACOTTA":
                    case "GREEN_GLAZED_TERRACOTTA":
                    case "LIGHT_BLUE_GLAZED_TERRACOTTA":
                    case "LIGHT_GRAY_GLAZED_TERRACOTTA":
                    case "LIME_GLAZED_TERRACOTTA":
                    case "MAGENTA_GLAZED_TERRACOTTA":
                    case "ORANGE_GLAZED_TERRACOTTA":
                    case "PINK_GLAZED_TERRACOTTA":
                    case "PURPLE_GLAZED_TERRACOTTA":
                    case "RED_GLAZED_TERRACOTTA":
                    case "WHITE_GLAZED_TERRACOTTA":
                    case "YELLOW_GLAZED_TERRACOTTA":
                        blockHardness.put(material2, 1.4F);
                        break;
                    case "CONCRETE":
                        blockHardness.put(material2, 1.8F);
                        break;
                    case "WORKBENCH":
                    case "TRAPPED_CHEST":
                        blockHardness.put(material2, 2.5F);
                }
            }
        }
        jumpBoostTable.put(1, 0.42F);
        jumpBoostTable.put(2, 0.33319999363422426);
        jumpBoostTable.put(3, 0.24813599859094637);
        jumpBoostTable.put(4, 0.164773281826065);
        jumpBoostTable.put(5, 0.08307781780646906);
        jumpBoostTable.put(6, -0.07840000152587834);
        jumpBoostTable.put(7, -0.15523200451659847);
        jumpBoostTable.put(8, -0.23052736891296632);
        jumpBoostTable.put(9, -0.30431682745754074);
        jumpBoostTable.put(10, -0.37663049823865435);
        jumpBoostTable.put(11, -0.1040803780930446);
        fallMotionTable.put(1, -0.07840000152587834);
        fallMotionTable.put(2, -0.15523200451659847);
        fallMotionTable.put(3, -0.23052736891296632);
        fallMotionTable.put(4, -0.30431682745754074);
        fallMotionTable.put(5, -0.37663049823865435);
        fallMotionTable.put(6, -0.44749789698342113);
        fallMotionTable.put(7, -0.5169479491049742);
        sensitivityTable.put(0, 0.0);
        sensitivityTable.put(1, 0.0070422534);
        sensitivityTable.put(2, 0.014084507);
        sensitivityTable.put(3, 0.0);
        sensitivityTable.put(4, 0.02112676);
        sensitivityTable.put(5, 0.028169014);
        sensitivityTable.put(6, 0.0281690166);
        sensitivityTable.put(7, 0.03521127);
        sensitivityTable.put(8, 0.04225352);
        sensitivityTable.put(9, 0.049295776);
        sensitivityTable.put(10, 0.0492957736);
        sensitivityTable.put(11, 0.056338027);
        sensitivityTable.put(12, 0.06338028);
        sensitivityTable.put(13, 0.0);
        sensitivityTable.put(14, 0.07042254);
        sensitivityTable.put(15, 0.07746479);
        sensitivityTable.put(16, 0.08450704);
        sensitivityTable.put(17, 0.0);
        sensitivityTable.put(18, 0.09154929);
        sensitivityTable.put(19, 0.09859155);
        sensitivityTable.put(20, 0.0);
        sensitivityTable.put(21, 0.1056338);
        sensitivityTable.put(22, 0.112676054);
        sensitivityTable.put(23, 0.11971831);
        sensitivityTable.put(24, 0.0);
        sensitivityTable.put(25, 0.12676056);
        sensitivityTable.put(26, 0.13380282);
        sensitivityTable.put(27, 0.0);
        sensitivityTable.put(28, 0.14084508);
        sensitivityTable.put(29, 0.14788732);
        sensitivityTable.put(30, 0.15492958);
        sensitivityTable.put(31, 0.0);
        sensitivityTable.put(32, 0.16197184);
        sensitivityTable.put(33, 0.16901408);
        sensitivityTable.put(34, 0.0);
        sensitivityTable.put(35, 0.17605634);
        sensitivityTable.put(36, 0.18309858);
        sensitivityTable.put(37, 0.0);
        sensitivityTable.put(38, 0.19014084);
        sensitivityTable.put(39, 0.1971831);
        sensitivityTable.put(40, 0.20422535);
        sensitivityTable.put(41, 0.0);
        sensitivityTable.put(42, 0.2112676);
        sensitivityTable.put(43, 0.21830986);
        sensitivityTable.put(44, 0.0);
        sensitivityTable.put(45, 0.22535211);
        sensitivityTable.put(46, 0.23239437);
        sensitivityTable.put(47, 0.23943663);
        sensitivityTable.put(48, 0.0);
        sensitivityTable.put(49, 0.24647887);
        sensitivityTable.put(50, 0.2535211);
        sensitivityTable.put(51, 0.0);
        sensitivityTable.put(52, 0.26056337);
        sensitivityTable.put(53, 0.26760563);
        sensitivityTable.put(54, 0.2746479);
        sensitivityTable.put(55, 0.0);
        sensitivityTable.put(56, 0.28169015);
        sensitivityTable.put(57, 0.28873238);
        sensitivityTable.put(58, 0.0);
        sensitivityTable.put(59, 0.29577464);
        sensitivityTable.put(60, 0.3028169);
        sensitivityTable.put(61, 0.30985916);
        sensitivityTable.put(62, 0.0);
        sensitivityTable.put(63, 0.31690142);
        sensitivityTable.put(64, 0.32394367);
        sensitivityTable.put(65, 0.0);
        sensitivityTable.put(66, 0.3309859);
        sensitivityTable.put(67, 0.33802816);
        sensitivityTable.put(68, 0.34507042);
        sensitivityTable.put(69, 0.0);
        sensitivityTable.put(70, 0.35211268);
        sensitivityTable.put(71, 0.35915494);
        sensitivityTable.put(72, 0.0);
        sensitivityTable.put(73, 0.36619717);
        sensitivityTable.put(74, 0.37323943);
        sensitivityTable.put(75, 0.0);
        sensitivityTable.put(76, 0.3802817);
        sensitivityTable.put(77, 0.38732395);
        sensitivityTable.put(78, 0.3943662);
        sensitivityTable.put(79, 0.0);
        sensitivityTable.put(80, 0.40140846);
        sensitivityTable.put(81, 0.4084507);
        sensitivityTable.put(82, 0.0);
        sensitivityTable.put(83, 0.41549295);
        sensitivityTable.put(84, 0.4225352);
        sensitivityTable.put(85, 0.42957747);
        sensitivityTable.put(86, 0.0);
        sensitivityTable.put(87, 0.43661973);
        sensitivityTable.put(88, 0.44366196);
        sensitivityTable.put(89, 0.0);
        sensitivityTable.put(90, 0.45070422);
        sensitivityTable.put(91, 0.45774648);
        sensitivityTable.put(92, 0.46478873);
        sensitivityTable.put(93, 0.0);
        sensitivityTable.put(94, 0.471831);
        sensitivityTable.put(95, 0.47887325);
        sensitivityTable.put(96, 0.0);
        sensitivityTable.put(97, 0.48591548);
        sensitivityTable.put(98, 0.49295774);
        sensitivityTable.put(99, 0.5);
        sensitivityTable.put(100, 0.5);
        sensitivityTable.put(101, 0.5070422);
        sensitivityTable.put(102, 0.5140845);
        sensitivityTable.put(103, 0.0);
        sensitivityTable.put(104, 0.52112675);
        sensitivityTable.put(105, 0.52816904);
        sensitivityTable.put(106, 0.0);
        sensitivityTable.put(107, 0.53521127);
        sensitivityTable.put(108, 0.5422535);
        sensitivityTable.put(109, 0.5492958);
        sensitivityTable.put(110, 0.0);
        sensitivityTable.put(111, 0.556338);
        sensitivityTable.put(112, 0.5633803);
        sensitivityTable.put(113, 0.0);
        sensitivityTable.put(114, 0.57042253);
        sensitivityTable.put(115, 0.57746476);
        sensitivityTable.put(116, 0.58450705);
        sensitivityTable.put(117, 0.0);
        sensitivityTable.put(118, 0.5915493);
        sensitivityTable.put(119, 0.59859157);
        sensitivityTable.put(120, 0.0);
        sensitivityTable.put(121, 0.6056338);
        sensitivityTable.put(122, 0.6126761);
        sensitivityTable.put(123, 0.6197183);
        sensitivityTable.put(124, 0.0);
        sensitivityTable.put(125, 0.62676054);
        sensitivityTable.put(126, 0.63380283);
        sensitivityTable.put(127, 0.0);
        sensitivityTable.put(128, 0.64084506);
        sensitivityTable.put(129, 0.64788735);
        sensitivityTable.put(130, 0.6549296);
        sensitivityTable.put(131, 0.0);
        sensitivityTable.put(132, 0.6619718);
        sensitivityTable.put(133, 0.6690141);
        sensitivityTable.put(134, 0.0);
        sensitivityTable.put(135, 0.6760563);
        sensitivityTable.put(136, 0.6830986);
        sensitivityTable.put(137, 0.0);
        sensitivityTable.put(138, 0.69014084);
        sensitivityTable.put(139, 0.6971831);
        sensitivityTable.put(140, 0.70422536);
        sensitivityTable.put(141, 0.0);
        sensitivityTable.put(142, 0.7112676);
        sensitivityTable.put(143, 0.7183099);
        sensitivityTable.put(144, 0.7253521);
        sensitivityTable.put(145, 0.7253521);
        sensitivityTable.put(146, 0.73239434);
        sensitivityTable.put(147, 0.7394366);
        sensitivityTable.put(148, 0.0);
        sensitivityTable.put(149, 0.74647886);
        sensitivityTable.put(150, 0.75352114);
        sensitivityTable.put(151, 0.0);
        sensitivityTable.put(152, 0.7605634);
        sensitivityTable.put(153, 0.76760566);
        sensitivityTable.put(154, 0.7746479);
        sensitivityTable.put(155, 0.0);
        sensitivityTable.put(156, 0.7816901);
        sensitivityTable.put(157, 0.7887324);
        sensitivityTable.put(158, 0.0);
        sensitivityTable.put(159, 0.79577464);
        sensitivityTable.put(160, 0.8028169);
        sensitivityTable.put(161, 0.80985916);
        sensitivityTable.put(162, 0.0);
        sensitivityTable.put(163, 0.8169014);
        sensitivityTable.put(164, 0.8239437);
        sensitivityTable.put(165, 0.0);
        sensitivityTable.put(166, 0.8309859);
        sensitivityTable.put(167, 0.8380282);
        sensitivityTable.put(168, 0.0);
        sensitivityTable.put(169, 0.8450704);
        sensitivityTable.put(170, 0.85211265);
        sensitivityTable.put(171, 0.85915494);
        sensitivityTable.put(172, 0.0);
        sensitivityTable.put(173, 0.86619717);
        sensitivityTable.put(174, 0.87323946);
        sensitivityTable.put(175, 0.0);
        sensitivityTable.put(176, 0.8802817);
        sensitivityTable.put(177, 0.8873239);
        sensitivityTable.put(178, 0.8943662);
        sensitivityTable.put(179, 0.0);
        sensitivityTable.put(180, 0.90140843);
        sensitivityTable.put(181, 0.9084507);
        sensitivityTable.put(182, 0.0);
        sensitivityTable.put(183, 0.91549295);
        sensitivityTable.put(184, 0.92253524);
        sensitivityTable.put(185, 0.92957747);
        sensitivityTable.put(186, 0.0);
        sensitivityTable.put(187, 0.9366197);
        sensitivityTable.put(188, 0.943662);
        sensitivityTable.put(189, 0.0);
        sensitivityTable.put(190, 0.9507042);
        sensitivityTable.put(191, 0.9577465);
        sensitivityTable.put(192, 0.96478873);
        sensitivityTable.put(193, 0.0);
        sensitivityTable.put(194, 0.97183096);
        sensitivityTable.put(195, 0.97887325);
        sensitivityTable.put(196, 0.0);
        sensitivityTable.put(197, 0.9859155);
        sensitivityTable.put(198, 0.9929578);
        sensitivityTable.put(199, 1.0);
        sensitivityTable.put(200, 1.0);
        jumpMotionValues.add(0.05999999821185753);
        jumpMotionValues.add(0.051999998867515274);
        jumpMotionValues.add(0.06159999881982969);
        jumpMotionValues.add(0.06927999889612124);
        jumpMotionValues.add(0.07542399904870933);
        jumpMotionValues.add(0.08033919924402255);
        jumpMotionValues.add(0.08427135945886732);
        jumpMotionValues.add(0.0874170876776148);
        jumpMotionValues.add(0.08993367029011523);
        jumpMotionValues.add(0.0519469373041872);
        jumpMotionValues.add(-0.05647059355944606);
        jumpMotionValues.add(0.03812980539822064);
        jumpMotionValues.add(-0.035014067535591664);
        jumpMotionValues.add(-0.04453032983624894);
        jumpMotionValues.add(0.019999999105927202);
        jumpMotionValues.add(-0.07159953051526458);
        jumpMotionValues.add(0.020820931761605266);
        jumpMotionValues.add(0.0010261658043049238);
        jumpMotionValues.add(-0.023717291273619878);
        jumpMotionValues.add(-0.010724939925282229);
    }
}
