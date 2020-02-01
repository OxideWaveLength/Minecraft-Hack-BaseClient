package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging
{
    /** Chance for this item frame's item to drop from the frame. */
    private float itemDropChance = 1.0F;

    public EntityItemFrame(World worldIn)
    {
        super(worldIn);
    }

    public EntityItemFrame(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_)
    {
        super(worldIn, p_i45852_2_);
        this.updateFacingWithBoundingBox(p_i45852_3_);
    }

    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(8, 5);
        this.getDataWatcher().addObject(9, Byte.valueOf((byte)0));
    }

    public float getCollisionBorderSize()
    {
        return 0.0F;
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
        else if (!source.isExplosion() && this.getDisplayedItem() != null)
        {
            if (!this.worldObj.isRemote)
            {
                this.dropItemOrSelf(source.getEntity(), false);
                this.setDisplayedItem((ItemStack)null);
            }

            return true;
        }
        else
        {
            return super.attackEntityFrom(source, amount);
        }
    }

    public int getWidthPixels()
    {
        return 12;
    }

    public int getHeightPixels()
    {
        return 12;
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = 16.0D;
        d0 = d0 * 64.0D * this.renderDistanceWeight;
        return distance < d0 * d0;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity brokenEntity)
    {
        this.dropItemOrSelf(brokenEntity, true);
    }

    public void dropItemOrSelf(Entity p_146065_1_, boolean p_146065_2_)
    {
        if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack itemstack = this.getDisplayedItem();

            if (p_146065_1_ instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)p_146065_1_;

                if (entityplayer.capabilities.isCreativeMode)
                {
                    this.removeFrameFromMap(itemstack);
                    return;
                }
            }

            if (p_146065_2_)
            {
                this.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
            }

            if (itemstack != null && this.rand.nextFloat() < this.itemDropChance)
            {
                itemstack = itemstack.copy();
                this.removeFrameFromMap(itemstack);
                this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    /**
     * Removes the dot representing this frame's position from the map when the item frame is broken.
     */
    private void removeFrameFromMap(ItemStack p_110131_1_)
    {
        if (p_110131_1_ != null)
        {
            if (p_110131_1_.getItem() == Items.filled_map)
            {
                MapData mapdata = ((ItemMap)p_110131_1_.getItem()).getMapData(p_110131_1_, this.worldObj);
                mapdata.mapDecorations.remove("frame-" + this.getEntityId());
            }

            p_110131_1_.setItemFrame((EntityItemFrame)null);
        }
    }

    public ItemStack getDisplayedItem()
    {
        return this.getDataWatcher().getWatchableObjectItemStack(8);
    }

    public void setDisplayedItem(ItemStack p_82334_1_)
    {
        this.setDisplayedItemWithUpdate(p_82334_1_, true);
    }

    private void setDisplayedItemWithUpdate(ItemStack p_174864_1_, boolean p_174864_2_)
    {
        if (p_174864_1_ != null)
        {
            p_174864_1_ = p_174864_1_.copy();
            p_174864_1_.stackSize = 1;
            p_174864_1_.setItemFrame(this);
        }

        this.getDataWatcher().updateObject(8, p_174864_1_);
        this.getDataWatcher().setObjectWatched(8);

        if (p_174864_2_ && this.hangingPosition != null)
        {
            this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
        }
    }

    /**
     * Return the rotation of the item currently on this frame.
     */
    public int getRotation()
    {
        return this.getDataWatcher().getWatchableObjectByte(9);
    }

    public void setItemRotation(int p_82336_1_)
    {
        this.func_174865_a(p_82336_1_, true);
    }

    private void func_174865_a(int p_174865_1_, boolean p_174865_2_)
    {
        this.getDataWatcher().updateObject(9, Byte.valueOf((byte)(p_174865_1_ % 8)));

        if (p_174865_2_ && this.hangingPosition != null)
        {
            this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        if (this.getDisplayedItem() != null)
        {
            tagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
            tagCompound.setByte("ItemRotation", (byte)this.getRotation());
            tagCompound.setFloat("ItemDropChance", this.itemDropChance);
        }

        super.writeEntityToNBT(tagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        NBTTagCompound nbttagcompound = tagCompund.getCompoundTag("Item");

        if (nbttagcompound != null && !nbttagcompound.hasNoTags())
        {
            this.setDisplayedItemWithUpdate(ItemStack.loadItemStackFromNBT(nbttagcompound), false);
            this.func_174865_a(tagCompund.getByte("ItemRotation"), false);

            if (tagCompund.hasKey("ItemDropChance", 99))
            {
                this.itemDropChance = tagCompund.getFloat("ItemDropChance");
            }

            if (tagCompund.hasKey("Direction"))
            {
                this.func_174865_a(this.getRotation() * 2, false);
            }
        }

        super.readEntityFromNBT(tagCompund);
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer playerIn)
    {
        if (this.getDisplayedItem() == null)
        {
            ItemStack itemstack = playerIn.getHeldItem();

            if (itemstack != null && !this.worldObj.isRemote)
            {
                this.setDisplayedItem(itemstack);

                if (!playerIn.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
                {
                    playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
                }
            }
        }
        else if (!this.worldObj.isRemote)
        {
            this.setItemRotation(this.getRotation() + 1);
        }

        return true;
    }

    public int func_174866_q()
    {
        return this.getDisplayedItem() == null ? 0 : this.getRotation() % 8 + 1;
    }
}
