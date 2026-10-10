package me.frep.vulcan.spigot.world.entity;

import org.bukkit.entity.Entity;

public final class EntityTypeMatcher {

    public static boolean isShulker(Entity entity2) {
        return entity2.getType().toString().equals("SHULKER");
    }

    public static boolean isBoat(Entity entity2) {
        return entity2.getType().toString().contains("BOAT") || entity2.getType().toString().contains("RAFT");
    }

    public static boolean isHappyGhast(Entity entity2) {
        return entity2.getType().toString().equals("HAPPY_GHAST");
    }

    private EntityTypeMatcher() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
