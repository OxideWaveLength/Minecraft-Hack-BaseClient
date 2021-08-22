package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand extends TileEntityLockable implements ITickable, ISidedInventory {
	/** an array of the input slot indices */
	private static final int[] inputSlots = new int[] { 3 };

	/** an array of the output slot indices */
	private static final int[] outputSlots = new int[] { 0, 1, 2 };

	/** The ItemStacks currently placed in the slots of the brewing stand */
	private ItemStack[] brewingItemStacks = new ItemStack[4];
	private int brewTime;

	/**
	 * an integer with each bit specifying whether that slot of the stand contains a
	 * potion
	 */
	private boolean[] filledSlots;

	/**
	 * used to check if the current ingredient has been removed from the brewing
	 * stand during brewing
	 */
	private Item ingredientID;
	private String customName;

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.brewing";
	}

	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setName(String name) {
		this.customName = name;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return this.brewingItemStacks.length;
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.brewTime > 0) {
			--this.brewTime;

			if (this.brewTime == 0) {
				this.brewPotions();
				this.markDirty();
			} else if (!this.canBrew()) {
				this.brewTime = 0;
				this.markDirty();
			} else if (this.ingredientID != this.brewingItemStacks[3].getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (this.canBrew()) {
			this.brewTime = 400;
			this.ingredientID = this.brewingItemStacks[3].getItem();
		}

		if (!this.worldObj.isRemote) {
			boolean[] aboolean = this.func_174902_m();

			if (!Arrays.equals(aboolean, this.filledSlots)) {
				this.filledSlots = aboolean;
				IBlockState iblockstate = this.worldObj.getBlockState(this.getPos());

				if (!(iblockstate.getBlock() instanceof BlockBrewingStand)) {
					return;
				}

				for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
					iblockstate = iblockstate.withProperty(BlockBrewingStand.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
				}

				this.worldObj.setBlockState(this.pos, iblockstate, 2);
			}
		}
	}

	private boolean canBrew() {
		if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0) {
			ItemStack itemstack = this.brewingItemStacks[3];

			if (!itemstack.getItem().isPotionIngredient(itemstack)) {
				return false;
			} else {
				boolean flag = false;

				for (int i = 0; i < 3; ++i) {
					if (this.brewingItemStacks[i] != null && this.brewingItemStacks[i].getItem() == Items.potionitem) {
						int j = this.brewingItemStacks[i].getMetadata();
						int k = this.getPotionResult(j, itemstack);

						if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k)) {
							flag = true;
							break;
						}

						List<PotionEffect> list = Items.potionitem.getEffects(j);
						List<PotionEffect> list1 = Items.potionitem.getEffects(k);

						if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) {
							flag = true;
							break;
						}
					}
				}

				return flag;
			}
		} else {
			return false;
		}
	}

	private void brewPotions() {
		if (this.canBrew()) {
			ItemStack itemstack = this.brewingItemStacks[3];

			for (int i = 0; i < 3; ++i) {
				if (this.brewingItemStacks[i] != null && this.brewingItemStacks[i].getItem() == Items.potionitem) {
					int j = this.brewingItemStacks[i].getMetadata();
					int k = this.getPotionResult(j, itemstack);
					List<PotionEffect> list = Items.potionitem.getEffects(j);
					List<PotionEffect> list1 = Items.potionitem.getEffects(k);

					if (j > 0 && list == list1 || list != null && (list.equals(list1) || list1 == null)) {
						if (!ItemPotion.isSplash(j) && ItemPotion.isSplash(k)) {
							this.brewingItemStacks[i].setItemDamage(k);
						}
					} else if (j != k) {
						this.brewingItemStacks[i].setItemDamage(k);
					}
				}
			}

			if (itemstack.getItem().hasContainerItem()) {
				this.brewingItemStacks[3] = new ItemStack(itemstack.getItem().getContainerItem());
			} else {
				--this.brewingItemStacks[3].stackSize;

				if (this.brewingItemStacks[3].stackSize <= 0) {
					this.brewingItemStacks[3] = null;
				}
			}
		}
	}

	/**
	 * The result of brewing a potion of the specified damage value with an
	 * ingredient itemstack.
	 */
	private int getPotionResult(int meta, ItemStack stack) {
		return stack == null ? meta : (stack.getItem().isPotionIngredient(stack) ? PotionHelper.applyIngredient(meta, stack.getItem().getPotionEffect(stack)) : meta);
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");

			if (j >= 0 && j < this.brewingItemStacks.length) {
				this.brewingItemStacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}

		this.brewTime = compound.getShort("BrewTime");

		if (compound.hasKey("CustomName", 8)) {
			this.customName = compound.getString("CustomName");
		}
	}

	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("BrewTime", (short) this.brewTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.brewingItemStacks.length; ++i) {
			if (this.brewingItemStacks[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.brewingItemStacks[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Items", nbttaglist);

		if (this.hasCustomName()) {
			compound.setString("CustomName", this.customName);
		}
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < this.brewingItemStacks.length ? this.brewingItemStacks[index] : null;
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns
	 * them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count) {
		if (index >= 0 && index < this.brewingItemStacks.length) {
			ItemStack itemstack = this.brewingItemStacks[index];
			this.brewingItemStacks[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		if (index >= 0 && index < this.brewingItemStacks.length) {
			ItemStack itemstack = this.brewingItemStacks[index];
			this.brewingItemStacks[index] = null;
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
		if (index >= 0 && index < this.brewingItemStacks.length) {
			this.brewingItemStacks[index] = stack;
		}
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64,
	 * possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with
	 * Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
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
		return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
	}

	public boolean[] func_174902_m() {
		boolean[] aboolean = new boolean[3];

		for (int i = 0; i < 3; ++i) {
			if (this.brewingItemStacks[i] != null) {
				aboolean[i] = true;
			}
		}

		return aboolean;
	}

	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.UP ? inputSlots : outputSlots;
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from
	 * the given side. Args: slot, item, side
	 */
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from
	 * the given side. Args: slot, item, side
	 */
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}

	public String getGuiID() {
		return "minecraft:brewing_stand";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerBrewingStand(playerInventory, this);
	}

	public int getField(int id) {
		switch (id) {
		case 0:
			return this.brewTime;

		default:
			return 0;
		}
	}

	public void setField(int id, int value) {
		switch (id) {
		case 0:
			this.brewTime = value;

		default:
		}
	}

	public int getFieldCount() {
		return 1;
	}

	public void clear() {
		for (int i = 0; i < this.brewingItemStacks.length; ++i) {
			this.brewingItemStacks[i] = null;
		}
	}
}
