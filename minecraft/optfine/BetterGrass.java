package optfine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BetterGrass
{
    private static IBakedModel modelEmpty = new SimpleBakedModel(new ArrayList(), new ArrayList(), false, false, (TextureAtlasSprite)null, (ItemCameraTransforms)null);
    private static IBakedModel modelCubeMycelium = null;
    private static IBakedModel modelCubeGrassSnowy = null;
    private static IBakedModel modelCubeGrass = null;

    public static void update()
    {
        modelCubeGrass = BlockModelUtils.makeModelCube((String)"minecraft:blocks/grass_top", 0);
        modelCubeGrassSnowy = BlockModelUtils.makeModelCube((String)"minecraft:blocks/snow", -1);
        modelCubeMycelium = BlockModelUtils.makeModelCube((String)"minecraft:blocks/mycelium_top", -1);
    }

    public static List getFaceQuads(IBlockAccess p_getFaceQuads_0_, Block p_getFaceQuads_1_, BlockPos p_getFaceQuads_2_, EnumFacing p_getFaceQuads_3_, List p_getFaceQuads_4_)
    {
        if (p_getFaceQuads_3_ != EnumFacing.UP && p_getFaceQuads_3_ != EnumFacing.DOWN)
        {
            if (p_getFaceQuads_1_ instanceof BlockMycelium)
            {
                return Config.isBetterGrassFancy() ? (getBlockAt(p_getFaceQuads_2_.down(), p_getFaceQuads_3_, p_getFaceQuads_0_) == Blocks.mycelium ? modelCubeMycelium.getFaceQuads(p_getFaceQuads_3_) : p_getFaceQuads_4_) : modelCubeMycelium.getFaceQuads(p_getFaceQuads_3_);
            }
            else
            {
                if (p_getFaceQuads_1_ instanceof BlockGrass)
                {
                    Block block = p_getFaceQuads_0_.getBlockState(p_getFaceQuads_2_.up()).getBlock();
                    boolean flag = block == Blocks.snow || block == Blocks.snow_layer;

                    if (!Config.isBetterGrassFancy())
                    {
                        if (flag)
                        {
                            return modelCubeGrassSnowy.getFaceQuads(p_getFaceQuads_3_);
                        }

                        return modelCubeGrass.getFaceQuads(p_getFaceQuads_3_);
                    }

                    if (flag)
                    {
                        if (getBlockAt(p_getFaceQuads_2_, p_getFaceQuads_3_, p_getFaceQuads_0_) == Blocks.snow_layer)
                        {
                            return modelCubeGrassSnowy.getFaceQuads(p_getFaceQuads_3_);
                        }
                    }
                    else if (getBlockAt(p_getFaceQuads_2_.down(), p_getFaceQuads_3_, p_getFaceQuads_0_) == Blocks.grass)
                    {
                        return modelCubeGrass.getFaceQuads(p_getFaceQuads_3_);
                    }
                }

                return p_getFaceQuads_4_;
            }
        }
        else
        {
            return p_getFaceQuads_4_;
        }
    }

    private static Block getBlockAt(BlockPos p_getBlockAt_0_, EnumFacing p_getBlockAt_1_, IBlockAccess p_getBlockAt_2_)
    {
        BlockPos blockpos = p_getBlockAt_0_.offset(p_getBlockAt_1_);
        Block block = p_getBlockAt_2_.getBlockState(blockpos).getBlock();
        return block;
    }
}
