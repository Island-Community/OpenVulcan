package me.frep.vulcan.spigot.check.impl.player.airplace;

import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Air Place", type = 'A', complexType = "Invalid", description = "Invalid block placement.")
public class AirPlaceA extends AbstractCheck {

    private static final String VIOLATION_DETAIL_PREFIX;

    public AirPlaceA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event) {
        BlockPlaceEvent blockPlaceEvent;
        if (!(event instanceof BlockPlaceEvent))
            return;
        blockPlaceEvent = (BlockPlaceEvent) event;
        boolean exempt = this.isExempt((new ExemptType[] { ExemptType.SPECTATOR, ExemptType.CANCELLED_PLACE, ExemptType.BLOCK_BREAK }));
        if ((blockPlaceEvent.isCancelled())) {
            return;
        }
        if ((exempt)) {
            return;
        }
        if ((BlockUtils.isSlab(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        if ((BlockUtils.isSand(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        if ((BlockUtils.isGravel(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        Material type5 = blockPlaceEvent.getBlock().getType();
        if ((BlockUtils.isAnvil(type5))) {
            return;
        }
        Material type2 = blockPlaceEvent.getBlock().getType();
        if ((BlockUtils.isTnt(type2))) {
            return;
        }
        Material type3 = blockPlaceEvent.getBlock().getType();
        if ((BlockUtils.isDirtPath(type3))) {
            return;
        }
        if ((BlockUtils.isTnt(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        if ((BlockUtils.isGlowstone(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        if ((BlockUtils.isRespawnAnchor(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        if ((BlockUtils.isConcretePowder(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        if ((BlockUtils.isStrippedWood(blockPlaceEvent.getBlock().getType()))) {
            return;
        }
        Material type4 = blockPlaceEvent.getBlock().getType();
        if ((BlockUtils.isFarmland(type4))) {
            return;
        }
        if ((blockPlaceEvent.getBlockAgainst().getX() == blockPlaceEvent.getBlock().getX()) && (blockPlaceEvent.getBlockAgainst().getY() == blockPlaceEvent.getBlock().getY()) && (blockPlaceEvent.getBlockAgainst().getZ() == blockPlaceEvent.getBlock().getZ()) && (BlockUtils.isAir(blockPlaceEvent.getBlock().getLocation().add(0.0, 1.0, 0.0).getBlock().getType())) && (BlockUtils.isAir(blockPlaceEvent.getBlock().getLocation().add(0.0, -1.0, 0.0).getBlock().getType())) && (BlockUtils.isAir(blockPlaceEvent.getBlock().getLocation().add(1.0, 0.0, 0.0).getBlock().getType())) && (BlockUtils.isAir(blockPlaceEvent.getBlock().getLocation().add(-1.0, 0.0, 0.0).getBlock().getType())) && (BlockUtils.isAir(blockPlaceEvent.getBlock().getLocation().add(0.0, 0.0, 1.0).getBlock().getType())) && (BlockUtils.isAir(blockPlaceEvent.getBlock().getLocation().add(0.0, 0.0, -1.0).getBlock().getType()))) {
            this.fail(VIOLATION_DETAIL_PREFIX + blockPlaceEvent.getBlock().getType());
            if (VulcanSettings.checksPlayerAirplaceACancelPlace) {
                blockPlaceEvent.setCancelled(true);
            }
        }
    }

    static {
        VIOLATION_DETAIL_PREFIX = "block=";
    }
}
