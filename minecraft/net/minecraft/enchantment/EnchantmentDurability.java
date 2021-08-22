package net.minecraft.enchantment;

import java.util.Random;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDurability extends Enchantment {
	protected EnchantmentDurability(int enchID, ResourceLocation enchName, int enchWeight) {
		super(enchID, enchName, enchWeight, EnumEnchantmentType.BREAKABLE);
		this.setName("durability");
	}

	/**
	 * Returns the minimal value of enchantability needed on the enchantment level
	 * passed.
	 */
	public int getMinEnchantability(int enchantmentLevel) {
		return 5 + (enchantmentLevel - 1) * 8;
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
		return 3;
	}

	/**
	 * Determines if this enchantment can be applied to a specific ItemStack.
	 */
	public boolean canApply(ItemStack stack) {
		return stack.isItemStackDamageable() ? true : super.canApply(stack);
	}

	/**
	 * Used by ItemStack.attemptDamageItem. Randomly determines if a point of damage
	 * should be negated using the enchantment level (par1). If the ItemStack is
	 * Armor then there is a flat 60% chance for damage to be negated no matter the
	 * enchantment level, otherwise there is a 1-(par/1) chance for damage to be
	 * negated.
	 */
	public static boolean negateDamage(ItemStack p_92097_0_, int p_92097_1_, Random p_92097_2_) {
		return p_92097_0_.getItem() instanceof ItemArmor && p_92097_2_.nextFloat() < 0.6F ? false : p_92097_2_.nextInt(p_92097_1_ + 1) > 0;
	}
}
