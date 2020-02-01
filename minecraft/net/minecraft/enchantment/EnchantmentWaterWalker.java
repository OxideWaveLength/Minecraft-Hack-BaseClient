package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWalker extends Enchantment
{
    public EnchantmentWaterWalker(int p_i45762_1_, ResourceLocation p_i45762_2_, int p_i45762_3_)
    {
        super(p_i45762_1_, p_i45762_2_, p_i45762_3_, EnumEnchantmentType.ARMOR_FEET);
        this.setName("waterWalker");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return enchantmentLevel * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 3;
    }
}
