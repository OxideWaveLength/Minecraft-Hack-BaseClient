package net.minecraft.village;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class VillageDoorInfo
{
    /** a block representing the door. Could be either upper or lower part */
    private final BlockPos doorBlockPos;
    private final BlockPos insideBlock;

    /** the inside direction is where can see less sky */
    private final EnumFacing insideDirection;
    private int lastActivityTimestamp;
    private boolean isDetachedFromVillageFlag;
    private int doorOpeningRestrictionCounter;

    public VillageDoorInfo(BlockPos p_i45871_1_, int p_i45871_2_, int p_i45871_3_, int p_i45871_4_)
    {
        this(p_i45871_1_, getFaceDirection(p_i45871_2_, p_i45871_3_), p_i45871_4_);
    }

    private static EnumFacing getFaceDirection(int deltaX, int deltaZ)
    {
        return deltaX < 0 ? EnumFacing.WEST : (deltaX > 0 ? EnumFacing.EAST : (deltaZ < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH));
    }

    public VillageDoorInfo(BlockPos p_i45872_1_, EnumFacing p_i45872_2_, int p_i45872_3_)
    {
        this.doorBlockPos = p_i45872_1_;
        this.insideDirection = p_i45872_2_;
        this.insideBlock = p_i45872_1_.offset(p_i45872_2_, 2);
        this.lastActivityTimestamp = p_i45872_3_;
    }

    /**
     * Returns the squared distance between this door and the given coordinate.
     */
    public int getDistanceSquared(int p_75474_1_, int p_75474_2_, int p_75474_3_)
    {
        return (int)this.doorBlockPos.distanceSq((double)p_75474_1_, (double)p_75474_2_, (double)p_75474_3_);
    }

    public int getDistanceToDoorBlockSq(BlockPos p_179848_1_)
    {
        return (int)p_179848_1_.distanceSq(this.getDoorBlockPos());
    }

    public int getDistanceToInsideBlockSq(BlockPos p_179846_1_)
    {
        return (int)this.insideBlock.distanceSq(p_179846_1_);
    }

    public boolean func_179850_c(BlockPos p_179850_1_)
    {
        int i = p_179850_1_.getX() - this.doorBlockPos.getX();
        int j = p_179850_1_.getZ() - this.doorBlockPos.getY();
        return i * this.insideDirection.getFrontOffsetX() + j * this.insideDirection.getFrontOffsetZ() >= 0;
    }

    public void resetDoorOpeningRestrictionCounter()
    {
        this.doorOpeningRestrictionCounter = 0;
    }

    public void incrementDoorOpeningRestrictionCounter()
    {
        ++this.doorOpeningRestrictionCounter;
    }

    public int getDoorOpeningRestrictionCounter()
    {
        return this.doorOpeningRestrictionCounter;
    }

    public BlockPos getDoorBlockPos()
    {
        return this.doorBlockPos;
    }

    public BlockPos getInsideBlockPos()
    {
        return this.insideBlock;
    }

    public int getInsideOffsetX()
    {
        return this.insideDirection.getFrontOffsetX() * 2;
    }

    public int getInsideOffsetZ()
    {
        return this.insideDirection.getFrontOffsetZ() * 2;
    }

    public int getInsidePosY()
    {
        return this.lastActivityTimestamp;
    }

    public void func_179849_a(int p_179849_1_)
    {
        this.lastActivityTimestamp = p_179849_1_;
    }

    public boolean getIsDetachedFromVillageFlag()
    {
        return this.isDetachedFromVillageFlag;
    }

    public void setIsDetachedFromVillageFlag(boolean p_179853_1_)
    {
        this.isDetachedFromVillageFlag = p_179853_1_;
    }
}
