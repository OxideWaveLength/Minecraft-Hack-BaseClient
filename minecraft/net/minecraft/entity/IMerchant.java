package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public interface IMerchant {
	void setCustomer(EntityPlayer p_70932_1_);

	EntityPlayer getCustomer();

	MerchantRecipeList getRecipes(EntityPlayer p_70934_1_);

	void setRecipes(MerchantRecipeList recipeList);

	void useRecipe(MerchantRecipe recipe);

	/**
	 * Notifies the merchant of a possible merchantrecipe being fulfilled or not.
	 * Usually, this is just a sound byte being played depending if the suggested
	 * itemstack is not null.
	 */
	void verifySellingItem(ItemStack stack);

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username
	 * in chat
	 */
	IChatComponent getDisplayName();
}
