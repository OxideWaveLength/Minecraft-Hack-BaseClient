package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    protected BlockNetherWart()
    {
        super(Material.plants, MapColor.redColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.soul_sand;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int i = ((Integer)state.getValue(AGE)).intValue();

        if (i < 3 && rand.nextInt(10) == 0)
        {
            state = state.withProperty(AGE, Integer.valueOf(i + 1));
            worldIn.setBlockState(pos, state, 2);
        }

        super.updateTick(worldIn, pos, state, rand);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (!worldIn.isRemote)
        {
            int i = 1;

            if (((Integer)state.getValue(AGE)).intValue() >= 3)
            {
                i = 2 + worldIn.rand.nextInt(3);

                if (fortune > 0)
                {
                    i += worldIn.rand.nextInt(fortune + 1);
                }
            }

            for (int j = 0; j < i; ++j)
            {
                spawnAsEntity(worldIn, pos, new ItemStack(Items.nether_wart));
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.nether_wart;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AGE});
    }
}
