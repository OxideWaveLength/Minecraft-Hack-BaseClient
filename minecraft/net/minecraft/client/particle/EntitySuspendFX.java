package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntitySuspendFX extends EntityFX
{
    protected EntitySuspendFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn - 0.125D, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.particleRed = 0.4F;
        this.particleGreen = 0.4F;
        this.particleBlue = 0.7F;
        this.setParticleTextureIndex(0);
        this.setSize(0.01F, 0.01F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = xSpeedIn * 0.0D;
        this.motionY = ySpeedIn * 0.0D;
        this.motionZ = zSpeedIn * 0.0D;
        this.particleMaxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() != Material.water)
        {
            this.setDead();
        }

        if (this.particleMaxAge-- <= 0)
        {
            this.setDead();
        }
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntitySuspendFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
