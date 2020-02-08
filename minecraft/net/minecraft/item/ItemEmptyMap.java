package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
	protected ItemEmptyMap() {
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		ItemStack itemstack = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
		String s = "map_" + itemstack.getMetadata();
		MapData mapdata = new MapData(s);
		worldIn.setItemData(s, mapdata);
		mapdata.scale = 0;
		mapdata.calculateMapCenter(playerIn.posX, playerIn.posZ, mapdata.scale);
		mapdata.dimension = (byte) worldIn.provider.getDimensionId();
		mapdata.markDirty();
		--itemStackIn.stackSize;

		if (itemStackIn.stackSize <= 0) {
			return itemstack;
		} else {
			if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
				playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
			}

			playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
			return itemStackIn;
		}
	}
}
