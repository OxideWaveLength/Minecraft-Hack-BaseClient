package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class BlockRendererDispatcher implements IResourceManagerReloadListener
{
    private BlockModelShapes blockModelShapes;
    private final GameSettings gameSettings;
    private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
    private final ChestRenderer chestRenderer = new ChestRenderer();
    private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();

    public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn, GameSettings gameSettingsIn)
    {
        this.blockModelShapes = blockModelShapesIn;
        this.gameSettings = gameSettingsIn;
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.blockModelShapes;
    }

    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess)
    {
        Block block = state.getBlock();
        int i = block.getRenderType();

        if (i == 3)
        {
            state = block.getActualState(state, blockAccess, pos);
            IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);
            IBakedModel ibakedmodel1 = (new SimpleBakedModel.Builder(ibakedmodel, texture)).makeBakedModel();
            this.blockModelRenderer.renderModel(blockAccess, ibakedmodel1, state, pos, Tessellator.getInstance().getWorldRenderer());
        }
    }

    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn)
    {
        try
        {
            int i = state.getBlock().getRenderType();

            if (i == -1)
            {
                return false;
            }
            else
            {
                switch (i)
                {
                    case 1:
                        return this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);

                    case 2:
                        return false;

                    case 3:
                        IBakedModel ibakedmodel = this.getModelFromBlockState(state, blockAccess, pos);
                        return this.blockModelRenderer.renderModel(blockAccess, ibakedmodel, state, pos, worldRendererIn);

                    default:
                        return false;
                }
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
    }

    public BlockModelRenderer getBlockModelRenderer()
    {
        return this.blockModelRenderer;
    }

    private IBakedModel getBakedModel(IBlockState state, BlockPos pos)
    {
        IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && ibakedmodel instanceof WeightedBakedModel)
        {
            ibakedmodel = ((WeightedBakedModel)ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return ibakedmodel;
    }

    public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Block block = state.getBlock();

        if (worldIn.getWorldType() != WorldType.DEBUG_WORLD)
        {
            try
            {
                state = block.getActualState(state, worldIn, pos);
            }
            catch (Exception var6)
            {
                ;
            }
        }

        IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && ibakedmodel instanceof WeightedBakedModel)
        {
            ibakedmodel = ((WeightedBakedModel)ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return ibakedmodel;
    }

    public void renderBlockBrightness(IBlockState state, float brightness)
    {
        int i = state.getBlock().getRenderType();

        if (i != -1)
        {
            switch (i)
            {
                case 1:
                default:
                    break;

                case 2:
                    this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
                    break;

                case 3:
                    IBakedModel ibakedmodel = this.getBakedModel(state, (BlockPos)null);
                    this.blockModelRenderer.renderModelBrightness(ibakedmodel, state, brightness, true);
            }
        }
    }

    public boolean isRenderTypeChest(Block p_175021_1_, int p_175021_2_)
    {
        if (p_175021_1_ == null)
        {
            return false;
        }
        else
        {
            int i = p_175021_1_.getRenderType();
            return i == 3 ? false : i == 2;
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.fluidRenderer.initAtlasSprites();
    }
}
