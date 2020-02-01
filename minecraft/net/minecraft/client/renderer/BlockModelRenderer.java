package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;
import optfine.BetterGrass;
import optfine.BetterSnow;
import optfine.Config;
import optfine.ConnectedTextures;
import optfine.CustomColorizer;
import optfine.NaturalTextures;
import optfine.RenderEnv;

public class BlockModelRenderer
{
    private static final String __OBFID = "CL_00002518";
    private static float aoLightValueOpaque = 0.2F;

    public static void updateAoLightValue()
    {
        aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
    }

    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn)
    {
        Block block = blockStateIn.getBlock();
        block.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
        return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
    }

    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();

        try
        {
            Block block = blockStateIn.getBlock();
            return flag ? this.renderModelAmbientOcclusion(blockAccessIn, modelIn, block, blockStateIn, blockPosIn, worldRendererIn, checkSides) : this.renderModelStandard(blockAccessIn, modelIn, block, blockStateIn, blockPosIn, worldRendererIn, checkSides);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, blockPosIn, blockStateIn);
            crashreportcategory.addCrashSection("Using AO", Boolean.valueOf(flag));
            throw new ReportedException(crashreport);
        }
    }

    public boolean renderModelAmbientOcclusion(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        return this.renderModelAmbientOcclusion(blockAccessIn, modelIn, blockIn, blockAccessIn.getBlockState(blockPosIn), blockPosIn, worldRendererIn, checkSides);
    }

    public boolean renderModelAmbientOcclusion(IBlockAccess p_renderModelAmbientOcclusion_1_, IBakedModel p_renderModelAmbientOcclusion_2_, Block p_renderModelAmbientOcclusion_3_, IBlockState p_renderModelAmbientOcclusion_4_, BlockPos p_renderModelAmbientOcclusion_5_, WorldRenderer p_renderModelAmbientOcclusion_6_, boolean p_renderModelAmbientOcclusion_7_)
    {
        boolean flag = false;
        RenderEnv renderenv = null;

        for (EnumFacing enumfacing : EnumFacing.VALUES)
        {
            List list = p_renderModelAmbientOcclusion_2_.getFaceQuads(enumfacing);

            if (!list.isEmpty())
            {
                BlockPos blockpos = p_renderModelAmbientOcclusion_5_.offset(enumfacing);

                if (!p_renderModelAmbientOcclusion_7_ || p_renderModelAmbientOcclusion_3_.shouldSideBeRendered(p_renderModelAmbientOcclusion_1_, blockpos, enumfacing))
                {
                    if (renderenv == null)
                    {
                        renderenv = RenderEnv.getInstance(p_renderModelAmbientOcclusion_1_, p_renderModelAmbientOcclusion_4_, p_renderModelAmbientOcclusion_5_);
                    }

                    if (!renderenv.isBreakingAnimation(list) && Config.isBetterGrass())
                    {
                        list = BetterGrass.getFaceQuads(p_renderModelAmbientOcclusion_1_, p_renderModelAmbientOcclusion_3_, p_renderModelAmbientOcclusion_5_, enumfacing, list);
                    }

                    this.renderModelAmbientOcclusionQuads(p_renderModelAmbientOcclusion_1_, p_renderModelAmbientOcclusion_3_, p_renderModelAmbientOcclusion_5_, p_renderModelAmbientOcclusion_6_, list, renderenv);
                    flag = true;
                }
            }
        }

        List list1 = p_renderModelAmbientOcclusion_2_.getGeneralQuads();

        if (list1.size() > 0)
        {
            if (renderenv == null)
            {
                renderenv = RenderEnv.getInstance(p_renderModelAmbientOcclusion_1_, p_renderModelAmbientOcclusion_4_, p_renderModelAmbientOcclusion_5_);
            }

            this.renderModelAmbientOcclusionQuads(p_renderModelAmbientOcclusion_1_, p_renderModelAmbientOcclusion_3_, p_renderModelAmbientOcclusion_5_, p_renderModelAmbientOcclusion_6_, list1, renderenv);
            flag = true;
        }

        if (renderenv != null && Config.isBetterSnow() && !renderenv.isBreakingAnimation() && BetterSnow.shouldRender(p_renderModelAmbientOcclusion_1_, p_renderModelAmbientOcclusion_3_, p_renderModelAmbientOcclusion_4_, p_renderModelAmbientOcclusion_5_))
        {
            IBakedModel ibakedmodel = BetterSnow.getModelSnowLayer();
            IBlockState iblockstate = BetterSnow.getStateSnowLayer();
            this.renderModelAmbientOcclusion(p_renderModelAmbientOcclusion_1_, ibakedmodel, iblockstate.getBlock(), iblockstate, p_renderModelAmbientOcclusion_5_, p_renderModelAmbientOcclusion_6_, true);
        }

        return flag;
    }

    public boolean renderModelStandard(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        return this.renderModelStandard(blockAccessIn, modelIn, blockIn, blockAccessIn.getBlockState(blockPosIn), blockPosIn, worldRendererIn, checkSides);
    }

    public boolean renderModelStandard(IBlockAccess p_renderModelStandard_1_, IBakedModel p_renderModelStandard_2_, Block p_renderModelStandard_3_, IBlockState p_renderModelStandard_4_, BlockPos p_renderModelStandard_5_, WorldRenderer p_renderModelStandard_6_, boolean p_renderModelStandard_7_)
    {
        boolean flag = false;
        RenderEnv renderenv = null;

        for (EnumFacing enumfacing : EnumFacing.VALUES)
        {
            List list = p_renderModelStandard_2_.getFaceQuads(enumfacing);

            if (!list.isEmpty())
            {
                BlockPos blockpos = p_renderModelStandard_5_.offset(enumfacing);

                if (!p_renderModelStandard_7_ || p_renderModelStandard_3_.shouldSideBeRendered(p_renderModelStandard_1_, blockpos, enumfacing))
                {
                    if (renderenv == null)
                    {
                        renderenv = RenderEnv.getInstance(p_renderModelStandard_1_, p_renderModelStandard_4_, p_renderModelStandard_5_);
                    }

                    if (!renderenv.isBreakingAnimation(list) && Config.isBetterGrass())
                    {
                        list = BetterGrass.getFaceQuads(p_renderModelStandard_1_, p_renderModelStandard_3_, p_renderModelStandard_5_, enumfacing, list);
                    }

                    int i = p_renderModelStandard_3_.getMixedBrightnessForBlock(p_renderModelStandard_1_, blockpos);
                    this.renderModelStandardQuads(p_renderModelStandard_1_, p_renderModelStandard_3_, p_renderModelStandard_5_, enumfacing, i, false, p_renderModelStandard_6_, list, renderenv);
                    flag = true;
                }
            }
        }

        List list1 = p_renderModelStandard_2_.getGeneralQuads();

        if (list1.size() > 0)
        {
            if (renderenv == null)
            {
                renderenv = RenderEnv.getInstance(p_renderModelStandard_1_, p_renderModelStandard_4_, p_renderModelStandard_5_);
            }

            this.renderModelStandardQuads(p_renderModelStandard_1_, p_renderModelStandard_3_, p_renderModelStandard_5_, (EnumFacing)null, -1, true, p_renderModelStandard_6_, list1, renderenv);
            flag = true;
        }

        if (renderenv != null && Config.isBetterSnow() && !renderenv.isBreakingAnimation() && BetterSnow.shouldRender(p_renderModelStandard_1_, p_renderModelStandard_3_, p_renderModelStandard_4_, p_renderModelStandard_5_) && BetterSnow.shouldRender(p_renderModelStandard_1_, p_renderModelStandard_3_, p_renderModelStandard_4_, p_renderModelStandard_5_))
        {
            IBakedModel ibakedmodel = BetterSnow.getModelSnowLayer();
            IBlockState iblockstate = BetterSnow.getStateSnowLayer();
            this.renderModelStandard(p_renderModelStandard_1_, ibakedmodel, iblockstate.getBlock(), iblockstate, p_renderModelStandard_5_, p_renderModelStandard_6_, true);
        }

        return flag;
    }

    private void renderModelAmbientOcclusionQuads(IBlockAccess p_renderModelAmbientOcclusionQuads_1_, Block p_renderModelAmbientOcclusionQuads_2_, BlockPos p_renderModelAmbientOcclusionQuads_3_, WorldRenderer p_renderModelAmbientOcclusionQuads_4_, List p_renderModelAmbientOcclusionQuads_5_, RenderEnv p_renderModelAmbientOcclusionQuads_6_)
    {
        float[] afloat = p_renderModelAmbientOcclusionQuads_6_.getQuadBounds();
        BitSet bitset = p_renderModelAmbientOcclusionQuads_6_.getBoundsFlags();
        BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = p_renderModelAmbientOcclusionQuads_6_.getAoFace();
        IBlockState iblockstate = p_renderModelAmbientOcclusionQuads_6_.getBlockState();
        double d0 = (double)p_renderModelAmbientOcclusionQuads_3_.getX();
        double d1 = (double)p_renderModelAmbientOcclusionQuads_3_.getY();
        double d2 = (double)p_renderModelAmbientOcclusionQuads_3_.getZ();
        Block.EnumOffsetType block$enumoffsettype = p_renderModelAmbientOcclusionQuads_2_.getOffsetType();

        if (block$enumoffsettype != Block.EnumOffsetType.NONE)
        {
            long i = MathHelper.getPositionRandom(p_renderModelAmbientOcclusionQuads_3_);
            d0 += ((double)((float)(i >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d2 += ((double)((float)(i >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

            if (block$enumoffsettype == Block.EnumOffsetType.XYZ)
            {
                d1 += ((double)((float)(i >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            }
        }

        for (Object bakedquad0 : p_renderModelAmbientOcclusionQuads_5_)
        {
            BakedQuad bakedquad = (BakedQuad) bakedquad0;

            if (bakedquad.getSprite() != null)
            {
                BakedQuad bakedquad1 = bakedquad;

                if (Config.isConnectedTextures())
                {
                    bakedquad = ConnectedTextures.getConnectedTexture(p_renderModelAmbientOcclusionQuads_1_, iblockstate, p_renderModelAmbientOcclusionQuads_3_, bakedquad, p_renderModelAmbientOcclusionQuads_6_);
                }

                if (bakedquad == bakedquad1 && Config.isNaturalTextures())
                {
                    bakedquad = NaturalTextures.getNaturalTexture(p_renderModelAmbientOcclusionQuads_3_, bakedquad);
                }
            }

            this.fillQuadBounds(p_renderModelAmbientOcclusionQuads_2_, bakedquad.getVertexData(), bakedquad.getFace(), afloat, bitset);
            blockmodelrenderer$ambientocclusionface.updateVertexBrightness(p_renderModelAmbientOcclusionQuads_1_, p_renderModelAmbientOcclusionQuads_2_, p_renderModelAmbientOcclusionQuads_3_, bakedquad.getFace(), afloat, bitset);

            if (p_renderModelAmbientOcclusionQuads_4_.isMultiTexture())
            {
                p_renderModelAmbientOcclusionQuads_4_.addVertexData(bakedquad.getVertexDataSingle());
                p_renderModelAmbientOcclusionQuads_4_.putSprite(bakedquad.getSprite());
            }
            else
            {
                p_renderModelAmbientOcclusionQuads_4_.addVertexData(bakedquad.getVertexData());
            }

            p_renderModelAmbientOcclusionQuads_4_.putBrightness4(blockmodelrenderer$ambientocclusionface.vertexBrightness[0], blockmodelrenderer$ambientocclusionface.vertexBrightness[1], blockmodelrenderer$ambientocclusionface.vertexBrightness[2], blockmodelrenderer$ambientocclusionface.vertexBrightness[3]);
            int k = CustomColorizer.getColorMultiplier(bakedquad, p_renderModelAmbientOcclusionQuads_2_, p_renderModelAmbientOcclusionQuads_1_, p_renderModelAmbientOcclusionQuads_3_, p_renderModelAmbientOcclusionQuads_6_);

            if (!bakedquad.hasTintIndex() && k < 0)
            {
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0], 4);
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1], 3);
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2], 2);
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3], 1);
            }
            else
            {
                int j;

                if (k >= 0)
                {
                    j = k;
                }
                else
                {
                    j = p_renderModelAmbientOcclusionQuads_2_.colorMultiplier(p_renderModelAmbientOcclusionQuads_1_, p_renderModelAmbientOcclusionQuads_3_, bakedquad.getTintIndex());
                }

                if (EntityRenderer.anaglyphEnable)
                {
                    j = TextureUtil.anaglyphColor(j);
                }

                float f = (float)(j >> 16 & 255) / 255.0F;
                float f1 = (float)(j >> 8 & 255) / 255.0F;
                float f2 = (float)(j & 255) / 255.0F;
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[0] * f2, 4);
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[1] * f2, 3);
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[2] * f2, 2);
                p_renderModelAmbientOcclusionQuads_4_.putColorMultiplier(blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f1, blockmodelrenderer$ambientocclusionface.vertexColorMultiplier[3] * f2, 1);
            }

            p_renderModelAmbientOcclusionQuads_4_.putPosition(d0, d1, d2);
        }
    }

    private void fillQuadBounds(Block blockIn, int[] vertexData, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags)
    {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for (int i = 0; i < 4; ++i)
        {
            float f6 = Float.intBitsToFloat(vertexData[i * 7]);
            float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null)
        {
            quadBounds[EnumFacing.WEST.getIndex()] = f;
            quadBounds[EnumFacing.EAST.getIndex()] = f3;
            quadBounds[EnumFacing.DOWN.getIndex()] = f1;
            quadBounds[EnumFacing.UP.getIndex()] = f4;
            quadBounds[EnumFacing.NORTH.getIndex()] = f2;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
            quadBounds[EnumFacing.WEST.getIndex() + EnumFacing.VALUES.length] = 1.0F - f;
            quadBounds[EnumFacing.EAST.getIndex() + EnumFacing.VALUES.length] = 1.0F - f3;
            quadBounds[EnumFacing.DOWN.getIndex() + EnumFacing.VALUES.length] = 1.0F - f1;
            quadBounds[EnumFacing.UP.getIndex() + EnumFacing.VALUES.length] = 1.0F - f4;
            quadBounds[EnumFacing.NORTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - f2;
            quadBounds[EnumFacing.SOUTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - f5;
        }

        float f10 = 1.0E-4F;
        float f9 = 0.9999F;

        switch (BlockModelRenderer.BlockModelRenderer$1.field_178290_a[facingIn.ordinal()])
        {
            case 1:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f1 < 1.0E-4F || blockIn.isFullCube()) && f1 == f4);
                break;

            case 2:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f4 > 0.9999F || blockIn.isFullCube()) && f1 == f4);
                break;

            case 3:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f2 < 1.0E-4F || blockIn.isFullCube()) && f2 == f5);
                break;

            case 4:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f5 > 0.9999F || blockIn.isFullCube()) && f2 == f5);
                break;

            case 5:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f < 1.0E-4F || blockIn.isFullCube()) && f == f3);
                break;

            case 6:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f3 > 0.9999F || blockIn.isFullCube()) && f == f3);
        }
    }

    private void renderModelStandardQuads(IBlockAccess p_renderModelStandardQuads_1_, Block p_renderModelStandardQuads_2_, BlockPos p_renderModelStandardQuads_3_, EnumFacing p_renderModelStandardQuads_4_, int p_renderModelStandardQuads_5_, boolean p_renderModelStandardQuads_6_, WorldRenderer p_renderModelStandardQuads_7_, List p_renderModelStandardQuads_8_, RenderEnv p_renderModelStandardQuads_9_)
    {
        BitSet bitset = p_renderModelStandardQuads_9_.getBoundsFlags();
        IBlockState iblockstate = p_renderModelStandardQuads_9_.getBlockState();
        double d0 = (double)p_renderModelStandardQuads_3_.getX();
        double d1 = (double)p_renderModelStandardQuads_3_.getY();
        double d2 = (double)p_renderModelStandardQuads_3_.getZ();
        Block.EnumOffsetType block$enumoffsettype = p_renderModelStandardQuads_2_.getOffsetType();

        if (block$enumoffsettype != Block.EnumOffsetType.NONE)
        {
            int i = p_renderModelStandardQuads_3_.getX();
            int j = p_renderModelStandardQuads_3_.getZ();
            long k = (long)(i * 3129871) ^ (long)j * 116129781L;
            k = k * k * 42317861L + k * 11L;
            d0 += ((double)((float)(k >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d2 += ((double)((float)(k >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

            if (block$enumoffsettype == Block.EnumOffsetType.XYZ)
            {
                d1 += ((double)((float)(k >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            }
        }

        for (Object bakedquad0 : p_renderModelStandardQuads_8_)
        {
            BakedQuad bakedquad = (BakedQuad) bakedquad0;

            if (bakedquad.getSprite() != null)
            {
                BakedQuad bakedquad1 = bakedquad;

                if (Config.isConnectedTextures())
                {
                    bakedquad = ConnectedTextures.getConnectedTexture(p_renderModelStandardQuads_1_, iblockstate, p_renderModelStandardQuads_3_, bakedquad, p_renderModelStandardQuads_9_);
                }

                if (bakedquad == bakedquad1 && Config.isNaturalTextures())
                {
                    bakedquad = NaturalTextures.getNaturalTexture(p_renderModelStandardQuads_3_, bakedquad);
                }
            }

            if (p_renderModelStandardQuads_6_)
            {
                this.fillQuadBounds(p_renderModelStandardQuads_2_, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitset);
                p_renderModelStandardQuads_5_ = bitset.get(0) ? p_renderModelStandardQuads_2_.getMixedBrightnessForBlock(p_renderModelStandardQuads_1_, p_renderModelStandardQuads_3_.offset(bakedquad.getFace())) : p_renderModelStandardQuads_2_.getMixedBrightnessForBlock(p_renderModelStandardQuads_1_, p_renderModelStandardQuads_3_);
            }

            if (p_renderModelStandardQuads_7_.isMultiTexture())
            {
                p_renderModelStandardQuads_7_.addVertexData(bakedquad.getVertexDataSingle());
                p_renderModelStandardQuads_7_.putSprite(bakedquad.getSprite());
            }
            else
            {
                p_renderModelStandardQuads_7_.addVertexData(bakedquad.getVertexData());
            }

            p_renderModelStandardQuads_7_.putBrightness4(p_renderModelStandardQuads_5_, p_renderModelStandardQuads_5_, p_renderModelStandardQuads_5_, p_renderModelStandardQuads_5_);
            int i1 = CustomColorizer.getColorMultiplier(bakedquad, p_renderModelStandardQuads_2_, p_renderModelStandardQuads_1_, p_renderModelStandardQuads_3_, p_renderModelStandardQuads_9_);

            if (bakedquad.hasTintIndex() || i1 >= 0)
            {
                int l;

                if (i1 >= 0)
                {
                    l = i1;
                }
                else
                {
                    l = p_renderModelStandardQuads_2_.colorMultiplier(p_renderModelStandardQuads_1_, p_renderModelStandardQuads_3_, bakedquad.getTintIndex());
                }

                if (EntityRenderer.anaglyphEnable)
                {
                    l = TextureUtil.anaglyphColor(l);
                }

                float f = (float)(l >> 16 & 255) / 255.0F;
                float f1 = (float)(l >> 8 & 255) / 255.0F;
                float f2 = (float)(l & 255) / 255.0F;
                p_renderModelStandardQuads_7_.putColorMultiplier(f, f1, f2, 4);
                p_renderModelStandardQuads_7_.putColorMultiplier(f, f1, f2, 3);
                p_renderModelStandardQuads_7_.putColorMultiplier(f, f1, f2, 2);
                p_renderModelStandardQuads_7_.putColorMultiplier(f, f1, f2, 1);
            }

            p_renderModelStandardQuads_7_.putPosition(d0, d1, d2);
        }
    }

    public void renderModelBrightnessColor(IBakedModel bakedModel, float p_178262_2_, float p_178262_3_, float p_178262_4_, float p_178262_5_)
    {
        for (EnumFacing enumfacing : EnumFacing.VALUES)
        {
            this.renderModelBrightnessColorQuads(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, bakedModel.getFaceQuads(enumfacing));
        }

        this.renderModelBrightnessColorQuads(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, bakedModel.getGeneralQuads());
    }

    public void renderModelBrightness(IBakedModel p_178266_1_, IBlockState p_178266_2_, float p_178266_3_, boolean p_178266_4_)
    {
        Block block = p_178266_2_.getBlock();
        block.setBlockBoundsForItemRender();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        int i = block.getRenderColor(block.getStateForEntityRender(p_178266_2_));

        if (EntityRenderer.anaglyphEnable)
        {
            i = TextureUtil.anaglyphColor(i);
        }

        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;

        if (!p_178266_4_)
        {
            GlStateManager.color(p_178266_3_, p_178266_3_, p_178266_3_, 1.0F);
        }

        this.renderModelBrightnessColor(p_178266_1_, p_178266_3_, f, f1, f2);
    }

    private void renderModelBrightnessColorQuads(float p_178264_1_, float p_178264_2_, float p_178264_3_, float p_178264_4_, List p_178264_5_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        for (Object bakedquad0 : p_178264_5_)
        {
            BakedQuad bakedquad = (BakedQuad) bakedquad0;
            worldrenderer.begin(7, DefaultVertexFormats.ITEM);
            worldrenderer.addVertexData(bakedquad.getVertexData());

            if (bakedquad.hasTintIndex())
            {
                worldrenderer.putColorRGB_F4(p_178264_2_ * p_178264_1_, p_178264_3_ * p_178264_1_, p_178264_4_ * p_178264_1_);
            }
            else
            {
                worldrenderer.putColorRGB_F4(p_178264_1_, p_178264_1_, p_178264_1_);
            }

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            worldrenderer.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            tessellator.draw();
        }
    }

    public static float fixAoLightValue(float p_fixAoLightValue_0_)
    {
        return p_fixAoLightValue_0_ == 0.2F ? aoLightValueOpaque : p_fixAoLightValue_0_;
    }

    static final class BlockModelRenderer$1
    {
        static final int[] field_178290_a = new int[EnumFacing.values().length];
        private static final String __OBFID = "CL_00002517";

        static
        {
            try
            {
                field_178290_a[EnumFacing.DOWN.ordinal()] = 1;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                field_178290_a[EnumFacing.UP.ordinal()] = 2;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                field_178290_a[EnumFacing.NORTH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                field_178290_a[EnumFacing.SOUTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                field_178290_a[EnumFacing.WEST.ordinal()] = 5;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                field_178290_a[EnumFacing.EAST.ordinal()] = 6;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }

    public static class AmbientOcclusionFace
    {
        private final float[] vertexColorMultiplier = new float[4];
        private final int[] vertexBrightness = new int[4];
        private static final String __OBFID = "CL_00002515";

        public AmbientOcclusionFace(BlockModelRenderer p_i46235_1_)
        {
        }

        public AmbientOcclusionFace()
        {
        }

        public void updateVertexBrightness(IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags)
        {
            BlockPos blockpos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
            BlockModelRenderer.EnumNeighborInfo blockmodelrenderer$enumneighborinfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(facingIn);
            BlockPos blockpos1 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[0]);
            BlockPos blockpos2 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[1]);
            BlockPos blockpos3 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
            BlockPos blockpos4 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
            int i = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos1);
            int j = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos2);
            int k = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos3);
            int l = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos4);
            float f = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos1).getBlock().getAmbientOcclusionLightValue());
            float f1 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos2).getBlock().getAmbientOcclusionLightValue());
            float f2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos3).getBlock().getAmbientOcclusionLightValue());
            float f3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos4).getBlock().getAmbientOcclusionLightValue());
            boolean flag = blockAccessIn.getBlockState(blockpos1.offset(facingIn)).getBlock().isTranslucent();
            boolean flag1 = blockAccessIn.getBlockState(blockpos2.offset(facingIn)).getBlock().isTranslucent();
            boolean flag2 = blockAccessIn.getBlockState(blockpos3.offset(facingIn)).getBlock().isTranslucent();
            boolean flag3 = blockAccessIn.getBlockState(blockpos4.offset(facingIn)).getBlock().isTranslucent();
            float f4;
            int i1;

            if (!flag2 && !flag)
            {
                f4 = f;
                i1 = i;
            }
            else
            {
                BlockPos blockpos5 = blockpos1.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos5).getBlock().getAmbientOcclusionLightValue());
                i1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos5);
            }

            float f5;
            int j1;

            if (!flag3 && !flag)
            {
                f5 = f;
                j1 = i;
            }
            else
            {
                BlockPos blockpos6 = blockpos1.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f5 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos6).getBlock().getAmbientOcclusionLightValue());
                j1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos6);
            }

            float f6;
            int k1;

            if (!flag2 && !flag1)
            {
                f6 = f1;
                k1 = j;
            }
            else
            {
                BlockPos blockpos7 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f6 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos7).getBlock().getAmbientOcclusionLightValue());
                k1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos7);
            }

            float f7;
            int l1;

            if (!flag3 && !flag1)
            {
                f7 = f1;
                l1 = j;
            }
            else
            {
                BlockPos blockpos8 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f7 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(blockpos8).getBlock().getAmbientOcclusionLightValue());
                l1 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos8);
            }

            int i2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);

            if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube())
            {
                i2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
            }

            float f8 = boundsFlags.get(0) ? blockAccessIn.getBlockState(blockpos).getBlock().getAmbientOcclusionLightValue() : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
            f8 = BlockModelRenderer.fixAoLightValue(f8);
            BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(facingIn);

            if (boundsFlags.get(1) && blockmodelrenderer$enumneighborinfo.field_178289_i)
            {
                float f29 = (f3 + f + f5 + f8) * 0.25F;
                float f30 = (f2 + f + f4 + f8) * 0.25F;
                float f31 = (f2 + f1 + f6 + f8) * 0.25F;
                float f32 = (f3 + f1 + f7 + f8) * 0.25F;
                float f13 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[1].field_178229_m];
                float f14 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[3].field_178229_m];
                float f15 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[5].field_178229_m];
                float f16 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[7].field_178229_m];
                float f17 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[1].field_178229_m];
                float f18 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[3].field_178229_m];
                float f19 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[5].field_178229_m];
                float f20 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[7].field_178229_m];
                float f21 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[1].field_178229_m];
                float f22 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[3].field_178229_m];
                float f23 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[5].field_178229_m];
                float f24 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[7].field_178229_m];
                float f25 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[1].field_178229_m];
                float f26 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[3].field_178229_m];
                float f27 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[5].field_178229_m];
                float f28 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[7].field_178229_m];
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178191_g] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178200_h] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178201_i] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178198_j] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
                int j2 = this.getAoBrightness(l, i, j1, i2);
                int k2 = this.getAoBrightness(k, i, i1, i2);
                int l2 = this.getAoBrightness(k, j, k1, i2);
                int i3 = this.getAoBrightness(l, j, l1, i2);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178191_g] = this.getVertexBrightness(j2, k2, l2, i3, f13, f14, f15, f16);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178200_h] = this.getVertexBrightness(j2, k2, l2, i3, f17, f18, f19, f20);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178201_i] = this.getVertexBrightness(j2, k2, l2, i3, f21, f22, f23, f24);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178198_j] = this.getVertexBrightness(j2, k2, l2, i3, f25, f26, f27, f28);
            }
            else
            {
                float f9 = (f3 + f + f5 + f8) * 0.25F;
                float f10 = (f2 + f + f4 + f8) * 0.25F;
                float f11 = (f2 + f1 + f6 + f8) * 0.25F;
                float f12 = (f3 + f1 + f7 + f8) * 0.25F;
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178191_g] = this.getAoBrightness(l, i, j1, i2);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178200_h] = this.getAoBrightness(k, i, i1, i2);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178201_i] = this.getAoBrightness(k, j, k1, i2);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178198_j] = this.getAoBrightness(l, j, l1, i2);
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178191_g] = f9;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178200_h] = f10;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178201_i] = f11;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178198_j] = f12;
            }
        }

        private int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_)
        {
            if (p_147778_1_ == 0)
            {
                p_147778_1_ = p_147778_4_;
            }

            if (p_147778_2_ == 0)
            {
                p_147778_2_ = p_147778_4_;
            }

            if (p_147778_3_ == 0)
            {
                p_147778_3_ = p_147778_4_;
            }

            return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
        }

        private int getVertexBrightness(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_)
        {
            int i = (int)((float)(p_178203_1_ >> 16 & 255) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 255) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 255) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
            int j = (int)((float)(p_178203_1_ & 255) * p_178203_5_ + (float)(p_178203_2_ & 255) * p_178203_6_ + (float)(p_178203_3_ & 255) * p_178203_7_ + (float)(p_178203_4_ & 255) * p_178203_8_) & 255;
            return i << 16 | j;
        }
    }

    public static enum EnumNeighborInfo
    {
        DOWN("DOWN", 0, new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
        UP("UP", 1, new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
        NORTH("NORTH", 2, new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
        SOUTH("SOUTH", 3, new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
        WEST("WEST", 4, new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
        EAST("EAST", 5, new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

        protected final EnumFacing[] field_178276_g;
        protected final float field_178288_h;
        protected final boolean field_178289_i;
        protected final BlockModelRenderer.Orientation[] field_178286_j;
        protected final BlockModelRenderer.Orientation[] field_178287_k;
        protected final BlockModelRenderer.Orientation[] field_178284_l;
        protected final BlockModelRenderer.Orientation[] field_178285_m;
        private static final BlockModelRenderer.EnumNeighborInfo[] field_178282_n = new BlockModelRenderer.EnumNeighborInfo[6];
        private static final BlockModelRenderer.EnumNeighborInfo[] $VALUES = new BlockModelRenderer.EnumNeighborInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        private static final String __OBFID = "CL_00002516";

        private EnumNeighborInfo(String p_i5_3_, int p_i5_4_, EnumFacing[] p_i5_5_, float p_i5_6_, boolean p_i5_7_, BlockModelRenderer.Orientation[] p_i5_8_, BlockModelRenderer.Orientation[] p_i5_9_, BlockModelRenderer.Orientation[] p_i5_10_, BlockModelRenderer.Orientation[] p_i5_11_)
        {
            this.field_178276_g = p_i5_5_;
            this.field_178288_h = p_i5_6_;
            this.field_178289_i = p_i5_7_;
            this.field_178286_j = p_i5_8_;
            this.field_178287_k = p_i5_9_;
            this.field_178284_l = p_i5_10_;
            this.field_178285_m = p_i5_11_;
        }

        public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing p_178273_0_)
        {
            return field_178282_n[p_178273_0_.getIndex()];
        }

        static {
            field_178282_n[EnumFacing.DOWN.getIndex()] = DOWN;
            field_178282_n[EnumFacing.UP.getIndex()] = UP;
            field_178282_n[EnumFacing.NORTH.getIndex()] = NORTH;
            field_178282_n[EnumFacing.SOUTH.getIndex()] = SOUTH;
            field_178282_n[EnumFacing.WEST.getIndex()] = WEST;
            field_178282_n[EnumFacing.EAST.getIndex()] = EAST;
        }
    }

    public static enum Orientation
    {
        DOWN("DOWN", 0, EnumFacing.DOWN, false),
        UP("UP", 1, EnumFacing.UP, false),
        NORTH("NORTH", 2, EnumFacing.NORTH, false),
        SOUTH("SOUTH", 3, EnumFacing.SOUTH, false),
        WEST("WEST", 4, EnumFacing.WEST, false),
        EAST("EAST", 5, EnumFacing.EAST, false),
        FLIP_DOWN("FLIP_DOWN", 6, EnumFacing.DOWN, true),
        FLIP_UP("FLIP_UP", 7, EnumFacing.UP, true),
        FLIP_NORTH("FLIP_NORTH", 8, EnumFacing.NORTH, true),
        FLIP_SOUTH("FLIP_SOUTH", 9, EnumFacing.SOUTH, true),
        FLIP_WEST("FLIP_WEST", 10, EnumFacing.WEST, true),
        FLIP_EAST("FLIP_EAST", 11, EnumFacing.EAST, true);

        protected final int field_178229_m;
        private static final BlockModelRenderer.Orientation[] $VALUES = new BlockModelRenderer.Orientation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
        private static final String __OBFID = "CL_00002513";

        private Orientation(String p_i7_3_, int p_i7_4_, EnumFacing p_i7_5_, boolean p_i7_6_)
        {
            this.field_178229_m = p_i7_5_.getIndex() + (p_i7_6_ ? EnumFacing.values().length : 0);
        }
    }

    static enum VertexTranslations
    {
        DOWN("DOWN", 0, 0, 1, 2, 3),
        UP("UP", 1, 2, 3, 0, 1),
        NORTH("NORTH", 2, 3, 0, 1, 2),
        SOUTH("SOUTH", 3, 0, 1, 2, 3),
        WEST("WEST", 4, 3, 0, 1, 2),
        EAST("EAST", 5, 1, 2, 3, 0);

        private final int field_178191_g;
        private final int field_178200_h;
        private final int field_178201_i;
        private final int field_178198_j;
        private static final BlockModelRenderer.VertexTranslations[] field_178199_k = new BlockModelRenderer.VertexTranslations[6];
        private static final BlockModelRenderer.VertexTranslations[] $VALUES = new BlockModelRenderer.VertexTranslations[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        private static final String __OBFID = "CL_00002514";

        private VertexTranslations(String p_i6_3_, int p_i6_4_, int p_i6_5_, int p_i6_6_, int p_i6_7_, int p_i6_8_)
        {
            this.field_178191_g = p_i6_5_;
            this.field_178200_h = p_i6_6_;
            this.field_178201_i = p_i6_7_;
            this.field_178198_j = p_i6_8_;
        }

        public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing p_178184_0_)
        {
            return field_178199_k[p_178184_0_.getIndex()];
        }

        static {
            field_178199_k[EnumFacing.DOWN.getIndex()] = DOWN;
            field_178199_k[EnumFacing.UP.getIndex()] = UP;
            field_178199_k[EnumFacing.NORTH.getIndex()] = NORTH;
            field_178199_k[EnumFacing.SOUTH.getIndex()] = SOUTH;
            field_178199_k[EnumFacing.WEST.getIndex()] = WEST;
            field_178199_k[EnumFacing.EAST.getIndex()] = EAST;
        }
    }
}
