package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract
{
    /** If the entity close the door */
    boolean closeDoor;

    /**
     * The temporisation before the entity close the door (in ticks, always 20 = 1 second)
     */
    int closeDoorTemporisation;

    public EntityAIOpenDoor(EntityLiving entitylivingIn, boolean shouldClose)
    {
        super(entitylivingIn);
        this.theEntity = entitylivingIn;
        this.closeDoor = shouldClose;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.closeDoor && this.closeDoorTemporisation > 0 && super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.closeDoorTemporisation = 20;
        this.doorBlock.toggleDoor(this.theEntity.worldObj, this.doorPosition, true);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        if (this.closeDoor)
        {
            this.doorBlock.toggleDoor(this.theEntity.worldObj, this.doorPosition, false);
        }
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --this.closeDoorTemporisation;
        super.updateTask();
    }
}
