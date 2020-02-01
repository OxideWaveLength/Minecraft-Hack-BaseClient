package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFishingSpeed extends Enchantment
{
    protected EnchantmentFishingSpeed(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType)
    {
        super(enchID, enchName, enchWeight, enchType);
        this.setName("fishingSpeed");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 15 + (enchantmentLevel - 1) * 9;
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
        return 3;
    }
}
