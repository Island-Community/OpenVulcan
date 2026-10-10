package me.frep.vulcan.spigot.check.impl.player.scaffold;

import java.util.ArrayList;
import java.util.List;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.geometry.ray.RaySegment;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.tracker.rotation.RotationTracker;
import me.frep.vulcan.spigot.util.numbers.MathUtils;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

@CheckInfo(name = "Scaffold", type = 'N', complexType = "Expand", description = "Invalid block face.", experimental = true)
public class ScaffoldN extends AbstractCheck {

    private List<org.bukkit.block.Block> recentPlacements = new ArrayList();

    public ScaffoldN(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void update() {
    }

    public void handleEvent(Event event2) {
        if (event2 instanceof BlockPlaceEvent blockPlaceEvent) {
            if (blockPlaceEvent.isCancelled()) {
                return;
            }
            if (BlockUtils.isScaffolding(blockPlaceEvent.getBlock().getType()) || BlockUtils.isSlab(blockPlaceEvent.getBlock().getType())) {
                return;
            }
            if (BlockUtils.isStairs(blockPlaceEvent.getBlock().getType())) {
                return;
            }
            Block block = blockPlaceEvent.getBlock();
            if (this.recentPlacements.contains(blockPlaceEvent.getBlockAgainst())) {
                double y = blockPlaceEvent.getBlockPlaced().getY();
                CollisionState collisionState = this.playerData.getCollisionState();
                boolean belowYLimit = y < collisionState.getY();
                BlockFace blockFace = blockPlaceEvent.getBlockAgainst().getFace(blockPlaceEvent.getBlock());
                Material material;
                if (ExemptionManager.isNewerThan1_13()) {
                    material = this.playerData.getCollisionState().getSubGroundMaterial();
                } else {
                    material = this.playerData.getCollisionState().getSubGroundMaterialLegacy();
                }
                if (material == null || blockFace == null) {
                    return;
                }
                if (blockFace == BlockFace.DOWN || blockFace == BlockFace.UP || blockFace == BlockFace.SELF) {
                    return;
                }
                CollisionState collisionState2 = this.playerData.getCollisionState();
                double xDifference = Math.abs(collisionState2.getX() - blockPlaceEvent.getBlockPlaced().getX());
                CollisionState collisionState3 = this.playerData.getCollisionState();
                double zDifference = Math.abs(collisionState3.getZ() - blockPlaceEvent.getBlockPlaced().getZ());
                double distanceXZ = MathUtils.hypot(xDifference, zDifference);
                int blockX = blockPlaceEvent.getBlock().getLocation().getBlockX();
                int blockZ = blockPlaceEvent.getBlock().getLocation().getBlockZ();
                CollisionState collisionState4 = this.playerData.getCollisionState();
                int blockX2 = collisionState4.getBlockX();
                CollisionState collisionState5 = this.playerData.getCollisionState();
                int blockZ2 = collisionState5.getBlockZ();
                boolean condition2 = blockX == blockX2 || blockZ == blockZ2;
                boolean condition3 = BlockUtils.isAir(material) && belowYLimit && distanceXZ > 1.85 && condition2;
                boolean exempt = this.isExempt(new ExemptType[] { ExemptType.FLIGHT, ExemptType.CREATIVE });
                if (condition3 && !exempt) {
                    if (this.playerData.getPlayer() == null) {
                        return;
                    }
                    Vector vector = this.playerData.getPlayer().getLocation().toVector();
                    RotationTracker rotationTracker = this.playerData.getRotationTracker();
                    float yaw = rotationTracker.getYaw();
                    RotationTracker rotationTracker2 = this.playerData.getRotationTracker();
                    RaySegment raySegment = new RaySegment(vector, this.getPosition(yaw, rotationTracker2.getPitch()));
                    World world = this.playerData.getPlayer().getWorld();
                    List list = raySegment.solidBlocksAlongRay(world, 3.0, 1.0);
                    if (!list.isEmpty()) {
                        if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("distanceXZ=" + distanceXZ + " r=" + list.size());
                        }
                    } else {
                        this.decayBuffer();
                    }
                }
                this.recentPlacements.clear();
            }
            this.recentPlacements.add(block);
        }
    }

    private BlockFace faceFromYaw(float amount) {
        float amount2 = (amount - 90.0F) % 360.0F;
        if (amount2 < 0.0F) {
            amount2 = (float) (amount2 + 360.0);
        }
        return this.faceFromAngle(amount2);
    }

    private BlockFace faceFromAngle(float amount) {
        if (0.0F <= amount && amount < 45.0F) {
            return BlockFace.WEST;
        }
        if (45.0F <= amount && amount < 135.0F) {
            return BlockFace.NORTH;
        }
        if (135.0F <= amount && amount < 225.0F) {
            return BlockFace.EAST;
        }
        if (225.0F <= amount && amount < 315.0F) {
            return BlockFace.SOUTH;
        }
        return 315.0F <= amount && amount < 360.0 ? BlockFace.WEST : BlockFace.SELF;
    }

    public Vector getPosition(float amount, float amount2) {
        Vector vector = new Vector();
        double amount3 = amount;
        double amount4 = amount2;
        vector.setY(-Math.sin(Math.toRadians(amount4)));
        double amount5 = Math.cos(Math.toRadians(amount4));
        vector.setX(-amount5 * Math.sin(Math.toRadians(amount3)));
        vector.setZ(amount5 * Math.cos(Math.toRadians(amount3)));
        return vector;
    }
}
