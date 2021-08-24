package net.minecraft.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable {
	private ItemStack[] inventory = new ItemStack[5];
	private String customName;
	private int transferCooldown = -1;

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.inventory = new ItemStack[this.getSizeInventory()];

		if (compound.hasKey("CustomName", 8)) {
			this.customName = compound.getString("CustomName");
		}

		this.transferCooldown = compound.getInteger("TransferCooldown");

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");

			if (j >= 0 && j < this.inventory.length) {
				this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Items", nbttaglist);
		compound.setInteger("TransferCooldown", this.transferCooldown);

		if (this.hasCustomName()) {
			compound.setString("CustomName", this.customName);
		}
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void markDirty() {
		super.markDirty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return this.inventory.length;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index) {
		return this.inventory[index];
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns
	 * them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count) {
		if (this.inventory[index] != null) {
			if (this.inventory[index].stackSize <= count) {
				ItemStack itemstack1 = this.inventory[index];
				this.inventory[index] = null;
				return itemstack1;
			} else {
				ItemStack itemstack = this.inventory[index].splitStack(count);

				if (this.inventory[index].stackSize == 0) {
					this.inventory[index] = null;
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
		if (this.inventory[index] != null) {
			ItemStack itemstack = this.inventory[index];
			this.inventory[index] = null;
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
		this.inventory[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.hopper";
	}

	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String customNameIn) {
		this.customName = customNameIn;
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
		return true;
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.worldObj != null && !this.worldObj.isRemote) {
			--this.transferCooldown;

			if (!this.isOnTransferCooldown()) {
				this.setTransferCooldown(0);
				this.updateHopper();
			}
		}
	}

	public boolean updateHopper() {
		if (this.worldObj != null && !this.worldObj.isRemote) {
			if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
				boolean flag = false;

				if (!this.isEmpty()) {
					flag = this.transferItemsOut();
				}

				if (!this.isFull()) {
					flag = captureDroppedItems(this) || flag;
				}

				if (flag) {
					this.setTransferCooldown(8);
					this.markDirty();
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	private boolean isEmpty() {
		for (ItemStack itemstack : this.inventory) {
			if (itemstack != null) {
				return false;
			}
		}

		return true;
	}

	private boolean isFull() {
		for (ItemStack itemstack : this.inventory) {
			if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	private boolean transferItemsOut() {
		IInventory iinventory = this.getInventoryForHopperTransfer();

		if (iinventory == null) {
			return false;
		} else {
			EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();

			if (this.isInventoryFull(iinventory, enumfacing)) {
				return false;
			} else {
				for (int i = 0; i < this.getSizeInventory(); ++i) {
					if (this.getStackInSlot(i) != null) {
						ItemStack itemstack = this.getStackInSlot(i).copy();
						ItemStack itemstack1 = putStackInInventoryAllSlots(iinventory, this.decrStackSize(i, 1), enumfacing);

						if (itemstack1 == null || itemstack1.stackSize == 0) {
							iinventory.markDirty();
							return true;
						}

						this.setInventorySlotContents(i, itemstack);
					}
				}

				return false;
			}
		}
	}

	/**
	 * Returns false if the inventory has any room to place items in
	 */
	private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
		if (inventoryIn instanceof ISidedInventory) {
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for (int k = 0; k < aint.length; ++k) {
				ItemStack itemstack1 = isidedinventory.getStackInSlot(aint[k]);

				if (itemstack1 == null || itemstack1.stackSize != itemstack1.getMaxStackSize()) {
					return false;
				}
			}
		} else {
			int i = inventoryIn.getSizeInventory();

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = inventoryIn.getStackInSlot(j);

				if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns false if the specified IInventory contains any items
	 */
	private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
		if (inventoryIn instanceof ISidedInventory) {
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for (int i = 0; i < aint.length; ++i) {
				if (isidedinventory.getStackInSlot(aint[i]) != null) {
					return false;
				}
			}
		} else {
			int j = inventoryIn.getSizeInventory();

			for (int k = 0; k < j; ++k) {
				if (inventoryIn.getStackInSlot(k) != null) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean captureDroppedItems(IHopper p_145891_0_) {
		IInventory iinventory = getHopperInventory(p_145891_0_);

		if (iinventory != null) {
			EnumFacing enumfacing = EnumFacing.DOWN;

			if (isInventoryEmpty(iinventory, enumfacing)) {
				return false;
			}

			if (iinventory instanceof ISidedInventory) {
				ISidedInventory isidedinventory = (ISidedInventory) iinventory;
				int[] aint = isidedinventory.getSlotsForFace(enumfacing);

				for (int i = 0; i < aint.length; ++i) {
					if (pullItemFromSlot(p_145891_0_, iinventory, aint[i], enumfacing)) {
						return true;
					}
				}
			} else {
				int j = iinventory.getSizeInventory();

				for (int k = 0; k < j; ++k) {
					if (pullItemFromSlot(p_145891_0_, iinventory, k, enumfacing)) {
						return true;
					}
				}
			}
		} else {
			for (EntityItem entityitem : func_181556_a(p_145891_0_.getWorld(), p_145891_0_.getXPos(), p_145891_0_.getYPos() + 1.0D, p_145891_0_.getZPos())) {
				if (putDropInInventoryAllSlots(p_145891_0_, entityitem)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Pulls from the specified slot in the inventory and places in any available
	 * slot in the hopper. Returns true if the entire stack was moved
	 */
	private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
		ItemStack itemstack = inventoryIn.getStackInSlot(index);

		if (itemstack != null && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing) null);

			if (itemstack2 == null || itemstack2.stackSize == 0) {
				inventoryIn.markDirty();
				return true;
			}

			inventoryIn.setInventorySlotContents(index, itemstack1);
		}

		return false;
	}

	/**
	 * Attempts to place the passed EntityItem's stack into the inventory using as
	 * many slots as possible. Returns false if the stackSize of the drop was not
	 * depleted.
	 */
	public static boolean putDropInInventoryAllSlots(IInventory p_145898_0_, EntityItem itemIn) {
		boolean flag = false;

		if (itemIn == null) {
			return false;
		} else {
			ItemStack itemstack = itemIn.getEntityItem().copy();
			ItemStack itemstack1 = putStackInInventoryAllSlots(p_145898_0_, itemstack, (EnumFacing) null);

			if (itemstack1 != null && itemstack1.stackSize != 0) {
				itemIn.setEntityItemStack(itemstack1);
			} else {
				flag = true;
				itemIn.setDead();
			}

			return flag;
		}
	}

	/**
	 * Attempts to place the passed stack in the inventory, using as many slots as
	 * required. Returns leftover items
	 */
	public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side) {
		if (inventoryIn instanceof ISidedInventory && side != null) {
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for (int k = 0; k < aint.length && stack != null && stack.stackSize > 0; ++k) {
				stack = insertStack(inventoryIn, stack, aint[k], side);
			}
		} else {
			int i = inventoryIn.getSizeInventory();

			for (int j = 0; j < i && stack != null && stack.stackSize > 0; ++j) {
				stack = insertStack(inventoryIn, stack, j, side);
			}
		}

		if (stack != null && stack.stackSize == 0) {
			stack = null;
		}

		return stack;
	}

	/**
	 * Can this hopper insert the specified item from the specified slot on the
	 * specified side?
	 */
	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
		return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
	}

	/**
	 * Can this hopper extract the specified item from the specified slot on the
	 * specified side?
	 */
	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
		return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
	}

	/**
	 * Insert the specified stack to the specified inventory and return any leftover
	 * items
	 */
	private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
		ItemStack itemstack = inventoryIn.getStackInSlot(index);

		if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
			boolean flag = false;

			if (itemstack == null) {
				inventoryIn.setInventorySlotContents(index, stack);
				stack = null;
				flag = true;
			} else if (canCombine(itemstack, stack)) {
				int i = stack.getMaxStackSize() - itemstack.stackSize;
				int j = Math.min(stack.stackSize, i);
				stack.stackSize -= j;
				itemstack.stackSize += j;
				flag = j > 0;
			}

			if (flag) {
				if (inventoryIn instanceof TileEntityHopper) {
					TileEntityHopper tileentityhopper = (TileEntityHopper) inventoryIn;

					if (tileentityhopper.mayTransfer()) {
						tileentityhopper.setTransferCooldown(8);
					}

					inventoryIn.markDirty();
				}

				inventoryIn.markDirty();
			}
		}

		return stack;
	}

	/**
	 * Returns the IInventory that this hopper is pointing into
	 */
	private IInventory getInventoryForHopperTransfer() {
		EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata());
		return getInventoryAtPosition(this.getWorld(), (double) (this.pos.getX() + enumfacing.getFrontOffsetX()), (double) (this.pos.getY() + enumfacing.getFrontOffsetY()), (double) (this.pos.getZ() + enumfacing.getFrontOffsetZ()));
	}

	/**
	 * Returns the IInventory for the specified hopper
	 */
	public static IInventory getHopperInventory(IHopper hopper) {
		return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
	}

	public static List<EntityItem> func_181556_a(World p_181556_0_, double p_181556_1_, double p_181556_3_, double p_181556_5_) {
		return p_181556_0_.<EntityItem>getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(p_181556_1_ - 0.5D, p_181556_3_ - 0.5D, p_181556_5_ - 0.5D, p_181556_1_ + 0.5D, p_181556_3_ + 0.5D, p_181556_5_ + 0.5D), EntitySelectors.selectAnything);
	}

	/**
	 * Returns the IInventory (if applicable) of the TileEntity at the specified
	 * position
	 */
	public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
		IInventory iinventory = null;
		int i = MathHelper.floor_double(x);
		int j = MathHelper.floor_double(y);
		int k = MathHelper.floor_double(z);
		BlockPos blockpos = new BlockPos(i, j, k);
		Block block = worldIn.getBlockState(blockpos).getBlock();

		if (block.hasTileEntity()) {
			TileEntity tileentity = worldIn.getTileEntity(blockpos);

			if (tileentity instanceof IInventory) {
				iinventory = (IInventory) tileentity;

				if (iinventory instanceof TileEntityChest && block instanceof BlockChest) {
					iinventory = ((BlockChest) block).getLockableContainer(worldIn, blockpos);
				}
			}
		}

		if (iinventory == null) {
			List<Entity> list = worldIn.getEntitiesInAABBexcluding((Entity) null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.selectInventories);

			if (list.size() > 0) {
				iinventory = (IInventory) list.get(worldIn.rand.nextInt(list.size()));
			}
		}

		return iinventory;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() != stack2.getItem() ? false : (stack1.getMetadata() != stack2.getMetadata() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
	}

	/**
	 * Gets the world X position for this hopper entity.
	 */
	public double getXPos() {
		return (double) this.pos.getX() + 0.5D;
	}

	/**
	 * Gets the world Y position for this hopper entity.
	 */
	public double getYPos() {
		return (double) this.pos.getY() + 0.5D;
	}

	/**
	 * Gets the world Z position for this hopper entity.
	 */
	public double getZPos() {
		return (double) this.pos.getZ() + 0.5D;
	}

	public void setTransferCooldown(int ticks) {
		this.transferCooldown = ticks;
	}

	public boolean isOnTransferCooldown() {
		return this.transferCooldown > 0;
	}

	public boolean mayTransfer() {
		return this.transferCooldown <= 1;
	}

	public String getGuiID() {
		return "minecraft:hopper";
	}

	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerHopper(playerInventory, this, playerIn);
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
	}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		for (int i = 0; i < this.inventory.length; ++i) {
			this.inventory[i] = null;
		}
	}
}
