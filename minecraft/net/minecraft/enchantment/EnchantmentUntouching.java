package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentUntouching extends Enchantment {
	protected EnchantmentUntouching(int p_i45763_1_, ResourceLocation p_i45763_2_, int p_i45763_3_) {
		super(p_i45763_1_, p_i45763_2_, p_i45763_3_, EnumEnchantmentType.DIGGER);
		this.setName("untouching");
	}

	/**
	 * Returns the minimal value of enchantability needed on the enchantment level
	 * passed.
	 */
	public int getMinEnchantability(int enchantmentLevel) {
		return 15;
	}

	/**
	 * Returns the maximum value of enchantability nedded on the enchantment level
	 * passed.
	 */
	public int getMaxEnchantability(int enchantmentLevel) {
		return super.getMinEnchantability(enchantmentLevel) + 50;
	}

	/**
	 * Returns the maximum level that the enchantment can have.
	 */
	public int getMaxLevel() {
		return 1;
	}

	/**
	 * Determines if the enchantment passed can be applyied together with this
	 * enchantment.
	 */
	public boolean canApplyTogether(Enchantment ench) {
		return super.canApplyTogether(ench) && ench.effectId != fortune.effectId;
	}

	/**
	 * Determines if this enchantment can be applied to a specific ItemStack.
	 */
	public boolean canApply(ItemStack stack) {
		return stack.getItem() == Items.shears ? true : super.canApply(stack);
	}
}
