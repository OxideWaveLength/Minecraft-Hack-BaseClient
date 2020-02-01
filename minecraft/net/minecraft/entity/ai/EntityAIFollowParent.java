package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase
{
    /** The child that is following its parent. */
    EntityAnimal childAnimal;
    EntityAnimal parentAnimal;
    double moveSpeed;
    private int delayCounter;

    public EntityAIFollowParent(EntityAnimal animal, double speed)
    {
        this.childAnimal = animal;
        this.moveSpeed = speed;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.childAnimal.getGrowingAge() >= 0)
        {
            return false;
        }
        else
        {
            List<EntityAnimal> list = this.childAnimal.worldObj.<EntityAnimal>getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getEntityBoundingBox().expand(8.0D, 4.0D, 8.0D));
            EntityAnimal entityanimal = null;
            double d0 = Double.MAX_VALUE;

            for (EntityAnimal entityanimal1 : list)
            {
                if (entityanimal1.getGrowingAge() >= 0)
                {
                    double d1 = this.childAnimal.getDistanceSqToEntity(entityanimal1);

                    if (d1 <= d0)
                    {
                        d0 = d1;
                        entityanimal = entityanimal1;
                    }
                }
            }

            if (entityanimal == null)
            {
                return false;
            }
            else if (d0 < 9.0D)
            {
                return false;
            }
            else
            {
                this.parentAnimal = entityanimal;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.childAnimal.getGrowingAge() >= 0)
        {
            return false;
        }
        else if (!this.parentAnimal.isEntityAlive())
        {
            return false;
        }
        else
        {
            double d0 = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.delayCounter = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.parentAnimal = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (--this.delayCounter <= 0)
        {
            this.delayCounter = 10;
            this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
        }
    }
}
