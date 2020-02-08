package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item {
	public ItemNameTag() {
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on
	 * sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
		if (!stack.hasDisplayName()) {
			return false;
		} else if (target instanceof EntityLiving) {
			EntityLiving entityliving = (EntityLiving) target;
			entityliving.setCustomNameTag(stack.getDisplayName());
			entityliving.enablePersistence();
			--stack.stackSize;
			return true;
		} else {
			return super.itemInteractionForEntity(stack, playerIn, target);
		}
	}
}
