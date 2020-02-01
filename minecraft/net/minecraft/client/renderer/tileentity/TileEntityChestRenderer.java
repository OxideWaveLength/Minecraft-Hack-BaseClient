package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest>
{
    private static final ResourceLocation textureTrappedDouble = new ResourceLocation("textures/entity/chest/trapped_double.png");
    private static final ResourceLocation textureChristmasDouble = new ResourceLocation("textures/entity/chest/christmas_double.png");
    private static final ResourceLocation textureNormalDouble = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation textureTrapped = new ResourceLocation("textures/entity/chest/trapped.png");
    private static final ResourceLocation textureChristmas = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation textureNormal = new ResourceLocation("textures/entity/chest/normal.png");
    private ModelChest simpleChest = new ModelChest();
    private ModelChest largeChest = new ModelLargeChest();
    private boolean isChristams;

    public TileEntityChestRenderer()
    {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
        {
            this.isChristams = true;
        }
    }

    public void renderTileEntityAt(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        int i;

        if (!te.hasWorldObj())
        {
            i = 0;
        }
        else
        {
            Block block = te.getBlockType();
            i = te.getBlockMetadata();

            if (block instanceof BlockChest && i == 0)
            {
                ((BlockChest)block).checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
                i = te.getBlockMetadata();
            }

            te.checkForAdjacentChests();
        }

        if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null)
        {
            ModelChest modelchest;

            if (te.adjacentChestXPos == null && te.adjacentChestZPos == null)
            {
                modelchest = this.simpleChest;

                if (destroyStage >= 0)
                {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(5888);
                }
                else if (te.getChestType() == 1)
                {
                    this.bindTexture(textureTrapped);
                }
                else if (this.isChristams)
                {
                    this.bindTexture(textureChristmas);
                }
                else
                {
                    this.bindTexture(textureNormal);
                }
            }
            else
            {
                modelchest = this.largeChest;

                if (destroyStage >= 0)
                {
                    this.bindTexture(DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(5888);
                }
                else if (te.getChestType() == 1)
                {
                    this.bindTexture(textureTrappedDouble);
                }
                else if (this.isChristams)
                {
                    this.bindTexture(textureChristmasDouble);
                }
                else
                {
                    this.bindTexture(textureNormalDouble);
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            if (destroyStage < 0)
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            int j = 0;

            if (i == 2)
            {
                j = 180;
            }

            if (i == 3)
            {
                j = 0;
            }

            if (i == 4)
            {
                j = 90;
            }

            if (i == 5)
            {
                j = -90;
            }

            if (i == 2 && te.adjacentChestXPos != null)
            {
                GlStateManager.translate(1.0F, 0.0F, 0.0F);
            }

            if (i == 5 && te.adjacentChestZPos != null)
            {
                GlStateManager.translate(0.0F, 0.0F, -1.0F);
            }

            GlStateManager.rotate((float)j, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

            if (te.adjacentChestZNeg != null)
            {
                float f1 = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;

                if (f1 > f)
                {
                    f = f1;
                }
            }

            if (te.adjacentChestXNeg != null)
            {
                float f2 = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;

                if (f2 > f)
                {
                    f = f2;
                }
            }

            f = 1.0F - f;
            f = 1.0F - f * f * f;
            modelchest.chestLid.rotateAngleX = -(f * (float)Math.PI / 2.0F);
            modelchest.renderAll();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (destroyStage >= 0)
            {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }
        }
    }
}
