package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock
{
    private final BlockSlab singleSlab;
    private final BlockSlab doubleSlab;

    public ItemSlab(Block block, BlockSlab singleSlab, BlockSlab doubleSlab)
    {
        super(block);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.singleSlab.getUnlocalizedName(stack.getMetadata());
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            Object object = this.singleSlab.getVariant(stack);
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == this.singleSlab)
            {
                IProperty iproperty = this.singleSlab.getVariantProperty();
                Comparable comparable = iblockstate.getValue(iproperty);
                BlockSlab.EnumBlockHalf blockslab$enumblockhalf = (BlockSlab.EnumBlockHalf)iblockstate.getValue(BlockSlab.HALF);

                if ((side == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP) && comparable == object)
                {
                    IBlockState iblockstate1 = this.doubleSlab.getDefaultState().withProperty(iproperty, comparable);

                    if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, iblockstate1)) && worldIn.setBlockState(pos, iblockstate1, 3))
                    {
                        worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
                        --stack.stackSize;
                    }

                    return true;
                }
            }

            return this.tryPlace(stack, worldIn, pos.offset(side), object) ? true : super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        }
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        BlockPos blockpos = pos;
        IProperty iproperty = this.singleSlab.getVariantProperty();
        Object object = this.singleSlab.getVariant(stack);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this.singleSlab)
        {
            boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

            if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) && object == iblockstate.getValue(iproperty))
            {
                return true;
            }
        }

        pos = pos.offset(side);
        IBlockState iblockstate1 = worldIn.getBlockState(pos);
        return iblockstate1.getBlock() == this.singleSlab && object == iblockstate1.getValue(iproperty) ? true : super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack);
    }

    private boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos, Object variantInStack)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this.singleSlab)
        {
            Comparable comparable = iblockstate.getValue(this.singleSlab.getVariantProperty());

            if (comparable == variantInStack)
            {
                IBlockState iblockstate1 = this.doubleSlab.getDefaultState().withProperty((IProperty)this.singleSlab.getVariantProperty(), comparable);

                if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, iblockstate1)) && worldIn.setBlockState(pos, iblockstate1, 3))
                {
                    worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
                    --stack.stackSize;
                }

                return true;
            }
        }

        return false;
    }
}
