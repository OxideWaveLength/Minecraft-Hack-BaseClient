package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItemWitch implements LayerRenderer<EntityWitch>
{
    private final RenderWitch witchRenderer;

    public LayerHeldItemWitch(RenderWitch witchRendererIn)
    {
        this.witchRenderer = witchRendererIn;
    }

    public void doRenderLayer(EntityWitch entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        ItemStack itemstack = entitylivingbaseIn.getHeldItem();

        if (itemstack != null)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();

            if (this.witchRenderer.getMainModel().isChild)
            {
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                float f = 0.5F;
                GlStateManager.scale(f, f, f);
            }

            ((ModelWitch)this.witchRenderer.getMainModel()).villagerNose.postRender(0.0625F);
            GlStateManager.translate(-0.0625F, 0.53125F, 0.21875F);
            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (item instanceof ItemBlock && minecraft.getBlockRendererDispatcher().isRenderTypeChest(Block.getBlockFromItem(item), itemstack.getMetadata()))
            {
                GlStateManager.translate(0.0F, 0.0625F, -0.25F);
                GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
                float f4 = 0.375F;
                GlStateManager.scale(f4, -f4, f4);
            }
            else if (item == Items.bow)
            {
                GlStateManager.translate(0.0F, 0.125F, -0.125F);
                GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
                float f1 = 0.625F;
                GlStateManager.scale(f1, -f1, f1);
                GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (item.isFull3D())
            {
                if (item.shouldRotateAroundWhenRendering())
                {
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(0.0F, -0.0625F, 0.0F);
                }

                this.witchRenderer.transformHeldFull3DItemLayer();
                GlStateManager.translate(0.0625F, -0.125F, 0.0F);
                float f2 = 0.625F;
                GlStateManager.scale(f2, -f2, f2);
                GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                GlStateManager.translate(0.1875F, 0.1875F, 0.0F);
                float f3 = 0.875F;
                GlStateManager.scale(f3, f3, f3);
                GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-60.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(40.0F, 0.0F, 0.0F, 1.0F);
            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
