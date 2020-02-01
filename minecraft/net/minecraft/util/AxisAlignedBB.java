package net.minecraft.util;

public class AxisAlignedBB
{
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AxisAlignedBB(BlockPos pos1, BlockPos pos2)
    {
        this.minX = (double)pos1.getX();
        this.minY = (double)pos1.getY();
        this.minZ = (double)pos1.getZ();
        this.maxX = (double)pos2.getX();
        this.maxY = (double)pos2.getY();
        this.maxZ = (double)pos2.getZ();
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    public AxisAlignedBB addCoord(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB expand(double x, double y, double z)
    {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public AxisAlignedBB union(AxisAlignedBB other)
    {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * returns an AABB with corners x1, y1, z1 and x2, y2, z2
     */
    public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double d0 = Math.min(x1, x2);
        double d1 = Math.min(y1, y2);
        double d2 = Math.min(z1, z2);
        double d3 = Math.max(x1, x2);
        double d4 = Math.max(y1, y2);
        double d5 = Math.max(z1, z2);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    public AxisAlignedBB offset(double x, double y, double z)
    {
        return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(AxisAlignedBB other, double offsetX)
    {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetX > 0.0D && other.maxX <= this.minX)
            {
                double d1 = this.minX - other.maxX;

                if (d1 < offsetX)
                {
                    offsetX = d1;
                }
            }
            else if (offsetX < 0.0D && other.minX >= this.maxX)
            {
                double d0 = this.maxX - other.minX;

                if (d0 > offsetX)
                {
                    offsetX = d0;
                }
            }

            return offsetX;
        }
        else
        {
            return offsetX;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(AxisAlignedBB other, double offsetY)
    {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetY > 0.0D && other.maxY <= this.minY)
            {
                double d1 = this.minY - other.maxY;

                if (d1 < offsetY)
                {
                    offsetY = d1;
                }
            }
            else if (offsetY < 0.0D && other.minY >= this.maxY)
            {
                double d0 = this.maxY - other.minY;

                if (d0 > offsetY)
                {
                    offsetY = d0;
                }
            }

            return offsetY;
        }
        else
        {
            return offsetY;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(AxisAlignedBB other, double offsetZ)
    {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY)
        {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ)
            {
                double d1 = this.minZ - other.maxZ;

                if (d1 < offsetZ)
                {
                    offsetZ = d1;
                }
            }
            else if (offsetZ < 0.0D && other.minZ >= this.maxZ)
            {
                double d0 = this.maxZ - other.minZ;

                if (d0 > offsetZ)
                {
                    offsetZ = d0;
                }
            }

            return offsetZ;
        }
        else
        {
            return offsetZ;
        }
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(AxisAlignedBB other)
    {
        return other.maxX > this.minX && other.minX < this.maxX ? (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false) : false;
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean isVecInside(Vec3 vec)
    {
        return vec.xCoord > this.minX && vec.xCoord < this.maxX ? (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false) : false;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getAverageEdgeLength()
    {
        double d0 = this.maxX - this.minX;
        double d1 = this.maxY - this.minY;
        double d2 = this.maxZ - this.minZ;
        return (d0 + d1 + d2) / 3.0D;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    public AxisAlignedBB contract(double x, double y, double z)
    {
        double d0 = this.minX + x;
        double d1 = this.minY + y;
        double d2 = this.minZ + z;
        double d3 = this.maxX - x;
        double d4 = this.maxY - y;
        double d5 = this.maxZ - z;
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public MovingObjectPosition calculateIntercept(Vec3 vecA, Vec3 vecB)
    {
        Vec3 vec3 = vecA.getIntermediateWithXValue(vecB, this.minX);
        Vec3 vec31 = vecA.getIntermediateWithXValue(vecB, this.maxX);
        Vec3 vec32 = vecA.getIntermediateWithYValue(vecB, this.minY);
        Vec3 vec33 = vecA.getIntermediateWithYValue(vecB, this.maxY);
        Vec3 vec34 = vecA.getIntermediateWithZValue(vecB, this.minZ);
        Vec3 vec35 = vecA.getIntermediateWithZValue(vecB, this.maxZ);

        if (!this.isVecInYZ(vec3))
        {
            vec3 = null;
        }

        if (!this.isVecInYZ(vec31))
        {
            vec31 = null;
        }

        if (!this.isVecInXZ(vec32))
        {
            vec32 = null;
        }

        if (!this.isVecInXZ(vec33))
        {
            vec33 = null;
        }

        if (!this.isVecInXY(vec34))
        {
            vec34 = null;
        }

        if (!this.isVecInXY(vec35))
        {
            vec35 = null;
        }

        Vec3 vec36 = null;

        if (vec3 != null)
        {
            vec36 = vec3;
        }

        if (vec31 != null && (vec36 == null || vecA.squareDistanceTo(vec31) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec31;
        }

        if (vec32 != null && (vec36 == null || vecA.squareDistanceTo(vec32) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec32;
        }

        if (vec33 != null && (vec36 == null || vecA.squareDistanceTo(vec33) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec33;
        }

        if (vec34 != null && (vec36 == null || vecA.squareDistanceTo(vec34) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec34;
        }

        if (vec35 != null && (vec36 == null || vecA.squareDistanceTo(vec35) < vecA.squareDistanceTo(vec36)))
        {
            vec36 = vec35;
        }

        if (vec36 == null)
        {
            return null;
        }
        else
        {
            EnumFacing enumfacing = null;

            if (vec36 == vec3)
            {
                enumfacing = EnumFacing.WEST;
            }
            else if (vec36 == vec31)
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (vec36 == vec32)
            {
                enumfacing = EnumFacing.DOWN;
            }
            else if (vec36 == vec33)
            {
                enumfacing = EnumFacing.UP;
            }
            else if (vec36 == vec34)
            {
                enumfacing = EnumFacing.NORTH;
            }
            else
            {
                enumfacing = EnumFacing.SOUTH;
            }

            return new MovingObjectPosition(vec36, enumfacing);
        }
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInYZ(Vec3 vec)
    {
        return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXZ(Vec3 vec)
    {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XY dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXY(Vec3 vec)
    {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public boolean func_181656_b()
    {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }
}
