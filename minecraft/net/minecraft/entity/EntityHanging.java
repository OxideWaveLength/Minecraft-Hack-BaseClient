package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity
{
    private int tickCounter1;
    protected BlockPos hangingPosition;

    /** The direction the entity is facing */
    public EnumFacing facingDirection;

    public EntityHanging(World worldIn)
    {
        super(worldIn);
        this.setSize(0.5F, 0.5F);
    }

    public EntityHanging(World worldIn, BlockPos hangingPositionIn)
    {
        this(worldIn);
        this.hangingPosition = hangingPositionIn;
    }

    protected void entityInit()
    {
    }

    /**
     * Updates facing and bounding box based on it
     */
    protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn)
    {
        Validate.notNull(facingDirectionIn);
        Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
        this.facingDirection = facingDirectionIn;
        this.prevRotationYaw = this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
        this.updateBoundingBox();
    }

    /**
     * Updates the entity bounding box based on current facing
     */
    private void updateBoundingBox()
    {
        if (this.facingDirection != null)
        {
            double d0 = (double)this.hangingPosition.getX() + 0.5D;
            double d1 = (double)this.hangingPosition.getY() + 0.5D;
            double d2 = (double)this.hangingPosition.getZ() + 0.5D;
            double d3 = 0.46875D;
            double d4 = this.func_174858_a(this.getWidthPixels());
            double d5 = this.func_174858_a(this.getHeightPixels());
            d0 = d0 - (double)this.facingDirection.getFrontOffsetX() * 0.46875D;
            d2 = d2 - (double)this.facingDirection.getFrontOffsetZ() * 0.46875D;
            d1 = d1 + d5;
            EnumFacing enumfacing = this.facingDirection.rotateYCCW();
            d0 = d0 + d4 * (double)enumfacing.getFrontOffsetX();
            d2 = d2 + d4 * (double)enumfacing.getFrontOffsetZ();
            this.posX = d0;
            this.posY = d1;
            this.posZ = d2;
            double d6 = (double)this.getWidthPixels();
            double d7 = (double)this.getHeightPixels();
            double d8 = (double)this.getWidthPixels();

            if (this.facingDirection.getAxis() == EnumFacing.Axis.Z)
            {
                d8 = 1.0D;
            }
            else
            {
                d6 = 1.0D;
            }

            d6 = d6 / 32.0D;
            d7 = d7 / 32.0D;
            d8 = d8 / 32.0D;
            this.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
        }
    }

    private double func_174858_a(int p_174858_1_)
    {
        return p_174858_1_ % 32 == 0 ? 0.5D : 0.0D;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.tickCounter1++ == 100 && !this.worldObj.isRemote)
        {
            this.tickCounter1 = 0;

            if (!this.isDead && !this.onValidSurface())
            {
                this.setDead();
                this.onBroken((Entity)null);
            }
        }
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty())
        {
            return false;
        }
        else
        {
            int i = Math.max(1, this.getWidthPixels() / 16);
            int j = Math.max(1, this.getHeightPixels() / 16);
            BlockPos blockpos = this.hangingPosition.offset(this.facingDirection.getOpposite());
            EnumFacing enumfacing = this.facingDirection.rotateYCCW();

            for (int k = 0; k < i; ++k)
            {
                for (int l = 0; l < j; ++l)
                {
                    BlockPos blockpos1 = blockpos.offset(enumfacing, k).up(l);
                    Block block = this.worldObj.getBlockState(blockpos1).getBlock();

                    if (!block.getMaterial().isSolid() && !BlockRedstoneDiode.isRedstoneRepeaterBlockID(block))
                    {
                        return false;
                    }
                }
            }

            for (Entity entity : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()))
            {
                if (entity instanceof EntityHanging)
                {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(Entity entityIn)
    {
        return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
    }

    public EnumFacing getHorizontalFacing()
    {
        return this.facingDirection;
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
        else
        {
            if (!this.isDead && !this.worldObj.isRemote)
            {
                this.setDead();
                this.setBeenAttacked();
                this.onBroken(source.getEntity());
            }

            return true;
        }
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double x, double y, double z)
    {
        if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D)
        {
            this.setDead();
            this.onBroken((Entity)null);
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double x, double y, double z)
    {
        if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D)
        {
            this.setDead();
            this.onBroken((Entity)null);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
        tagCompound.setInteger("TileX", this.getHangingPosition().getX());
        tagCompound.setInteger("TileY", this.getHangingPosition().getY());
        tagCompound.setInteger("TileZ", this.getHangingPosition().getZ());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.hangingPosition = new BlockPos(tagCompund.getInteger("TileX"), tagCompund.getInteger("TileY"), tagCompund.getInteger("TileZ"));
        EnumFacing enumfacing;

        if (tagCompund.hasKey("Direction", 99))
        {
            enumfacing = EnumFacing.getHorizontal(tagCompund.getByte("Direction"));
            this.hangingPosition = this.hangingPosition.offset(enumfacing);
        }
        else if (tagCompund.hasKey("Facing", 99))
        {
            enumfacing = EnumFacing.getHorizontal(tagCompund.getByte("Facing"));
        }
        else
        {
            enumfacing = EnumFacing.getHorizontal(tagCompund.getByte("Dir"));
        }

        this.updateFacingWithBoundingBox(enumfacing);
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public abstract void onBroken(Entity brokenEntity);

    protected boolean shouldSetPosAfterLoading()
    {
        return false;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        BlockPos blockpos = this.hangingPosition;
        this.hangingPosition = new BlockPos(x, y, z);

        if (!this.hangingPosition.equals(blockpos))
        {
            this.updateBoundingBox();
            this.isAirBorne = true;
        }
    }

    public BlockPos getHangingPosition()
    {
        return this.hangingPosition;
    }
}
