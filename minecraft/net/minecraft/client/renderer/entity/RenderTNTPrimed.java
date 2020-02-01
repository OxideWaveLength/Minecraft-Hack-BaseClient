package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderTNTPrimed extends Render<EntityTNTPrimed>
{
    public RenderTNTPrimed(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(EntityTNTPrimed entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y + 0.5F, (float)z);

        if ((float)entity.fuse - partialTicks + 1.0F < 10.0F)
        {
            float f = 1.0F - ((float)entity.fuse - partialTicks + 1.0F) / 10.0F;
            f = MathHelper.clamp_float(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            GlStateManager.scale(f1, f1, f1);
        }

        float f2 = (1.0F - ((float)entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
        this.bindEntityTexture(entity);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(Blocks.tnt.getDefaultState(), entity.getBrightness(partialTicks));
        GlStateManager.translate(0.0F, 0.0F, 1.0F);

        if (entity.fuse / 5 % 2 == 0)
        {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 772);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            blockrendererdispatcher.renderBlockBrightness(Blocks.tnt.getDefaultState(), 1.0F);
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTNTPrimed entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}
