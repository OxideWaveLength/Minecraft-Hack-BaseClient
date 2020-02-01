package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockSeaLantern extends Block
{
    public BlockSeaLantern(Material materialIn)
    {
        super(materialIn);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 2 + random.nextInt(2);
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.prismarine_crystals;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return MapColor.quartzColor;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }
}
