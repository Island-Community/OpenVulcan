package me.frep.vulcan.spigot.check.impl.player.ghosthand;

import java.util.ArrayList;
import java.util.List;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

@CheckInfo(name = "Ghost Hand", type = 'A', complexType = "Bed", description = "Invalid bed break.")
public class GhostHandA extends AbstractCheck {

    public GhostHandA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event) {
        if (!(event instanceof BlockBreakEvent)) {
            return;
        }
        Block broken = ((BlockBreakEvent) event).getBlock();
        String brokenType = broken.getType().toString();
        if (!brokenType.contains("BED") || brokenType.equalsIgnoreCase("BEDROCK")) {
            return;
        }
        if (this.playerData.getCollisionState().getY() - broken.getY() < 0.0) {
            return;
        }
        BlockFace facing = getBedFacing(broken);
        if (facing == null) {
            return;
        }
        checkSurroundings((BlockBreakEvent) event, facing);
    }

    private static BlockFace getBedFacing(Block broken) {
        BlockFace facing = null;
        if (broken.getLocation().add(0.0, 0.0, 1.0).getBlock().getType().toString().contains("BED")) {
            facing = BlockFace.SOUTH;
        }
        if (broken.getLocation().add(-1.0, 0.0, 0.0).getBlock().getType().toString().contains("BED")) {
            facing = BlockFace.WEST;
        }
        if (broken.getLocation().add(0.0, 0.0, -1.0).getBlock().getType().toString().contains("BED")) {
            facing = BlockFace.NORTH;
        }
        if (broken.getLocation().add(1.0, 0.0, 0.0).getBlock().getType().toString().contains("BED")) {
            facing = BlockFace.EAST;
        }
        return facing;
    }

    private void checkSurroundings(BlockBreakEvent event, BlockFace facing) {
        List<Material> nearby = new ArrayList<>();
        for (double[] offset : neighborOffsets(facing)) {
            nearby.add(event.getBlock().getLocation().add(offset[0], offset[1], offset[2]).getBlock().getType());
        }
        boolean surrounded = nearby.stream().allMatch(Material::isSolid) && nearby.stream().allMatch(Material::isOccluding);
        nearby.remove(0);
        nearby.remove(0);
        if (!surrounded) {
            if (!nearby.stream().allMatch(BlockUtils::isGlass)) {
                return;
            }
        }
        this.fail("direction=" + facing + " blocks=" + nearby);
        if (VulcanSettings.checksPlayerGhosthandACancel) {
            event.setCancelled(true);
        }
    }

    private static double[][] neighborOffsets(BlockFace facing) {
        switch(facing) {
            case SOUTH:
                return new double[][] { { 0.0, -1.0, 0.0 }, { 0.0, -1.0, 1.0 }, { 0.0, 0.0, -1.0 }, { -1.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0 }, { -1.0, 0.0, 1.0 }, { 1.0, 0.0, 1.0 }, { 0.0, 0.0, 2.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 1.0, 1.0 } };
            case NORTH:
                return new double[][] { { 0.0, -1.0, 0.0 }, { 0.0, -1.0, -1.0 }, { 0.0, 0.0, 1.0 }, { -1.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0 }, { -1.0, 0.0, -1.0 }, { 1.0, 0.0, -1.0 }, { 0.0, 0.0, -2.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 1.0, -1.0 } };
            case EAST:
                return new double[][] { { 0.0, -1.0, 0.0 }, { 1.0, -1.0, 0.0 }, { -1.0, 0.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, -1.0 }, { 1.0, 0.0, 1.0 }, { 1.0, 0.0, -1.0 }, { 2.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { 1.0, 1.0, 0.0 } };
            case WEST:
                return new double[][] { { 0.0, -1.0, 0.0 }, { -1.0, -1.0, 0.0 }, { 1.0, 0.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, -1.0 }, { -1.0, 0.0, 1.0 }, { -1.0, 0.0, -1.0 }, { -2.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { -1.0, 1.0, 0.0 } };
            default:
                return new double[0][0];
        }
    }
}
