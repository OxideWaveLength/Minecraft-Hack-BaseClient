package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class Enchantment
{
    private static final Enchantment[] enchantmentsList = new Enchantment[256];
    public static final Enchantment[] enchantmentsBookList;
    private static final Map<ResourceLocation, Enchantment> locationEnchantments = Maps.<ResourceLocation, Enchantment>newHashMap();
    public static final Enchantment protection = new EnchantmentProtection(0, new ResourceLocation("protection"), 10, 0);

    /** Protection against fire */
    public static final Enchantment fireProtection = new EnchantmentProtection(1, new ResourceLocation("fire_protection"), 5, 1);
    public static final Enchantment featherFalling = new EnchantmentProtection(2, new ResourceLocation("feather_falling"), 5, 2);

    /** Protection against explosions */
    public static final Enchantment blastProtection = new EnchantmentProtection(3, new ResourceLocation("blast_protection"), 2, 3);
    public static final Enchantment projectileProtection = new EnchantmentProtection(4, new ResourceLocation("projectile_protection"), 5, 4);
    public static final Enchantment respiration = new EnchantmentOxygen(5, new ResourceLocation("respiration"), 2);

    /** Increases underwater mining rate */
    public static final Enchantment aquaAffinity = new EnchantmentWaterWorker(6, new ResourceLocation("aqua_affinity"), 2);
    public static final Enchantment thorns = new EnchantmentThorns(7, new ResourceLocation("thorns"), 1);
    public static final Enchantment depthStrider = new EnchantmentWaterWalker(8, new ResourceLocation("depth_strider"), 2);
    public static final Enchantment sharpness = new EnchantmentDamage(16, new ResourceLocation("sharpness"), 10, 0);
    public static final Enchantment smite = new EnchantmentDamage(17, new ResourceLocation("smite"), 5, 1);
    public static final Enchantment baneOfArthropods = new EnchantmentDamage(18, new ResourceLocation("bane_of_arthropods"), 5, 2);
    public static final Enchantment knockback = new EnchantmentKnockback(19, new ResourceLocation("knockback"), 5);

    /** Lights mobs on fire */
    public static final Enchantment fireAspect = new EnchantmentFireAspect(20, new ResourceLocation("fire_aspect"), 2);

    /** Mobs have a chance to drop more loot */
    public static final Enchantment looting = new EnchantmentLootBonus(21, new ResourceLocation("looting"), 2, EnumEnchantmentType.WEAPON);

    /** Faster resource gathering while in use */
    public static final Enchantment efficiency = new EnchantmentDigging(32, new ResourceLocation("efficiency"), 10);

    /**
     * Blocks mined will drop themselves, even if it should drop something else (e.g. stone will drop stone, not
     * cobblestone)
     */
    public static final Enchantment silkTouch = new EnchantmentUntouching(33, new ResourceLocation("silk_touch"), 1);

    /**
     * Sometimes, the tool's durability will not be spent when the tool is used
     */
    public static final Enchantment unbreaking = new EnchantmentDurability(34, new ResourceLocation("unbreaking"), 5);

    /** Can multiply the drop rate of items from blocks */
    public static final Enchantment fortune = new EnchantmentLootBonus(35, new ResourceLocation("fortune"), 2, EnumEnchantmentType.DIGGER);

    /** Power enchantment for bows, add's extra damage to arrows. */
    public static final Enchantment power = new EnchantmentArrowDamage(48, new ResourceLocation("power"), 10);

    /**
     * Knockback enchantments for bows, the arrows will knockback the target when hit.
     */
    public static final Enchantment punch = new EnchantmentArrowKnockback(49, new ResourceLocation("punch"), 2);

    /**
     * Flame enchantment for bows. Arrows fired by the bow will be on fire. Any target hit will also set on fire.
     */
    public static final Enchantment flame = new EnchantmentArrowFire(50, new ResourceLocation("flame"), 2);

    /**
     * Infinity enchantment for bows. The bow will not consume arrows anymore, but will still required at least one
     * arrow on inventory use the bow.
     */
    public static final Enchantment infinity = new EnchantmentArrowInfinite(51, new ResourceLocation("infinity"), 1);
    public static final Enchantment luckOfTheSea = new EnchantmentLootBonus(61, new ResourceLocation("luck_of_the_sea"), 2, EnumEnchantmentType.FISHING_ROD);
    public static final Enchantment lure = new EnchantmentFishingSpeed(62, new ResourceLocation("lure"), 2, EnumEnchantmentType.FISHING_ROD);
    public final int effectId;
    private final int weight;

    /** The EnumEnchantmentType given to this Enchantment. */
    public EnumEnchantmentType type;

    /** Used in localisation and stats. */
    protected String name;

    /**
     * Retrieves an Enchantment from the enchantmentsList
     */
    public static Enchantment getEnchantmentById(int enchID)
    {
        return enchID >= 0 && enchID < enchantmentsList.length ? enchantmentsList[enchID] : null;
    }

    protected Enchantment(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType)
    {
        this.effectId = enchID;
        this.weight = enchWeight;
        this.type = enchType;

        if (enchantmentsList[enchID] != null)
        {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        }
        else
        {
            enchantmentsList[enchID] = this;
            locationEnchantments.put(enchName, this);
        }
    }

    /**
     * Retrieves an enchantment by using its location name.
     */
    public static Enchantment getEnchantmentByLocation(String location)
    {
        return (Enchantment)locationEnchantments.get(new ResourceLocation(location));
    }

    public static Set<ResourceLocation> func_181077_c()
    {
        return locationEnchantments.keySet();
    }

    /**
     * Retrieves the weight value of an Enchantment. This weight value is used within vanilla to determine how rare an
     * enchantment is.
     */
    public int getWeight()
    {
        return this.weight;
    }

    /**
     * Returns the minimum level that the enchantment can have.
     */
    public int getMinLevel()
    {
        return 1;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 1 + enchantmentLevel * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 5;
    }

    /**
     * Calculates the damage protection of the enchantment based on level and damage source passed.
     */
    public int calcModifierDamage(int level, DamageSource source)
    {
        return 0;
    }

    /**
     * Calculates the additional damage that will be dealt by an item with this enchantment. This alternative to
     * calcModifierDamage is sensitive to the targets EnumCreatureAttribute.
     */
    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
        return 0.0F;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(Enchantment ench)
    {
        return this != ench;
    }

    /**
     * Sets the enchantment name
     */
    public Enchantment setName(String enchName)
    {
        this.name = enchName;
        return this;
    }

    /**
     * Return the name of key in translation table of this enchantment.
     */
    public String getName()
    {
        return "enchantment." + this.name;
    }

    /**
     * Returns the correct traslated name of the enchantment and the level in roman numbers.
     */
    public String getTranslatedName(int level)
    {
        String s = StatCollector.translateToLocal(this.getName());
        return s + " " + StatCollector.translateToLocal("enchantment.level." + level);
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     */
    public boolean canApply(ItemStack stack)
    {
        return this.type.canEnchantItem(stack.getItem());
    }

    /**
     * Called whenever a mob is damaged with an item that has this enchantment on it.
     */
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level)
    {
    }

    /**
     * Whenever an entity that has this enchantment on one of its associated items is damaged this method will be
     * called.
     */
    public void onUserHurt(EntityLivingBase user, Entity attacker, int level)
    {
    }

    static
    {
        List<Enchantment> list = Lists.<Enchantment>newArrayList();

        for (Enchantment enchantment : enchantmentsList)
        {
            if (enchantment != null)
            {
                list.add(enchantment);
            }
        }

        enchantmentsBookList = (Enchantment[])list.toArray(new Enchantment[list.size()]);
    }
}
