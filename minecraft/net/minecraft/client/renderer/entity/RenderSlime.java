package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerSlimeGel;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class RenderSlime extends RenderLiving<EntitySlime>
{
    private static final ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");

    public RenderSlime(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        this.addLayer(new LayerSlimeGel(this));
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(EntitySlime entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.shadowSize = 0.25F * (float)entity.getSlimeSize();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntitySlime entitylivingbaseIn, float partialTickTime)
    {
        float f = (float)entitylivingbaseIn.getSlimeSize();
        float f1 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f * 0.5F + 1.0F);
        float f2 = 1.0F / (f1 + 1.0F);
        GlStateManager.scale(f2 * f, 1.0F / f2 * f, f2 * f);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySlime entity)
    {
        return slimeTextures;
    }
}
