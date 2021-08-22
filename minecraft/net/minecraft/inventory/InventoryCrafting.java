package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCrafting implements IInventory {
	/** List of the stacks in the crafting matrix. */
	private final ItemStack[] stackList;

	/** the width of the crafting inventory */
	private final int inventoryWidth;
	private final int inventoryHeight;

	/**
	 * Class containing the callbacks for the events on_GUIClosed and
	 * on_CraftMaxtrixChanged.
	 */
	private final Container eventHandler;

	public InventoryCrafting(Container eventHandlerIn, int width, int height) {
		int i = width * height;
		this.stackList = new ItemStack[i];
		this.eventHandler = eventHandlerIn;
		this.inventoryWidth = width;
		this.inventoryHeight = height;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return this.stackList.length;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index) {
		return index >= this.getSizeInventory() ? null : this.stackList[index];
	}

	/**
	 * Returns the itemstack in the slot specified (Top left is 0, 0). Args: row,
	 * column
	 */
	public ItemStack getStackInRowAndColumn(int row, int column) {
		return row >= 0 && row < this.inventoryWidth && column >= 0 && column <= this.inventoryHeight ? this.getStackInSlot(row + column * this.inventoryWidth) : null;
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return "container.crafting";
	}

	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username
	 * in chat
	 */
	public IChatComponent getDisplayName() {
		return (IChatComponent) (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]));
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		if (this.stackList[index] != null) {
			ItemStack itemstack = this.stackList[index];
			this.stackList[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns
	 * them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count) {
		if (this.stackList[index] != null) {
			if (this.stackList[index].stackSize <= count) {
				ItemStack itemstack1 = this.stackList[index];
				this.stackList[index] = null;
				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack1;
			} else {
				ItemStack itemstack = this.stackList[index].splitStack(count);

				if (this.stackList[index].stackSize == 0) {
					this.stackList[index] = null;
				}

				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stackList[index] = stack;
		this.eventHandler.onCraftMatrixChanged(this);
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64,
	 * possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 64;
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
		return true;
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

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
	}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		for (int i = 0; i < this.stackList.length; ++i) {
			this.stackList[i] = null;
		}
	}

	public int getHeight() {
		return this.inventoryHeight;
	}

	public int getWidth() {
		return this.inventoryWidth;
	}
}
