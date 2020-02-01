package net.minecraft.world.pathfinder;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor
{
    protected IBlockAccess blockaccess;
    protected IntHashMap<PathPoint> pointMap = new IntHashMap();
    protected int entitySizeX;
    protected int entitySizeY;
    protected int entitySizeZ;

    public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn)
    {
        this.blockaccess = iblockaccessIn;
        this.pointMap.clearMap();
        this.entitySizeX = MathHelper.floor_float(entityIn.width + 1.0F);
        this.entitySizeY = MathHelper.floor_float(entityIn.height + 1.0F);
        this.entitySizeZ = MathHelper.floor_float(entityIn.width + 1.0F);
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     *  {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
     * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
     */
    public void postProcess()
    {
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    protected PathPoint openPoint(int x, int y, int z)
    {
        int i = PathPoint.makeHash(x, y, z);
        PathPoint pathpoint = (PathPoint)this.pointMap.lookup(i);

        if (pathpoint == null)
        {
            pathpoint = new PathPoint(x, y, z);
            this.pointMap.addKey(i, pathpoint);
        }

        return pathpoint;
    }

    /**
     * Returns given entity's position as PathPoint
     */
    public abstract PathPoint getPathPointTo(Entity entityIn);

    /**
     * Returns PathPoint for given coordinates
     */
    public abstract PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target);

    public abstract int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance);
}
