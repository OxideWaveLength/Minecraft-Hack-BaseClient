package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube extends RenderLiving<EntityMagmaCube>
{
    private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

    public RenderMagmaCube(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelMagmaCube(), 0.25F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityMagmaCube entity)
    {
        return magmaCubeTextures;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityMagmaCube entitylivingbaseIn, float partialTickTime)
    {
        int i = entitylivingbaseIn.getSlimeSize();
        float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / ((float)i * 0.5F + 1.0F);
        float f1 = 1.0F / (f + 1.0F);
        float f2 = (float)i;
        GlStateManager.scale(f1 * f2, 1.0F / f1 * f2, f1 * f2);
    }
}
