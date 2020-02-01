package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIMoveToBlock extends EntityAIBase
{
    private final EntityCreature theEntity;
    private final double movementSpeed;

    /** Controls task execution delay */
    protected int runDelay;
    private int timeoutCounter;
    private int field_179490_f;

    /** Block to move to */
    protected BlockPos destinationBlock = BlockPos.ORIGIN;
    private boolean isAboveDestination;
    private int searchLength;

    public EntityAIMoveToBlock(EntityCreature creature, double speedIn, int length)
    {
        this.theEntity = creature;
        this.movementSpeed = speedIn;
        this.searchLength = length;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.runDelay > 0)
        {
            --this.runDelay;
            return false;
        }
        else
        {
            this.runDelay = 200 + this.theEntity.getRNG().nextInt(200);
            return this.searchForDestination();
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.timeoutCounter >= -this.field_179490_f && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.theEntity.worldObj, this.destinationBlock);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.theEntity.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
        this.timeoutCounter = 0;
        this.field_179490_f = this.theEntity.getRNG().nextInt(this.theEntity.getRNG().nextInt(1200) + 1200) + 1200;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.theEntity.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0D)
        {
            this.isAboveDestination = false;
            ++this.timeoutCounter;

            if (this.timeoutCounter % 40 == 0)
            {
                this.theEntity.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
            }
        }
        else
        {
            this.isAboveDestination = true;
            --this.timeoutCounter;
        }
    }

    protected boolean getIsAboveDestination()
    {
        return this.isAboveDestination;
    }

    /**
     * Searches and sets new destination block and returns true if a suitable block (specified in {@link
     * net.minecraft.entity.ai.EntityAIMoveToBlock#shouldMoveTo(World, BlockPos) EntityAIMoveToBlock#shouldMoveTo(World,
     * BlockPos)}) can be found.
     */
    private boolean searchForDestination()
    {
        int i = this.searchLength;
        int j = 1;
        BlockPos blockpos = new BlockPos(this.theEntity);

        for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k)
        {
            for (int l = 0; l < i; ++l)
            {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1)
                {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1)
                    {
                        BlockPos blockpos1 = blockpos.add(i1, k - 1, j1);

                        if (this.theEntity.isWithinHomeDistanceFromPosition(blockpos1) && this.shouldMoveTo(this.theEntity.worldObj, blockpos1))
                        {
                            this.destinationBlock = blockpos1;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Return true to set given position as destination
     */
    protected abstract boolean shouldMoveTo(World worldIn, BlockPos pos);
}
