package me.frep.vulcan.spigot.world.registry;

import org.bukkit.enchantments.Enchantment;

public final class EnchantmentMapper {

    public static final Enchantment AQUA_AFFINITY;

    private static Enchantment findAquaAffinity() {
        Enchantment byName = Enchantment.getByName("AQUA_AFFINITY");
        return byName != null ? byName : Enchantment.getByName("WATER_WORKER");
    }

    private EnchantmentMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        AQUA_AFFINITY = findAquaAffinity();
    }
}
