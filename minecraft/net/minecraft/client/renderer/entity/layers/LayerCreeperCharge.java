package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class LayerCreeperCharge implements LayerRenderer<EntityCreeper>
{
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final RenderCreeper creeperRenderer;
    private final ModelCreeper creeperModel = new ModelCreeper(2.0F);

    public LayerCreeperCharge(RenderCreeper creeperRendererIn)
    {
        this.creeperRenderer = creeperRendererIn;
    }

    public void doRenderLayer(EntityCreeper entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (entitylivingbaseIn.getPowered())
        {
            boolean flag = entitylivingbaseIn.isInvisible();
            GlStateManager.depthMask(!flag);
            this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
            GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            float f1 = 0.5F;
            GlStateManager.color(f1, f1, f1, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(1, 1);
            this.creeperModel.setModelAttributes(this.creeperRenderer.getMainModel());
            this.creeperModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(flag);
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
