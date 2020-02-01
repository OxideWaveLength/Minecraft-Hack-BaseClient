package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFireAspect extends Enchantment
{
    protected EnchantmentFireAspect(int enchID, ResourceLocation enchName, int enchWeight)
    {
        super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
        this.setName("fire");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 2;
    }
}
