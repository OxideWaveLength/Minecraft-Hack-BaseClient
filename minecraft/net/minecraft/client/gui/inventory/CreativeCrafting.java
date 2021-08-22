package net.minecraft.client.gui.inventory;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CreativeCrafting implements ICrafting {
	private final Minecraft mc;

	public CreativeCrafting(Minecraft mc) {
		this.mc = mc;
	}

	/**
	 * update the crafting window inventory with the items in the list
	 */
	public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
	}

	/**
	 * Sends the contents of an inventory slot to the client-side Container. This
	 * doesn't have to match the actual contents of that slot. Args: Container, slot
	 * number, slot contents
	 */
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
		this.mc.playerController.sendSlotPacket(stack, slotInd);
	}

	/**
	 * Sends two ints to the client-side Container. Used for furnace burning time,
	 * smelting progress, brewing progress, and enchanting level. Normally the first
	 * int identifies which variable to update, and the second contains the new
	 * value. Both are truncated to shorts in non-local SMP.
	 */
	public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
	}

	public void func_175173_a(Container p_175173_1_, IInventory p_175173_2_) {
	}
}
