package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

public class RenderWitch extends RenderLiving<EntityWitch> {
	private static final ResourceLocation witchTextures = new ResourceLocation("textures/entity/witch.png");

	public RenderWitch(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelWitch(0.0F), 0.5F);
		this.addLayer(new LayerHeldItemWitch(this));
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker function
	 * which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void
	 * doRender(T entity, double d, double d1, double d2, float f, float f1). But
	 * JAD is pre 1.5 so doe
	 */
	public void doRender(EntityWitch entity, double x, double y, double z, float entityYaw, float partialTicks) {
		((ModelWitch) this.mainModel).field_82900_g = entity.getHeldItem() != null;
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless
	 * you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityWitch entity) {
		return witchTextures;
	}

	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.0F, 0.1875F, 0.0F);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the
	 * model is rendered. Args: entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityWitch entitylivingbaseIn, float partialTickTime) {
		float f = 0.9375F;
		GlStateManager.scale(f, f, f);
	}
}
