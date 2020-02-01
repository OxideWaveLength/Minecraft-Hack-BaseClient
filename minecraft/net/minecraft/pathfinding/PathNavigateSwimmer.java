package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate
{
    public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn)
    {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder()
    {
        return new PathFinder(new SwimNodeProcessor());
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate()
    {
        return this.isInLiquid();
    }

    protected Vec3 getEntityPosition()
    {
        return new Vec3(this.theEntity.posX, this.theEntity.posY + (double)this.theEntity.height * 0.5D, this.theEntity.posZ);
    }

    protected void pathFollow()
    {
        Vec3 vec3 = this.getEntityPosition();
        float f = this.theEntity.width * this.theEntity.width;
        int i = 6;

        if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double)f)
        {
            this.currentPath.incrementPathIndex();
        }

        for (int j = Math.min(this.currentPath.getCurrentPathIndex() + i, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j)
        {
            Vec3 vec31 = this.currentPath.getVectorFromIndex(this.theEntity, j);

            if (vec31.squareDistanceTo(vec3) <= 36.0D && this.isDirectPathBetweenPoints(vec3, vec31, 0, 0, 0))
            {
                this.currentPath.setCurrentPathIndex(j);
                break;
            }
        }

        this.checkForStuck(vec3);
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath()
    {
        super.removeSunnyPath();
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ)
    {
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + (double)this.theEntity.height * 0.5D, posVec32.zCoord), false, true, false);
        return movingobjectposition == null || movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
    }
}
