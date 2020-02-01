package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot
{
    /** Merchant's inventory. */
    private final InventoryMerchant theMerchantInventory;

    /** The Player whos trying to buy/sell stuff. */
    private EntityPlayer thePlayer;
    private int field_75231_g;

    /** "Instance" of the Merchant. */
    private final IMerchant theMerchant;

    public SlotMerchantResult(EntityPlayer player, IMerchant merchant, InventoryMerchant merchantInventory, int slotIndex, int xPosition, int yPosition)
    {
        super(merchantInventory, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
        this.theMerchant = merchant;
        this.theMerchantInventory = merchantInventory;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.field_75231_g += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount)
    {
        this.field_75231_g += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack)
    {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
        this.field_75231_g = 0;
    }

    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        this.onCrafting(stack);
        MerchantRecipe merchantrecipe = this.theMerchantInventory.getCurrentRecipe();

        if (merchantrecipe != null)
        {
            ItemStack itemstack = this.theMerchantInventory.getStackInSlot(0);
            ItemStack itemstack1 = this.theMerchantInventory.getStackInSlot(1);

            if (this.doTrade(merchantrecipe, itemstack, itemstack1) || this.doTrade(merchantrecipe, itemstack1, itemstack))
            {
                this.theMerchant.useRecipe(merchantrecipe);
                playerIn.triggerAchievement(StatList.timesTradedWithVillagerStat);

                if (itemstack != null && itemstack.stackSize <= 0)
                {
                    itemstack = null;
                }

                if (itemstack1 != null && itemstack1.stackSize <= 0)
                {
                    itemstack1 = null;
                }

                this.theMerchantInventory.setInventorySlotContents(0, itemstack);
                this.theMerchantInventory.setInventorySlotContents(1, itemstack1);
            }
        }
    }

    private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem)
    {
        ItemStack itemstack = trade.getItemToBuy();
        ItemStack itemstack1 = trade.getSecondItemToBuy();

        if (firstItem != null && firstItem.getItem() == itemstack.getItem())
        {
            if (itemstack1 != null && secondItem != null && itemstack1.getItem() == secondItem.getItem())
            {
                firstItem.stackSize -= itemstack.stackSize;
                secondItem.stackSize -= itemstack1.stackSize;
                return true;
            }

            if (itemstack1 == null && secondItem == null)
            {
                firstItem.stackSize -= itemstack.stackSize;
                return true;
            }
        }

        return false;
    }
}
