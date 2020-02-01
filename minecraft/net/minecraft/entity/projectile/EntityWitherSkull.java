package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball
{
    public EntityWitherSkull(World worldIn)
    {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityWitherSkull(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
    {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor()
    {
        return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
    }

    public EntityWitherSkull(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }

    /**
     * Explosion resistance of a block relative to this entity
     */
    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn)
    {
        float f = super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn);
        Block block = blockStateIn.getBlock();

        if (this.isInvulnerable() && EntityWither.func_181033_a(block))
        {
            f = Math.min(0.8F, f);
        }

        return f;
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition movingObject)
    {
        if (!this.worldObj.isRemote)
        {
            if (movingObject.entityHit != null)
            {
                if (this.shootingEntity != null)
                {
                    if (movingObject.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F))
                    {
                        if (!movingObject.entityHit.isEntityAlive())
                        {
                            this.shootingEntity.heal(5.0F);
                        }
                        else
                        {
                            this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
                        }
                    }
                }
                else
                {
                    movingObject.entityHit.attackEntityFrom(DamageSource.magic, 5.0F);
                }

                if (movingObject.entityHit instanceof EntityLivingBase)
                {
                    int i = 0;

                    if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL)
                    {
                        i = 10;
                    }
                    else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD)
                    {
                        i = 40;
                    }

                    if (i > 0)
                    {
                        ((EntityLivingBase)movingObject.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * i, 1));
                    }
                }
            }

            this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
            this.setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(10, Byte.valueOf((byte)0));
    }

    /**
     * Return whether this skull comes from an invulnerable (aura) wither boss.
     */
    public boolean isInvulnerable()
    {
        return this.dataWatcher.getWatchableObjectByte(10) == 1;
    }

    /**
     * Set whether this skull comes from an invulnerable (aura) wither boss.
     */
    public void setInvulnerable(boolean invulnerable)
    {
        this.dataWatcher.updateObject(10, Byte.valueOf((byte)(invulnerable ? 1 : 0)));
    }
}
