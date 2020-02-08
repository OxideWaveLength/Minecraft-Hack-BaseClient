package net.minecraft.inventory;

import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
	/** SlotEnchantmentTable object with ItemStack to be enchanted */
	public IInventory tableInventory;

	/** current world (for bookshelf counting) */
	private World worldPointer;
	private BlockPos position;
	private Random rand;
	public int xpSeed;

	/** 3-member array storing the enchantment levels of each slot */
	public int[] enchantLevels;
	public int[] field_178151_h;

	public ContainerEnchantment(InventoryPlayer playerInv, World worldIn) {
		this(playerInv, worldIn, BlockPos.ORIGIN);
	}

	public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos) {
		this.tableInventory = new InventoryBasic("Enchant", true, 2) {
			public int getInventoryStackLimit() {
				return 64;
			}

			public void markDirty() {
				super.markDirty();
				ContainerEnchantment.this.onCraftMatrixChanged(this);
			}
		};
		this.rand = new Random();
		this.enchantLevels = new int[3];
		this.field_178151_h = new int[] { -1, -1, -1 };
		this.worldPointer = worldIn;
		this.position = pos;
		this.xpSeed = playerInv.player.getXPSeed();
		this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47) {
			public boolean isItemValid(ItemStack stack) {
				return true;
			}

			public int getSlotStackLimit() {
				return 1;
			}
		});
		this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47) {
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE;
			}
		});

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
		}
	}

	public void onCraftGuiOpened(ICrafting listener) {
		super.onCraftGuiOpened(listener);
		listener.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
		listener.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
		listener.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
		listener.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
		listener.sendProgressBarUpdate(this, 4, this.field_178151_h[0]);
		listener.sendProgressBarUpdate(this, 5, this.field_178151_h[1]);
		listener.sendProgressBarUpdate(this, 6, this.field_178151_h[2]);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
			icrafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
			icrafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
			icrafting.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
			icrafting.sendProgressBarUpdate(this, 4, this.field_178151_h[0]);
			icrafting.sendProgressBarUpdate(this, 5, this.field_178151_h[1]);
			icrafting.sendProgressBarUpdate(this, 6, this.field_178151_h[2]);
		}
	}

	public void updateProgressBar(int id, int data) {
		if (id >= 0 && id <= 2) {
			this.enchantLevels[id] = data;
		} else if (id == 3) {
			this.xpSeed = data;
		} else if (id >= 4 && id <= 6) {
			this.field_178151_h[id - 4] = data;
		} else {
			super.updateProgressBar(id, data);
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		if (inventoryIn == this.tableInventory) {
			ItemStack itemstack = inventoryIn.getStackInSlot(0);

			if (itemstack != null && itemstack.isItemEnchantable()) {
				if (!this.worldPointer.isRemote) {
					int l = 0;

					for (int j = -1; j <= 1; ++j) {
						for (int k = -1; k <= 1; ++k) {
							if ((j != 0 || k != 0) && this.worldPointer.isAirBlock(this.position.add(k, 0, j)) && this.worldPointer.isAirBlock(this.position.add(k, 1, j))) {
								if (this.worldPointer.getBlockState(this.position.add(k * 2, 0, j * 2)).getBlock() == Blocks.bookshelf) {
									++l;
								}

								if (this.worldPointer.getBlockState(this.position.add(k * 2, 1, j * 2)).getBlock() == Blocks.bookshelf) {
									++l;
								}

								if (k != 0 && j != 0) {
									if (this.worldPointer.getBlockState(this.position.add(k * 2, 0, j)).getBlock() == Blocks.bookshelf) {
										++l;
									}

									if (this.worldPointer.getBlockState(this.position.add(k * 2, 1, j)).getBlock() == Blocks.bookshelf) {
										++l;
									}

									if (this.worldPointer.getBlockState(this.position.add(k, 0, j * 2)).getBlock() == Blocks.bookshelf) {
										++l;
									}

									if (this.worldPointer.getBlockState(this.position.add(k, 1, j * 2)).getBlock() == Blocks.bookshelf) {
										++l;
									}
								}
							}
						}
					}

					this.rand.setSeed((long) this.xpSeed);

					for (int i1 = 0; i1 < 3; ++i1) {
						this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, l, itemstack);
						this.field_178151_h[i1] = -1;

						if (this.enchantLevels[i1] < i1 + 1) {
							this.enchantLevels[i1] = 0;
						}
					}

					for (int j1 = 0; j1 < 3; ++j1) {
						if (this.enchantLevels[j1] > 0) {
							List<EnchantmentData> list = this.func_178148_a(itemstack, j1, this.enchantLevels[j1]);

							if (list != null && !list.isEmpty()) {
								EnchantmentData enchantmentdata = (EnchantmentData) list.get(this.rand.nextInt(list.size()));
								this.field_178151_h[j1] = enchantmentdata.enchantmentobj.effectId | enchantmentdata.enchantmentLevel << 8;
							}
						}
					}

					this.detectAndSendChanges();
				}
			} else {
				for (int i = 0; i < 3; ++i) {
					this.enchantLevels[i] = 0;
					this.field_178151_h[i] = -1;
				}
			}
		}
	}

	/**
	 * Handles the given Button-click on the server, currently only used by
	 * enchanting. Name is for legacy.
	 */
	public boolean enchantItem(EntityPlayer playerIn, int id) {
		ItemStack itemstack = this.tableInventory.getStackInSlot(0);
		ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
		int i = id + 1;

		if ((itemstack1 == null || itemstack1.stackSize < i) && !playerIn.capabilities.isCreativeMode) {
			return false;
		} else if (this.enchantLevels[id] > 0 && itemstack != null && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode)) {
			if (!this.worldPointer.isRemote) {
				List<EnchantmentData> list = this.func_178148_a(itemstack, id, this.enchantLevels[id]);
				boolean flag = itemstack.getItem() == Items.book;

				if (list != null) {
					playerIn.removeExperienceLevel(i);

					if (flag) {
						itemstack.setItem(Items.enchanted_book);
					}

					for (int j = 0; j < list.size(); ++j) {
						EnchantmentData enchantmentdata = (EnchantmentData) list.get(j);

						if (flag) {
							Items.enchanted_book.addEnchantment(itemstack, enchantmentdata);
						} else {
							itemstack.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
						}
					}

					if (!playerIn.capabilities.isCreativeMode) {
						itemstack1.stackSize -= i;

						if (itemstack1.stackSize <= 0) {
							this.tableInventory.setInventorySlotContents(1, (ItemStack) null);
						}
					}

					playerIn.triggerAchievement(StatList.field_181739_W);
					this.tableInventory.markDirty();
					this.xpSeed = playerIn.getXPSeed();
					this.onCraftMatrixChanged(this.tableInventory);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private List<EnchantmentData> func_178148_a(ItemStack stack, int p_178148_2_, int p_178148_3_) {
		this.rand.setSeed((long) (this.xpSeed + p_178148_2_));
		List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, stack, p_178148_3_);

		if (stack.getItem() == Items.book && list != null && list.size() > 1) {
			list.remove(this.rand.nextInt(list.size()));
		}

		return list;
	}

	public int getLapisAmount() {
		ItemStack itemstack = this.tableInventory.getStackInSlot(1);
		return itemstack == null ? 0 : itemstack.stackSize;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		if (!this.worldPointer.isRemote) {
			for (int i = 0; i < this.tableInventory.getSizeInventory(); ++i) {
				ItemStack itemstack = this.tableInventory.removeStackFromSlot(i);

				if (itemstack != null) {
					playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
				}
			}
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.enchanting_table ? false : playerIn.getDistanceSq((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
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
				if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
					return null;
				}
			} else if (index == 1) {
				if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
					return null;
				}
			} else if (itemstack1.getItem() == Items.dye && EnumDyeColor.byDyeDamage(itemstack1.getMetadata()) == EnumDyeColor.BLUE) {
				if (!this.mergeItemStack(itemstack1, 1, 2, true)) {
					return null;
				}
			} else {
				if (((Slot) this.inventorySlots.get(0)).getHasStack() || !((Slot) this.inventorySlots.get(0)).isItemValid(itemstack1)) {
					return null;
				}

				if (itemstack1.hasTagCompound() && itemstack1.stackSize == 1) {
					((Slot) this.inventorySlots.get(0)).putStack(itemstack1.copy());
					itemstack1.stackSize = 0;
				} else if (itemstack1.stackSize >= 1) {
					((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(itemstack1.getItem(), 1, itemstack1.getMetadata()));
					--itemstack1.stackSize;
				}
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
}
