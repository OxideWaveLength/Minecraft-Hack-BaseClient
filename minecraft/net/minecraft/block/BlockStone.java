package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;

public class BlockStone extends Block
{
    public static final PropertyEnum<BlockStone.EnumType> VARIANT = PropertyEnum.<BlockStone.EnumType>create("variant", BlockStone.EnumType.class);

    public BlockStone()
    {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStone.EnumType.STONE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockStone.EnumType.STONE.getUnlocalizedName() + ".name");
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return ((BlockStone.EnumType)state.getValue(VARIANT)).func_181072_c();
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(VARIANT) == BlockStone.EnumType.STONE ? Item.getItemFromBlock(Blocks.cobblestone) : Item.getItemFromBlock(Blocks.stone);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((BlockStone.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (BlockStone.EnumType blockstone$enumtype : BlockStone.EnumType.values())
        {
            list.add(new ItemStack(itemIn, 1, blockstone$enumtype.getMetadata()));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockStone.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }

    public static enum EnumType implements IStringSerializable
    {
        STONE(0, MapColor.stoneColor, "stone"),
        GRANITE(1, MapColor.dirtColor, "granite"),
        GRANITE_SMOOTH(2, MapColor.dirtColor, "smooth_granite", "graniteSmooth"),
        DIORITE(3, MapColor.quartzColor, "diorite"),
        DIORITE_SMOOTH(4, MapColor.quartzColor, "smooth_diorite", "dioriteSmooth"),
        ANDESITE(5, MapColor.stoneColor, "andesite"),
        ANDESITE_SMOOTH(6, MapColor.stoneColor, "smooth_andesite", "andesiteSmooth");

        private static final BlockStone.EnumType[] META_LOOKUP = new BlockStone.EnumType[values().length];
        private final int meta;
        private final String name;
        private final String unlocalizedName;
        private final MapColor field_181073_l;

        private EnumType(int p_i46383_3_, MapColor p_i46383_4_, String p_i46383_5_)
        {
            this(p_i46383_3_, p_i46383_4_, p_i46383_5_, p_i46383_5_);
        }

        private EnumType(int p_i46384_3_, MapColor p_i46384_4_, String p_i46384_5_, String p_i46384_6_)
        {
            this.meta = p_i46384_3_;
            this.name = p_i46384_5_;
            this.unlocalizedName = p_i46384_6_;
            this.field_181073_l = p_i46384_4_;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public MapColor func_181072_c()
        {
            return this.field_181073_l;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockStone.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static {
            for (BlockStone.EnumType blockstone$enumtype : values())
            {
                META_LOOKUP[blockstone$enumtype.getMetadata()] = blockstone$enumtype;
            }
        }
    }
}
