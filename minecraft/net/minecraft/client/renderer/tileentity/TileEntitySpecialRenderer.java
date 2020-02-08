package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class TileEntitySpecialRenderer<T extends TileEntity> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] { new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png") };
	protected TileEntityRendererDispatcher rendererDispatcher;

	public abstract void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage);

	protected void bindTexture(ResourceLocation location) {
		TextureManager texturemanager = this.rendererDispatcher.renderEngine;

		if (texturemanager != null) {
			texturemanager.bindTexture(location);
		}
	}

	protected World getWorld() {
		return this.rendererDispatcher.worldObj;
	}

	public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
		this.rendererDispatcher = rendererDispatcherIn;
	}

	public FontRenderer getFontRenderer() {
		return this.rendererDispatcher.getFontRenderer();
	}

	public boolean func_181055_a() {
		return false;
	}
}
