package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator
{
    /**
     * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
     * MULTIPLE findTowards and findAway calls, will share this var
     */
    private static Vec3 staticVector = new Vec3(0.0D, 0.0D, 0.0D);

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    public static Vec3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y)
    {
        return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3)null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
     */
    public static Vec3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3)
    {
        staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
     */
    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3)
    {
        staticVector = (new Vec3(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVec3);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    private static Vec3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3)
    {
        Random random = entitycreatureIn.getRNG();
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreatureIn.hasHome())
        {
            double d0 = entitycreatureIn.getHomePosition().distanceSq((double)MathHelper.floor_double(entitycreatureIn.posX), (double)MathHelper.floor_double(entitycreatureIn.posY), (double)MathHelper.floor_double(entitycreatureIn.posZ)) + 4.0D;
            double d1 = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz);
            flag1 = d0 < d1 * d1;
        }
        else
        {
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1)
        {
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;

            if (targetVec3 == null || (double)l * targetVec3.xCoord + (double)i1 * targetVec3.zCoord >= 0.0D)
            {
                if (entitycreatureIn.hasHome() && xz > 1)
                {
                    BlockPos blockpos = entitycreatureIn.getHomePosition();

                    if (entitycreatureIn.posX > (double)blockpos.getX())
                    {
                        l -= random.nextInt(xz / 2);
                    }
                    else
                    {
                        l += random.nextInt(xz / 2);
                    }

                    if (entitycreatureIn.posZ > (double)blockpos.getZ())
                    {
                        i1 -= random.nextInt(xz / 2);
                    }
                    else
                    {
                        i1 += random.nextInt(xz / 2);
                    }
                }

                l = l + MathHelper.floor_double(entitycreatureIn.posX);
                k1 = k1 + MathHelper.floor_double(entitycreatureIn.posY);
                i1 = i1 + MathHelper.floor_double(entitycreatureIn.posZ);
                BlockPos blockpos1 = new BlockPos(l, k1, i1);

                if (!flag1 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockpos1))
                {
                    float f1 = entitycreatureIn.getBlockPathWeight(blockpos1);

                    if (f1 > f)
                    {
                        f = f1;
                        i = l;
                        j = k1;
                        k = i1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return new Vec3((double)i, (double)j, (double)k);
        }
        else
        {
            return null;
        }
    }
}
