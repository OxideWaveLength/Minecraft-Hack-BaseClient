package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
	public ItemWritableBook() {
		this.setMaxStackSize(1);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		playerIn.displayGUIBook(itemStackIn);
		playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		return itemStackIn;
	}

	/**
	 * this method returns true if the book's NBT Tag List "pages" is valid
	 */
	public static boolean isNBTValid(NBTTagCompound nbt) {
		if (nbt == null) {
			return false;
		} else if (!nbt.hasKey("pages", 9)) {
			return false;
		} else {
			NBTTagList nbttaglist = nbt.getTagList("pages", 8);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				String s = nbttaglist.getStringTagAt(i);

				if (s == null) {
					return false;
				}

				if (s.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
