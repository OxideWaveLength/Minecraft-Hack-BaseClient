package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block
{
    public static final PropertyEnum<BlockHugeMushroom.EnumType> VARIANT = PropertyEnum.<BlockHugeMushroom.EnumType>create("variant", BlockHugeMushroom.EnumType.class);
    private final Block smallBlock;

    public BlockHugeMushroom(Material p_i46392_1_, MapColor p_i46392_2_, Block p_i46392_3_)
    {
        super(p_i46392_1_, p_i46392_2_);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockHugeMushroom.EnumType.ALL_OUTSIDE));
        this.smallBlock = p_i46392_3_;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return Math.max(0, random.nextInt(10) - 7);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        switch ((BlockHugeMushroom.EnumType)state.getValue(VARIANT))
        {
            case ALL_STEM:
                return MapColor.clothColor;

            case ALL_INSIDE:
                return MapColor.sandColor;

            case STEM:
                return MapColor.sandColor;

            default:
                return super.getMapColor(state);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(this.smallBlock);
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Item.getItemFromBlock(this.smallBlock);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockHugeMushroom.EnumType.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockHugeMushroom.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }

    public static enum EnumType implements IStringSerializable
    {
        NORTH_WEST(1, "north_west"),
        NORTH(2, "north"),
        NORTH_EAST(3, "north_east"),
        WEST(4, "west"),
        CENTER(5, "center"),
        EAST(6, "east"),
        SOUTH_WEST(7, "south_west"),
        SOUTH(8, "south"),
        SOUTH_EAST(9, "south_east"),
        STEM(10, "stem"),
        ALL_INSIDE(0, "all_inside"),
        ALL_OUTSIDE(14, "all_outside"),
        ALL_STEM(15, "all_stem");

        private static final BlockHugeMushroom.EnumType[] META_LOOKUP = new BlockHugeMushroom.EnumType[16];
        private final int meta;
        private final String name;

        private EnumType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockHugeMushroom.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            BlockHugeMushroom.EnumType blockhugemushroom$enumtype = META_LOOKUP[meta];
            return blockhugemushroom$enumtype == null ? META_LOOKUP[0] : blockhugemushroom$enumtype;
        }

        public String getName()
        {
            return this.name;
        }

        static {
            for (BlockHugeMushroom.EnumType blockhugemushroom$enumtype : values())
            {
                META_LOOKUP[blockhugemushroom$enumtype.getMetadata()] = blockhugemushroom$enumtype;
            }
        }
    }
}
