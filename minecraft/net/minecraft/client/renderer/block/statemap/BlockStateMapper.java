package net.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockStateMapper
{
    private Map<Block, IStateMapper> blockStateMap = Maps.<Block, IStateMapper>newIdentityHashMap();
    private Set<Block> setBuiltInBlocks = Sets.<Block>newIdentityHashSet();

    public void registerBlockStateMapper(Block p_178447_1_, IStateMapper p_178447_2_)
    {
        this.blockStateMap.put(p_178447_1_, p_178447_2_);
    }

    public void registerBuiltInBlocks(Block... p_178448_1_)
    {
        Collections.addAll(this.setBuiltInBlocks, p_178448_1_);
    }

    public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations()
    {
        Map<IBlockState, ModelResourceLocation> map = Maps.<IBlockState, ModelResourceLocation>newIdentityHashMap();

        for (Block block : Block.blockRegistry)
        {
            if (!this.setBuiltInBlocks.contains(block))
            {
                map.putAll(((IStateMapper)Objects.firstNonNull(this.blockStateMap.get(block), new DefaultStateMapper())).putStateModelLocations(block));
            }
        }

        return map;
    }
}
