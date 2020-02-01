package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockColored extends Block
{
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);

    public BlockColored(Material materialIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values())
        {
            list.add(new ItemStack(itemIn, 1, enumdyecolor.getMetadata()));
        }
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {COLOR});
    }
}
