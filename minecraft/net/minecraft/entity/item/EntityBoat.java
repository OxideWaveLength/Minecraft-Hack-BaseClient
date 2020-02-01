package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity
{
    /** true if no player in boat */
    private boolean isBoatEmpty;
    private double speedMultiplier;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    public EntityBoat(World worldIn)
    {
        super(worldIn);
        this.isBoatEmpty = true;
        this.speedMultiplier = 0.07D;
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 0.6F);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Float(0.0F));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.getEntityBoundingBox();
    }

    /**
     * Returns the collision bounding box for this entity
     */
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityBoat(World worldIn, double p_i1705_2_, double p_i1705_4_, double p_i1705_6_)
    {
        this(worldIn);
        this.setPosition(p_i1705_2_, p_i1705_4_, p_i1705_6_);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = p_i1705_2_;
        this.prevPosY = p_i1705_4_;
        this.prevPosZ = p_i1705_6_;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return -0.3D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!this.worldObj.isRemote && !this.isDead)
        {
            if (this.riddenByEntity != null && this.riddenByEntity == source.getEntity() && source instanceof EntityDamageSourceIndirect)
            {
                return false;
            }
            else
            {
                this.setForwardDirection(-this.getForwardDirection());
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                this.setBeenAttacked();
                boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;

                if (flag || this.getDamageTaken() > 40.0F)
                {
                    if (this.riddenByEntity != null)
                    {
                        this.riddenByEntity.mountEntity(this);
                    }

                    if (!flag && this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        this.dropItemWithOffset(Items.boat, 1, 0.0F);
                    }

                    this.setDead();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        if (p_180426_10_ && this.riddenByEntity != null)
        {
            this.prevPosX = this.posX = x;
            this.prevPosY = this.posY = y;
            this.prevPosZ = this.posZ = z;
            this.rotationYaw = yaw;
            this.rotationPitch = pitch;
            this.boatPosRotationIncrements = 0;
            this.setPosition(x, y, z);
            this.motionX = this.velocityX = 0.0D;
            this.motionY = this.velocityY = 0.0D;
            this.motionZ = this.velocityZ = 0.0D;
        }
        else
        {
            if (this.isBoatEmpty)
            {
                this.boatPosRotationIncrements = posRotationIncrements + 5;
            }
            else
            {
                double d0 = x - this.posX;
                double d1 = y - this.posY;
                double d2 = z - this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 <= 1.0D)
                {
                    return;
                }

                this.boatPosRotationIncrements = 3;
            }

            this.boatX = x;
            this.boatY = y;
            this.boatZ = z;
            this.boatYaw = (double)yaw;
            this.boatPitch = (double)pitch;
            this.motionX = this.velocityX;
            this.motionY = this.velocityY;
            this.motionZ = this.velocityZ;
        }
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double x, double y, double z)
    {
        this.velocityX = this.motionX = x;
        this.velocityY = this.motionY = y;
        this.velocityZ = this.motionZ = z;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int i = 5;
        double d0 = 0.0D;

        for (int j = 0; j < i; ++j)
        {
            double d1 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(j + 0) / (double)i - 0.125D;
            double d3 = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(j + 1) / (double)i - 0.125D;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.getEntityBoundingBox().minX, d1, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, d3, this.getEntityBoundingBox().maxZ);

            if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water))
            {
                d0 += 1.0D / (double)i;
            }
        }

        double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (d9 > 0.2975D)
        {
            double d2 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
            double d4 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

            for (int k = 0; (double)k < 1.0D + d9 * 60.0D; ++k)
            {
                double d5 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
                double d6 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;

                if (this.rand.nextBoolean())
                {
                    double d7 = this.posX - d2 * d5 * 0.8D + d4 * d6;
                    double d8 = this.posZ - d4 * d5 * 0.8D - d2 * d6;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d7, this.posY - 0.125D, d8, this.motionX, this.motionY, this.motionZ, new int[0]);
                }
                else
                {
                    double d24 = this.posX + d2 + d4 * d5 * 0.7D;
                    double d25 = this.posZ + d4 - d2 * d5 * 0.7D;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d24, this.posY - 0.125D, d25, this.motionX, this.motionY, this.motionZ, new int[0]);
                }
            }
        }

        if (this.worldObj.isRemote && this.isBoatEmpty)
        {
            if (this.boatPosRotationIncrements > 0)
            {
                double d12 = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
                double d16 = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
                double d19 = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
                double d22 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d22 / (double)this.boatPosRotationIncrements);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(d12, d16, d19);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                double d13 = this.posX + this.motionX;
                double d17 = this.posY + this.motionY;
                double d20 = this.posZ + this.motionZ;
                this.setPosition(d13, d17, d20);

                if (this.onGround)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        }
        else
        {
            if (d0 < 1.0D)
            {
                double d10 = d0 * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * d10;
            }
            else
            {
                if (this.motionY < 0.0D)
                {
                    this.motionY /= 2.0D;
                }

                this.motionY += 0.007000000216066837D;
            }

            if (this.riddenByEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)this.riddenByEntity;
                float f = this.riddenByEntity.rotationYaw + -entitylivingbase.moveStrafing * 90.0F;
                this.motionX += -Math.sin((double)(f * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)entitylivingbase.moveForward * 0.05000000074505806D;
                this.motionZ += Math.cos((double)(f * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)entitylivingbase.moveForward * 0.05000000074505806D;
            }

            double d11 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d11 > 0.35D)
            {
                double d14 = 0.35D / d11;
                this.motionX *= d14;
                this.motionZ *= d14;
                d11 = 0.35D;
            }

            if (d11 > d9 && this.speedMultiplier < 0.35D)
            {
                this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

                if (this.speedMultiplier > 0.35D)
                {
                    this.speedMultiplier = 0.35D;
                }
            }
            else
            {
                this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

                if (this.speedMultiplier < 0.07D)
                {
                    this.speedMultiplier = 0.07D;
                }
            }

            for (int i1 = 0; i1 < 4; ++i1)
            {
                int l1 = MathHelper.floor_double(this.posX + ((double)(i1 % 2) - 0.5D) * 0.8D);
                int i2 = MathHelper.floor_double(this.posZ + ((double)(i1 / 2) - 0.5D) * 0.8D);

                for (int j2 = 0; j2 < 2; ++j2)
                {
                    int l = MathHelper.floor_double(this.posY) + j2;
                    BlockPos blockpos = new BlockPos(l1, l, i2);
                    Block block = this.worldObj.getBlockState(blockpos).getBlock();

                    if (block == Blocks.snow_layer)
                    {
                        this.worldObj.setBlockToAir(blockpos);
                        this.isCollidedHorizontally = false;
                    }
                    else if (block == Blocks.waterlily)
                    {
                        this.worldObj.destroyBlock(blockpos, true);
                        this.isCollidedHorizontally = false;
                    }
                }
            }

            if (this.onGround)
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && d9 > 0.2975D)
            {
                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();

                    if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        for (int j1 = 0; j1 < 3; ++j1)
                        {
                            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                        }

                        for (int k1 = 0; k1 < 2; ++k1)
                        {
                            this.dropItemWithOffset(Items.stick, 1, 0.0F);
                        }
                    }
                }
            }
            else
            {
                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }

            this.rotationPitch = 0.0F;
            double d15 = (double)this.rotationYaw;
            double d18 = this.prevPosX - this.posX;
            double d21 = this.prevPosZ - this.posZ;

            if (d18 * d18 + d21 * d21 > 0.001D)
            {
                d15 = (double)((float)(MathHelper.func_181159_b(d21, d18) * 180.0D / Math.PI));
            }

            double d23 = MathHelper.wrapAngleTo180_double(d15 - (double)this.rotationYaw);

            if (d23 > 20.0D)
            {
                d23 = 20.0D;
            }

            if (d23 < -20.0D)
            {
                d23 = -20.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + d23);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (!this.worldObj.isRemote)
            {
                List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (list != null && !list.isEmpty())
                {
                    for (int k2 = 0; k2 < list.size(); ++k2)
                    {
                        Entity entity = (Entity)list.get(k2);

                        if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityBoat)
                        {
                            entity.applyEntityCollision(this);
                        }
                    }
                }

                if (this.riddenByEntity != null && this.riddenByEntity.isDead)
                {
                    this.riddenByEntity = null;
                }
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer playerIn)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn)
        {
            return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                playerIn.mountEntity(this);
            }

            return true;
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos)
    {
        if (onGroundIn)
        {
            if (this.fallDistance > 3.0F)
            {
                this.fall(this.fallDistance, 1.0F);

                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();

                    if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        for (int i = 0; i < 3; ++i)
                        {
                            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                        }

                        for (int j = 0; j < 2; ++j)
                        {
                            this.dropItemWithOffset(Items.stick, 1, 0.0F);
                        }
                    }
                }

                this.fallDistance = 0.0F;
            }
        }
        else if (this.worldObj.getBlockState((new BlockPos(this)).down()).getBlock().getMaterial() != Material.water && y < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - y);
        }
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float p_70266_1_)
    {
        this.dataWatcher.updateObject(19, Float.valueOf(p_70266_1_));
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken()
    {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int p_70265_1_)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(p_70265_1_));
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int p_70269_1_)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(p_70269_1_));
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * true if no player in boat
     */
    public void setIsBoatEmpty(boolean p_70270_1_)
    {
        this.isBoatEmpty = p_70270_1_;
    }
}
