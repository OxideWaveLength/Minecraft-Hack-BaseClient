package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
	private IInventory tileBeacon;

	/**
	 * This beacon's slot where you put in Emerald, Diamond, Gold or Iron Ingot.
	 */
	private final ContainerBeacon.BeaconSlot beaconSlot;

	public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn) {
		this.tileBeacon = tileBeaconIn;
		this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(tileBeaconIn, 0, 136, 110));
		int i = 36;
		int j = 137;

		for (int k = 0; k < 3; ++k) {
			for (int l = 0; l < 9; ++l) {
				this.addSlotToContainer(new Slot(playerInventory, l + k * 9 + 9, i + l * 18, j + k * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlotToContainer(new Slot(playerInventory, i1, i + i1 * 18, 58 + j));
		}
	}

	public void onCraftGuiOpened(ICrafting listener) {
		super.onCraftGuiOpened(listener);
		listener.func_175173_a(this, this.tileBeacon);
	}

	public void updateProgressBar(int id, int data) {
		this.tileBeacon.setField(id, data);
	}

	public IInventory func_180611_e() {
		return this.tileBeacon;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		if (playerIn != null && !playerIn.worldObj.isRemote) {
			ItemStack itemstack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());

			if (itemstack != null) {
				playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
			}
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tileBeacon.isUseableByPlayer(playerIn);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index == 0) {
				if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1) {
				if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
					return null;
				}
			} else if (index >= 1 && index < 28) {
				if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
					return null;
				}
			} else if (index >= 28 && index < 37) {
				if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	class BeaconSlot extends Slot {
		public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {
			super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
		}

		public boolean isItemValid(ItemStack stack) {
			return stack == null ? false : stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
		}

		public int getSlotStackLimit() {
			return 1;
		}
	}
}
