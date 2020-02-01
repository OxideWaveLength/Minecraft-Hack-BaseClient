package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemReed extends Item
{
    private Block block;

    public ItemReed(Block block)
    {
        this.block = block;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.snow_layer && ((Integer)iblockstate.getValue(BlockSnow.LAYERS)).intValue() < 1)
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        if (!playerIn.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (stack.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
            {
                IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);

                if (worldIn.setBlockState(pos, iblockstate1, 3))
                {
                    iblockstate1 = worldIn.getBlockState(pos);

                    if (iblockstate1.getBlock() == this.block)
                    {
                        ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack);
                        iblockstate1.getBlock().onBlockPlacedBy(worldIn, pos, iblockstate1, playerIn, stack);
                    }

                    worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
                    --stack.stackSize;
                    return true;
                }
            }

            return false;
        }
    }
}
