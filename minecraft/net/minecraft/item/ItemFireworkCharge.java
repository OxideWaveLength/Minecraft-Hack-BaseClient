package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item
{
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass != 1)
        {
            return super.getColorFromItemStack(stack, renderPass);
        }
        else
        {
            NBTBase nbtbase = getExplosionTag(stack, "Colors");

            if (!(nbtbase instanceof NBTTagIntArray))
            {
                return 9079434;
            }
            else
            {
                NBTTagIntArray nbttagintarray = (NBTTagIntArray)nbtbase;
                int[] aint = nbttagintarray.getIntArray();

                if (aint.length == 1)
                {
                    return aint[0];
                }
                else
                {
                    int i = 0;
                    int j = 0;
                    int k = 0;

                    for (int l : aint)
                    {
                        i += (l & 16711680) >> 16;
                        j += (l & 65280) >> 8;
                        k += (l & 255) >> 0;
                    }

                    i = i / aint.length;
                    j = j / aint.length;
                    k = k / aint.length;
                    return i << 16 | j << 8 | k;
                }
            }
        }
    }

    public static NBTBase getExplosionTag(ItemStack stack, String key)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");

            if (nbttagcompound != null)
            {
                return nbttagcompound.getTag(key);
            }
        }

        return null;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");

            if (nbttagcompound != null)
            {
                addExplosionInfo(nbttagcompound, tooltip);
            }
        }
    }

    public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip)
    {
        byte b0 = nbt.getByte("Type");

        if (b0 >= 0 && b0 <= 4)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + b0).trim());
        }
        else
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
        }

        int[] aint = nbt.getIntArray("Colors");

        if (aint.length > 0)
        {
            boolean flag = true;
            String s = "";

            for (int i : aint)
            {
                if (!flag)
                {
                    s = s + ", ";
                }

                flag = false;
                boolean flag1 = false;

                for (int j = 0; j < ItemDye.dyeColors.length; ++j)
                {
                    if (i == ItemDye.dyeColors[j])
                    {
                        flag1 = true;
                        s = s + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName());
                        break;
                    }
                }

                if (!flag1)
                {
                    s = s + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
            }

            tooltip.add(s);
        }

        int[] aint1 = nbt.getIntArray("FadeColors");

        if (aint1.length > 0)
        {
            boolean flag2 = true;
            String s1 = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";

            for (int l : aint1)
            {
                if (!flag2)
                {
                    s1 = s1 + ", ";
                }

                flag2 = false;
                boolean flag5 = false;

                for (int k = 0; k < 16; ++k)
                {
                    if (l == ItemDye.dyeColors[k])
                    {
                        flag5 = true;
                        s1 = s1 + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k).getUnlocalizedName());
                        break;
                    }
                }

                if (!flag5)
                {
                    s1 = s1 + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
            }

            tooltip.add(s1);
        }

        boolean flag3 = nbt.getBoolean("Trail");

        if (flag3)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
        }

        boolean flag4 = nbt.getBoolean("Flicker");

        if (flag4)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}
