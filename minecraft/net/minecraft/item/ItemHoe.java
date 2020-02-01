package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHoe extends Item
{
    protected Item.ToolMaterial theToolMaterial;

    public ItemHoe(Item.ToolMaterial material)
    {
        this.theToolMaterial = material;
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @SuppressWarnings("incomplete-switch")

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (side != EnumFacing.DOWN && worldIn.getBlockState(pos.up()).getBlock().getMaterial() == Material.air)
            {
                if (block == Blocks.grass)
                {
                    return this.useHoe(stack, playerIn, worldIn, pos, Blocks.farmland.getDefaultState());
                }

                if (block == Blocks.dirt)
                {
                    switch ((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT))
                    {
                        case DIRT:
                            return this.useHoe(stack, playerIn, worldIn, pos, Blocks.farmland.getDefaultState());

                        case COARSE_DIRT:
                            return this.useHoe(stack, playerIn, worldIn, pos, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                    }
                }
            }

            return false;
        }
    }

    protected boolean useHoe(ItemStack stack, EntityPlayer player, World worldIn, BlockPos target, IBlockState newState)
    {
        worldIn.playSoundEffect((double)((float)target.getX() + 0.5F), (double)((float)target.getY() + 0.5F), (double)((float)target.getZ() + 0.5F), newState.getBlock().stepSound.getStepSound(), (newState.getBlock().stepSound.getVolume() + 1.0F) / 2.0F, newState.getBlock().stepSound.getFrequency() * 0.8F);

        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(target, newState);
            stack.damageItem(1, player);
            return true;
        }
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * Returns the name of the material this tool is made from as it is declared in EnumToolMaterial (meaning diamond
     * would return "EMERALD")
     */
    public String getMaterialName()
    {
        return this.theToolMaterial.toString();
    }
}
