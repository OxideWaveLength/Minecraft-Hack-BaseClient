package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inv.getSizeInventory(); ++j)
        {
            ItemStack itemstack1 = inv.getStackInSlot(j);

            if (itemstack1 != null)
            {
                if (itemstack1.getItem() == Items.written_book)
                {
                    if (itemstack != null)
                    {
                        return false;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.getItem() != Items.writable_book)
                    {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inv.getSizeInventory(); ++j)
        {
            ItemStack itemstack1 = inv.getStackInSlot(j);

            if (itemstack1 != null)
            {
                if (itemstack1.getItem() == Items.written_book)
                {
                    if (itemstack != null)
                    {
                        return null;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if (itemstack1.getItem() != Items.writable_book)
                    {
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1 && ItemEditableBook.getGeneration(itemstack) < 2)
        {
            ItemStack itemstack2 = new ItemStack(Items.written_book, i);
            itemstack2.setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
            itemstack2.getTagCompound().setInteger("generation", ItemEditableBook.getGeneration(itemstack) + 1);

            if (itemstack.hasDisplayName())
            {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }

            return itemstack2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 9;
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

            if (itemstack != null && itemstack.getItem() instanceof ItemEditableBook)
            {
                aitemstack[i] = itemstack;
                break;
            }
        }

        return aitemstack;
    }
}
