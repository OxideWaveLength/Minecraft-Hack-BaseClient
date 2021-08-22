package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ChestRenderer {
	public void renderChestBrightness(Block p_178175_1_, float color) {
		GlStateManager.color(color, color, color, 1.0F);
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		TileEntityItemStackRenderer.instance.renderByItem(new ItemStack(p_178175_1_));
	}
}
