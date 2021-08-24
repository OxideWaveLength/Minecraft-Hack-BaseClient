package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
	private ItemStack[] minecartContainerItems = new ItemStack[36];

	/**
	 * When set to true, the minecart will drop all items when setDead() is called.
	 * When false (such as when travelling dimensions) it preserves its contents.
	 */
	private boolean dropContentsWhenDead = true;

	public EntityMinecartContainer(World worldIn) {
		super(worldIn);
	}

	public EntityMinecartContainer(World worldIn, double p_i1717_2_, double p_i1717_4_, double p_i1717_6_) {
		super(worldIn, p_i1717_2_, p_i1717_4_, p_i1717_6_);
	}

	public void killMinecart(DamageSource p_94095_1_) {
		super.killMinecart(p_94095_1_);

		if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
			InventoryHelper.func_180176_a(this.worldObj, this, this);
		}
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index) {
		return this.minecartContainerItems[index];
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns
	 * them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count) {
		if (this.minecartContainerItems[index] != null) {
			if (this.minecartContainerItems[index].stackSize <= count) {
				ItemStack itemstack1 = this.minecartContainerItems[index];
				this.minecartContainerItems[index] = null;
				return itemstack1;
			} else {
				ItemStack itemstack = this.minecartContainerItems[index].splitStack(count);

				if (this.minecartContainerItems[index].stackSize == 0) {
					this.minecartContainerItems[index] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		if (this.minecartContainerItems[index] != null) {
			ItemStack itemstack = this.minecartContainerItems[index];
			this.minecartContainerItems[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.minecartContainerItems[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void markDirty() {
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with
	 * Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.isDead ? false : player.getDistanceSqToEntity(this) <= 64.0D;
	}

	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64,
	 * possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Teleports the entity to another dimension. Params: Dimension number to
	 * teleport to
	 */
	public void travelToDimension(int dimensionId) {
		this.dropContentsWhenDead = false;
		super.travelToDimension(dimensionId);
	}

	/**
	 * Will get destroyed next tick.
	 */
	public void setDead() {
		if (this.dropContentsWhenDead) {
			InventoryHelper.func_180176_a(this.worldObj, this, this);
		}

		super.setDead();
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.minecartContainerItems.length; ++i) {
			if (this.minecartContainerItems[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.minecartContainerItems[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		tagCompound.setTag("Items", nbttaglist);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		NBTTagList nbttaglist = tagCompund.getTagList("Items", 10);
		this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.minecartContainerItems.length) {
				this.minecartContainerItems[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer playerIn) {
		if (!this.worldObj.isRemote) {
			playerIn.displayGUIChest(this);
		}

		return true;
	}

	protected void applyDrag() {
		int i = 15 - Container.calcRedstoneFromInventory(this);
		float f = 0.98F + (float) i * 0.001F;
		this.motionX *= (double) f;
		this.motionY *= 0.0D;
		this.motionZ *= (double) f;
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
	}

	public int getFieldCount() {
		return 0;
	}

	public boolean isLocked() {
		return false;
	}

	public void setLockCode(LockCode code) {
	}

	public LockCode getLockCode() {
		return LockCode.EMPTY_CODE;
	}

	public void clear() {
		for (int i = 0; i < this.minecartContainerItems.length; ++i) {
			this.minecartContainerItems[i] = null;
		}
	}
}
