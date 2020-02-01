package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block
{
    public static final PropertyEnum<BlockDirt.DirtType> VARIANT = PropertyEnum.<BlockDirt.DirtType>create("variant", BlockDirt.DirtType.class);
    public static final PropertyBool SNOWY = PropertyBool.create("snowy");

    protected BlockDirt()
    {
        super(Material.ground);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDirt.DirtType.DIRT).withProperty(SNOWY, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return ((BlockDirt.DirtType)state.getValue(VARIANT)).func_181066_d();
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(VARIANT) == BlockDirt.DirtType.PODZOL)
        {
            Block block = worldIn.getBlockState(pos.up()).getBlock();
            state = state.withProperty(SNOWY, Boolean.valueOf(block == Blocks.snow || block == Blocks.snow_layer));
        }

        return state;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(this, 1, BlockDirt.DirtType.DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, BlockDirt.DirtType.COARSE_DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, BlockDirt.DirtType.PODZOL.getMetadata()));
    }

    public int getDamageValue(World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock() != this ? 0 : ((BlockDirt.DirtType)iblockstate.getValue(VARIANT)).getMetadata();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockDirt.DirtType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT, SNOWY});
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        BlockDirt.DirtType blockdirt$dirttype = (BlockDirt.DirtType)state.getValue(VARIANT);

        if (blockdirt$dirttype == BlockDirt.DirtType.PODZOL)
        {
            blockdirt$dirttype = BlockDirt.DirtType.DIRT;
        }

        return blockdirt$dirttype.getMetadata();
    }

    public static enum DirtType implements IStringSerializable
    {
        DIRT(0, "dirt", "default", MapColor.dirtColor),
        COARSE_DIRT(1, "coarse_dirt", "coarse", MapColor.dirtColor),
        PODZOL(2, "podzol", MapColor.obsidianColor);

        private static final BlockDirt.DirtType[] METADATA_LOOKUP = new BlockDirt.DirtType[values().length];
        private final int metadata;
        private final String name;
        private final String unlocalizedName;
        private final MapColor field_181067_h;

        private DirtType(int p_i46396_3_, String p_i46396_4_, MapColor p_i46396_5_)
        {
            this(p_i46396_3_, p_i46396_4_, p_i46396_4_, p_i46396_5_);
        }

        private DirtType(int p_i46397_3_, String p_i46397_4_, String p_i46397_5_, MapColor p_i46397_6_)
        {
            this.metadata = p_i46397_3_;
            this.name = p_i46397_4_;
            this.unlocalizedName = p_i46397_5_;
            this.field_181067_h = p_i46397_6_;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        public MapColor func_181066_d()
        {
            return this.field_181067_h;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockDirt.DirtType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public String getName()
        {
            return this.name;
        }

        static {
            for (BlockDirt.DirtType blockdirt$dirttype : values())
            {
                METADATA_LOOKUP[blockdirt$dirttype.getMetadata()] = blockdirt$dirttype;
            }
        }
    }
}
