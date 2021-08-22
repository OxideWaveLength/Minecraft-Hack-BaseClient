package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentOxygen extends Enchantment {
	public EnchantmentOxygen(int enchID, ResourceLocation p_i45766_2_, int p_i45766_3_) {
		super(enchID, p_i45766_2_, p_i45766_3_, EnumEnchantmentType.ARMOR_HEAD);
		this.setName("oxygen");
	}

	/**
	 * Returns the minimal value of enchantability needed on the enchantment level
	 * passed.
	 */
	public int getMinEnchantability(int enchantmentLevel) {
		return 10 * enchantmentLevel;
	}

	/**
	 * Returns the maximum value of enchantability nedded on the enchantment level
	 * passed.
	 */
	public int getMaxEnchantability(int enchantmentLevel) {
		return this.getMinEnchantability(enchantmentLevel) + 30;
	}

	/**
	 * Returns the maximum level that the enchantment can have.
	 */
	public int getMaxLevel() {
		return 3;
	}
}
