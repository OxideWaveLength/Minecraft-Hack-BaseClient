package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer extends TileEntitySpecialRenderer<TileEntityBanner>
{
    private static final Map<String, TileEntityBannerRenderer.TimedBannerTexture> DESIGNS = Maps.<String, TileEntityBannerRenderer.TimedBannerTexture>newHashMap();
    private static final ResourceLocation BANNERTEXTURES = new ResourceLocation("textures/entity/banner_base.png");
    private ModelBanner bannerModel = new ModelBanner();

    public void renderTileEntityAt(TileEntityBanner te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        boolean flag = te.getWorld() != null;
        boolean flag1 = !flag || te.getBlockType() == Blocks.standing_banner;
        int i = flag ? te.getBlockMetadata() : 0;
        long j = flag ? te.getWorld().getTotalWorldTime() : 0L;
        GlStateManager.pushMatrix();
        float f = 0.6666667F;

        if (flag1)
        {
            GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
            float f1 = (float)(i * 360) / 16.0F;
            GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
            this.bannerModel.bannerStand.showModel = true;
        }
        else
        {
            float f2 = 0.0F;

            if (i == 2)
            {
                f2 = 180.0F;
            }

            if (i == 4)
            {
                f2 = 90.0F;
            }

            if (i == 5)
            {
                f2 = -90.0F;
            }

            GlStateManager.translate((float)x + 0.5F, (float)y - 0.25F * f, (float)z + 0.5F);
            GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            this.bannerModel.bannerStand.showModel = false;
        }

        BlockPos blockpos = te.getPos();
        float f3 = (float)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + (float)j + partialTicks;
        this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * (float)Math.PI * 0.02F)) * (float)Math.PI;
        GlStateManager.enableRescaleNormal();
        ResourceLocation resourcelocation = this.func_178463_a(te);

        if (resourcelocation != null)
        {
            this.bindTexture(resourcelocation);
            GlStateManager.pushMatrix();
            GlStateManager.scale(f, -f, -f);
            this.bannerModel.renderBanner();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private ResourceLocation func_178463_a(TileEntityBanner bannerObj)
    {
        String s = bannerObj.func_175116_e();

        if (s.isEmpty())
        {
            return null;
        }
        else
        {
            TileEntityBannerRenderer.TimedBannerTexture tileentitybannerrenderer$timedbannertexture = (TileEntityBannerRenderer.TimedBannerTexture)DESIGNS.get(s);

            if (tileentitybannerrenderer$timedbannertexture == null)
            {
                if (DESIGNS.size() >= 256)
                {
                    long i = System.currentTimeMillis();
                    Iterator<String> iterator = DESIGNS.keySet().iterator();

                    while (iterator.hasNext())
                    {
                        String s1 = (String)iterator.next();
                        TileEntityBannerRenderer.TimedBannerTexture tileentitybannerrenderer$timedbannertexture1 = (TileEntityBannerRenderer.TimedBannerTexture)DESIGNS.get(s1);

                        if (i - tileentitybannerrenderer$timedbannertexture1.systemTime > 60000L)
                        {
                            Minecraft.getMinecraft().getTextureManager().deleteTexture(tileentitybannerrenderer$timedbannertexture1.bannerTexture);
                            iterator.remove();
                        }
                    }

                    if (DESIGNS.size() >= 256)
                    {
                        return null;
                    }
                }

                List<TileEntityBanner.EnumBannerPattern> list1 = bannerObj.getPatternList();
                List<EnumDyeColor> list = bannerObj.getColorList();
                List<String> list2 = Lists.<String>newArrayList();

                for (TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern : list1)
                {
                    list2.add("textures/entity/banner/" + tileentitybanner$enumbannerpattern.getPatternName() + ".png");
                }

                tileentitybannerrenderer$timedbannertexture = new TileEntityBannerRenderer.TimedBannerTexture();
                tileentitybannerrenderer$timedbannertexture.bannerTexture = new ResourceLocation(s);
                Minecraft.getMinecraft().getTextureManager().loadTexture(tileentitybannerrenderer$timedbannertexture.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, list2, list));
                DESIGNS.put(s, tileentitybannerrenderer$timedbannertexture);
            }

            tileentitybannerrenderer$timedbannertexture.systemTime = System.currentTimeMillis();
            return tileentitybannerrenderer$timedbannertexture.bannerTexture;
        }
    }

    static class TimedBannerTexture
    {
        public long systemTime;
        public ResourceLocation bannerTexture;

        private TimedBannerTexture()
        {
        }
    }
}
