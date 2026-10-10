package me.frep.vulcan.spigot.geometry.box;

import me.frep.vulcan.spigot.geometry.direction.Direction;
import me.frep.vulcan.spigot.geometry.position.BlockPosition;
import me.frep.vulcan.spigot.geometry.vector.Vector3D;
import me.frep.vulcan.spigot.geometry.ray.VectorRayHit;

public class AxisAlignedBox {

    public final double minX;

    public final double minY;

    public final double minZ;

    public final double maxX;

    public final double maxY;

    public final double maxZ;

    public AxisAlignedBox(double firstX, double firstY, double firstZ, double secondX, double secondY, double secondZ) {
        this.minX = Math.min(firstX, secondX);
        this.minY = Math.min(firstY, secondY);
        this.minZ = Math.min(firstZ, secondZ);
        this.maxX = Math.max(firstX, secondX);
        this.maxY = Math.max(firstY, secondY);
        this.maxZ = Math.max(firstZ, secondZ);
    }

    public AxisAlignedBox(BlockPosition minimum, BlockPosition maximum) {
        this.minX = minimum.getX();
        this.minY = minimum.getY();
        this.minZ = minimum.getZ();
        this.maxX = maximum.getX();
        this.maxY = maximum.getY();
        this.maxZ = maximum.getZ();
    }

    public AxisAlignedBox expandTowards(double deltaX, double deltaY, double deltaZ) {
        double expandedMinX = this.minX;
        double expandedMinY = this.minY;
        double expandedMinZ = this.minZ;
        double expandedMaxX = this.maxX;
        double expandedMaxY = this.maxY;
        double expandedMaxZ = this.maxZ;
        if (deltaX < 0.0) {
            expandedMinX += deltaX;
        } else if (deltaX > 0.0) {
            expandedMaxX += deltaX;
        }
        if (deltaY < 0.0) {
            expandedMinY += deltaY;
        } else if (deltaY > 0.0) {
            expandedMaxY += deltaY;
        }
        if (deltaZ < 0.0) {
            expandedMinZ += deltaZ;
        } else if (deltaZ > 0.0) {
            expandedMaxZ += deltaZ;
        }
        return new AxisAlignedBox(expandedMinX, expandedMinY, expandedMinZ, expandedMaxX, expandedMaxY, expandedMaxZ);
    }

    public AxisAlignedBox expand(double amountX, double amountY, double amountZ) {
        double expandedMinX = this.minX - amountX;
        double expandedMinY = this.minY - amountY;
        double expandedMinZ = this.minZ - amountZ;
        double expandedMaxX = this.maxX + amountX;
        double expandedMaxY = this.maxY + amountY;
        double expandedMaxZ = this.maxZ + amountZ;
        return new AxisAlignedBox(expandedMinX, expandedMinY, expandedMinZ, expandedMaxX, expandedMaxY, expandedMaxZ);
    }

    public AxisAlignedBox union(AxisAlignedBox other) {
        double unionMinX = Math.min(this.minX, other.minX);
        double unionMinY = Math.min(this.minY, other.minY);
        double unionMinZ = Math.min(this.minZ, other.minZ);
        double unionMaxX = Math.max(this.maxX, other.maxX);
        double unionMaxY = Math.max(this.maxY, other.maxY);
        double unionMaxZ = Math.max(this.maxZ, other.maxZ);
        return new AxisAlignedBox(unionMinX, unionMinY, unionMinZ, unionMaxX, unionMaxY, unionMaxZ);
    }

    public static AxisAlignedBox of(double firstX, double firstY, double firstZ, double secondX, double secondY, double secondZ) {
        double minimumX = Math.min(firstX, secondX);
        double minimumY = Math.min(firstY, secondY);
        double minimumZ = Math.min(firstZ, secondZ);
        double maximumX = Math.max(firstX, secondX);
        double maximumY = Math.max(firstY, secondY);
        double maximumZ = Math.max(firstZ, secondZ);
        return new AxisAlignedBox(minimumX, minimumY, minimumZ, maximumX, maximumY, maximumZ);
    }

    public AxisAlignedBox offset(double deltaX, double deltaY, double deltaZ) {
        return new AxisAlignedBox(this.minX + deltaX, this.minY + deltaY, this.minZ + deltaZ, this.maxX + deltaX, this.maxY + deltaY, this.maxZ + deltaZ);
    }

