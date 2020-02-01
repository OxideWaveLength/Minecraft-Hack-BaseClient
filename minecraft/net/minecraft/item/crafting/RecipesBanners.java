package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.world.World;

public class RecipesBanners
{
    /**
     * Adds the banner recipes to the CraftingManager.
     */
    void addRecipes(CraftingManager p_179534_1_)
    {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values())
        {
            p_179534_1_.addRecipe(new ItemStack(Items.banner, 1, enumdyecolor.getDyeDamage()), new Object[] {"###", "###", " | ", '#', new ItemStack(Blocks.wool, 1, enumdyecolor.getMetadata()), '|', Items.stick});
        }

        p_179534_1_.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
        p_179534_1_.addRecipe(new RecipesBanners.RecipeAddPattern());
    }

    static class RecipeAddPattern implements IRecipe
    {
        private RecipeAddPattern()
        {
        }

        public boolean matches(InventoryCrafting inv, World worldIn)
        {
            boolean flag = false;

            for (int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);

                if (itemstack != null && itemstack.getItem() == Items.banner)
                {
                    if (flag)
                    {
                        return false;
                    }

                    if (TileEntityBanner.getPatterns(itemstack) >= 6)
                    {
                        return false;
                    }

                    flag = true;
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                return this.func_179533_c(inv) != null;
            }
        }

        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            ItemStack itemstack = null;

            for (int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack itemstack1 = inv.getStackInSlot(i);

                if (itemstack1 != null && itemstack1.getItem() == Items.banner)
                {
                    itemstack = itemstack1.copy();
                    itemstack.stackSize = 1;
                    break;
                }
            }

            TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern = this.func_179533_c(inv);

            if (tileentitybanner$enumbannerpattern != null)
            {
                int k = 0;

                for (int j = 0; j < inv.getSizeInventory(); ++j)
                {
                    ItemStack itemstack2 = inv.getStackInSlot(j);

                    if (itemstack2 != null && itemstack2.getItem() == Items.dye)
                    {
                        k = itemstack2.getMetadata();
                        break;
                    }
                }

                NBTTagCompound nbttagcompound1 = itemstack.getSubCompound("BlockEntityTag", true);
                NBTTagList nbttaglist = null;

                if (nbttagcompound1.hasKey("Patterns", 9))
                {
                    nbttaglist = nbttagcompound1.getTagList("Patterns", 10);
                }
                else
                {
                    nbttaglist = new NBTTagList();
                    nbttagcompound1.setTag("Patterns", nbttaglist);
                }

                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setString("Pattern", tileentitybanner$enumbannerpattern.getPatternID());
                nbttagcompound.setInteger("Color", k);
                nbttaglist.appendTag(nbttagcompound);
            }

            return itemstack;
        }

        public int getRecipeSize()
        {
            return 10;
        }

        public ItemStack getRecipeOutput()
        {
            return null;
        }

        public ItemStack[] getRemainingItems(InventoryCrafting inv)
        {
            ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

            for (int i = 0; i < aitemstack.length; ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);

                if (itemstack != null && itemstack.getItem().hasContainerItem())
                {
                    aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
                }
            }

            return aitemstack;
        }

        private TileEntityBanner.EnumBannerPattern func_179533_c(InventoryCrafting p_179533_1_)
        {
            for (TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern : TileEntityBanner.EnumBannerPattern.values())
            {
                if (tileentitybanner$enumbannerpattern.hasValidCrafting())
                {
                    boolean flag = true;

                    if (tileentitybanner$enumbannerpattern.hasCraftingStack())
                    {
                        boolean flag1 = false;
                        boolean flag2 = false;

                        for (int i = 0; i < p_179533_1_.getSizeInventory() && flag; ++i)
                        {
                            ItemStack itemstack = p_179533_1_.getStackInSlot(i);

                            if (itemstack != null && itemstack.getItem() != Items.banner)
                            {
                                if (itemstack.getItem() == Items.dye)
                                {
                                    if (flag2)
                                    {
                                        flag = false;
                                        break;
                                    }

                                    flag2 = true;
                                }
                                else
                                {
                                    if (flag1 || !itemstack.isItemEqual(tileentitybanner$enumbannerpattern.getCraftingStack()))
                                    {
                                        flag = false;
                                        break;
                                    }

                                    flag1 = true;
                                }
                            }
                        }

                        if (!flag1)
                        {
                            flag = false;
                        }
                    }
                    else if (p_179533_1_.getSizeInventory() == tileentitybanner$enumbannerpattern.getCraftingLayers().length * tileentitybanner$enumbannerpattern.getCraftingLayers()[0].length())
                    {
                        int j = -1;

                        for (int k = 0; k < p_179533_1_.getSizeInventory() && flag; ++k)
                        {
                            int l = k / 3;
                            int i1 = k % 3;
                            ItemStack itemstack1 = p_179533_1_.getStackInSlot(k);

                            if (itemstack1 != null && itemstack1.getItem() != Items.banner)
                            {
                                if (itemstack1.getItem() != Items.dye)
                                {
                                    flag = false;
                                    break;
                                }

                                if (j != -1 && j != itemstack1.getMetadata())
                                {
                                    flag = false;
                                    break;
                                }

                                if (tileentitybanner$enumbannerpattern.getCraftingLayers()[l].charAt(i1) == 32)
                                {
                                    flag = false;
                                    break;
                                }

                                j = itemstack1.getMetadata();
                            }
                            else if (tileentitybanner$enumbannerpattern.getCraftingLayers()[l].charAt(i1) != 32)
                            {
                                flag = false;
                                break;
                            }
                        }
                    }
                    else
                    {
                        flag = false;
                    }

                    if (flag)
                    {
                        return tileentitybanner$enumbannerpattern;
                    }
                }
            }

            return null;
        }
    }

    static class RecipeDuplicatePattern implements IRecipe
    {
        private RecipeDuplicatePattern()
        {
        }

        public boolean matches(InventoryCrafting inv, World worldIn)
        {
            ItemStack itemstack = null;
            ItemStack itemstack1 = null;

            for (int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack itemstack2 = inv.getStackInSlot(i);

                if (itemstack2 != null)
                {
                    if (itemstack2.getItem() != Items.banner)
                    {
                        return false;
                    }

                    if (itemstack != null && itemstack1 != null)
                    {
                        return false;
                    }

                    int j = TileEntityBanner.getBaseColor(itemstack2);
                    boolean flag = TileEntityBanner.getPatterns(itemstack2) > 0;

                    if (itemstack != null)
                    {
                        if (flag)
                        {
                            return false;
                        }

                        if (j != TileEntityBanner.getBaseColor(itemstack))
                        {
                            return false;
                        }

                        itemstack1 = itemstack2;
                    }
                    else if (itemstack1 != null)
                    {
                        if (!flag)
                        {
                            return false;
                        }

                        if (j != TileEntityBanner.getBaseColor(itemstack1))
                        {
                            return false;
                        }

                        itemstack = itemstack2;
                    }
                    else if (flag)
                    {
                        itemstack = itemstack2;
                    }
                    else
                    {
                        itemstack1 = itemstack2;
                    }
                }
            }

            return itemstack != null && itemstack1 != null;
        }

        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            for (int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);

                if (itemstack != null && TileEntityBanner.getPatterns(itemstack) > 0)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    itemstack1.stackSize = 1;
                    return itemstack1;
                }
            }

            return null;
        }

        public int getRecipeSize()
        {
            return 2;
        }

        public ItemStack getRecipeOutput()
        {
            return null;
        }

        public ItemStack[] getRemainingItems(InventoryCrafting inv)
        {
            ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

            for (int i = 0; i < aitemstack.length; ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);

                if (itemstack != null)
                {
                    if (itemstack.getItem().hasContainerItem())
                    {
                        aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
                    }
                    else if (itemstack.hasTagCompound() && TileEntityBanner.getPatterns(itemstack) > 0)
                    {
                        aitemstack[i] = itemstack.copy();
                        aitemstack[i].stackSize = 1;
                    }
                }
            }

            return aitemstack;
        }
    }
}