    public double calculateXOffset(AxisAlignedBox other, double offset) {
        if (other.maxY <= minY || other.minY >= maxY || other.maxZ <= minZ || other.minZ >= maxZ)
            return offset;
        if (offset > 0.0 && other.maxX <= minX)
            return Math.min(offset, minX - other.maxX);
        if (offset < 0.0 && other.minX >= maxX)
            return Math.max(offset, maxX - other.minX);
        return offset;
    }

    public double calculateYOffset(AxisAlignedBox other, double offset) {
        if (!(other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ)) {
            return offset;
        }
        if (offset > 0.0 && other.maxY <= this.minY) {
            double positiveGap = this.minY - other.maxY;
            if (positiveGap < offset) {
                offset = positiveGap;
            }
        } else if (offset < 0.0 && other.minY >= this.maxY) {
            double negativeGap = this.maxY - other.minY;
            if (negativeGap > offset) {
                offset = negativeGap;
            }
        }
        return offset;
    }

    public double calculateZOffset(AxisAlignedBox other, double offset) {
        if (!(other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY)) {
            return offset;
        }
        if (offset > 0.0 && other.maxZ <= this.minZ) {
            double positiveGap = this.minZ - other.maxZ;
            if (positiveGap < offset) {
                offset = positiveGap;
            }
        } else if (offset < 0.0 && other.minZ >= this.maxZ) {
            double negativeGap = this.maxZ - other.minZ;
            if (negativeGap > offset) {
                offset = negativeGap;
            }
        }
        return offset;
    }

    public boolean intersects(AxisAlignedBox other) {
        return !(other.maxX > this.minX) || !(other.minX < this.maxX) ? false : (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false);
    }

    public boolean contains(Vector3D point) {
        return !(point.x > this.minX) || !(point.x < this.maxX) ? false : (point.y > this.minY && point.y < this.maxY ? point.z > this.minZ && point.z < this.maxZ : false);
    }

    public double averageSideLength() {
        double lengthX = this.maxX - this.minX;
        double lengthY = this.maxY - this.minY;
        double lengthZ = this.maxZ - this.minZ;
        return (lengthX + lengthY + lengthZ) / 3.0;
    }

    public AxisAlignedBox contract(double amountX, double amountY, double amountZ) {
        double contractedMinX = this.minX + amountX;
        double contractedMinY = this.minY + amountY;
        double contractedMinZ = this.minZ + amountZ;
        double contractedMaxX = this.maxX - amountX;
        double contractedMaxY = this.maxY - amountY;
        double contractedMaxZ = this.maxZ - amountZ;
        return new AxisAlignedBox(contractedMinX, contractedMinY, contractedMinZ, contractedMaxX, contractedMaxY, contractedMaxZ);
    }

    public VectorRayHit rayTrace(Vector3D start, Vector3D end) {
        Vector3D[] candidates = { start.interpolateX(end, minX), start.interpolateX(end, maxX), start.interpolateY(end, minY), start.interpolateY(end, maxY), start.interpolateZ(end, minZ), start.interpolateZ(end, maxZ) };
        Direction[] faces = { Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH };
        Vector3D closest = null;
        Direction face = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (int index = 0; index < candidates.length; index++) {
            Vector3D point = candidates[index];
            if (point == null || point.x < minX || point.x > maxX || point.y < minY || point.y > maxY || point.z < minZ || point.z > maxZ)
                continue;
            double distance = start.distanceSquared(point);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = point;
                face = faces[index];
            }
        }
        return closest == null ? null : new VectorRayHit(closest, face);
    }

    private boolean isWithinYZ(Vector3D point) {
        return point == null ? false : point.y >= this.minY && point.y <= this.maxY && point.z >= this.minZ && point.z <= this.maxZ;
    }

    private boolean isWithinXZ(Vector3D point) {
        return point == null ? false : point.x >= this.minX && point.x <= this.maxX && point.z >= this.minZ && point.z <= this.maxZ;
    }

    private boolean isWithinXY(Vector3D point) {
        return point == null ? false : point.x >= this.minX && point.x <= this.maxX && point.y >= this.minY && point.y <= this.maxY;
    }

    @Override
    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }
}
