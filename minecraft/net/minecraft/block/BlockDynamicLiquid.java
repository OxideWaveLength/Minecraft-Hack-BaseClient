package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid
{
    int adjacentSourceBlocks;

    protected BlockDynamicLiquid(Material materialIn)
    {
        super(materialIn);
    }

    private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState)
    {
        worldIn.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int i = ((Integer)state.getValue(LEVEL)).intValue();
        int j = 1;

        if (this.blockMaterial == Material.lava && !worldIn.provider.doesWaterVaporize())
        {
            j = 2;
        }

        int k = this.tickRate(worldIn);

        if (i > 0)
        {
            int l = -100;
            this.adjacentSourceBlocks = 0;

            for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL)
            {
                EnumFacing enumfacing = (EnumFacing) enumfacing0;
                l = this.checkAdjacentBlock(worldIn, pos.offset(enumfacing), l);
            }

            int i1 = l + j;

            if (i1 >= 8 || l < 0)
            {
                i1 = -1;
            }

            if (this.getLevel(worldIn, pos.up()) >= 0)
            {
                int j1 = this.getLevel(worldIn, pos.up());

                if (j1 >= 8)
                {
                    i1 = j1;
                }
                else
                {
                    i1 = j1 + 8;
                }
            }

            if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water)
            {
                IBlockState iblockstate1 = worldIn.getBlockState(pos.down());

                if (iblockstate1.getBlock().getMaterial().isSolid())
                {
                    i1 = 0;
                }
                else if (iblockstate1.getBlock().getMaterial() == this.blockMaterial && ((Integer)iblockstate1.getValue(LEVEL)).intValue() == 0)
                {
                    i1 = 0;
                }
            }

            if (this.blockMaterial == Material.lava && i < 8 && i1 < 8 && i1 > i && rand.nextInt(4) != 0)
            {
                k *= 4;
            }

            if (i1 == i)
            {
                this.placeStaticBlock(worldIn, pos, state);
            }
            else
            {
                i = i1;

                if (i1 < 0)
                {
                    worldIn.setBlockToAir(pos);
                }
                else
                {
                    state = state.withProperty(LEVEL, Integer.valueOf(i1));
                    worldIn.setBlockState(pos, state, 2);
                    worldIn.scheduleUpdate(pos, this, k);
                    worldIn.notifyNeighborsOfStateChange(pos, this);
                }
            }
        }
        else
        {
            this.placeStaticBlock(worldIn, pos, state);
        }

        IBlockState iblockstate = worldIn.getBlockState(pos.down());

        if (this.canFlowInto(worldIn, pos.down(), iblockstate))
        {
            if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.down()).getBlock().getMaterial() == Material.water)
            {
                worldIn.setBlockState(pos.down(), Blocks.stone.getDefaultState());
                this.triggerMixEffects(worldIn, pos.down());
                return;
            }

            if (i >= 8)
            {
                this.tryFlowInto(worldIn, pos.down(), iblockstate, i);
            }
            else
            {
                this.tryFlowInto(worldIn, pos.down(), iblockstate, i + 8);
            }
        }
        else if (i >= 0 && (i == 0 || this.isBlocked(worldIn, pos.down(), iblockstate)))
        {
            Set<EnumFacing> set = this.getPossibleFlowDirections(worldIn, pos);
            int k1 = i + j;

            if (i >= 8)
            {
                k1 = 1;
            }

            if (k1 >= 8)
            {
                return;
            }

            for (EnumFacing enumfacing1 : set)
            {
                this.tryFlowInto(worldIn, pos.offset(enumfacing1), worldIn.getBlockState(pos.offset(enumfacing1)), k1);
            }
        }
    }

    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level)
    {
        if (this.canFlowInto(worldIn, pos, state))
        {
            if (state.getBlock() != Blocks.air)
            {
                if (this.blockMaterial == Material.lava)
                {
                    this.triggerMixEffects(worldIn, pos);
                }
                else
                {
                    state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
                }
            }

            worldIn.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, Integer.valueOf(level)), 3);
        }
    }

    private int func_176374_a(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost)
    {
        int i = 1000;

        for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL)
        {
            EnumFacing enumfacing = (EnumFacing) enumfacing0;

            if (enumfacing != calculateFlowCost)
            {
                BlockPos blockpos = pos.offset(enumfacing);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (!this.isBlocked(worldIn, blockpos, iblockstate) && (iblockstate.getBlock().getMaterial() != this.blockMaterial || ((Integer)iblockstate.getValue(LEVEL)).intValue() > 0))
                {
                    if (!this.isBlocked(worldIn, blockpos.down(), iblockstate))
                    {
                        return distance;
                    }

                    if (distance < 4)
                    {
                        int j = this.func_176374_a(worldIn, blockpos, distance + 1, enumfacing.getOpposite());

                        if (j < i)
                        {
                            i = j;
                        }
                    }
                }
            }
        }

        return i;
    }

    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos)
    {
        int i = 1000;
        Set<EnumFacing> set = EnumSet.<EnumFacing>noneOf(EnumFacing.class);

        for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL)
        {
            EnumFacing enumfacing = (EnumFacing) enumfacing0;
            BlockPos blockpos = pos.offset(enumfacing);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (!this.isBlocked(worldIn, blockpos, iblockstate) && (iblockstate.getBlock().getMaterial() != this.blockMaterial || ((Integer)iblockstate.getValue(LEVEL)).intValue() > 0))
            {
                int j;

                if (this.isBlocked(worldIn, blockpos.down(), worldIn.getBlockState(blockpos.down())))
                {
                    j = this.func_176374_a(worldIn, blockpos, 1, enumfacing.getOpposite());
                }
                else
                {
                    j = 0;
                }

                if (j < i)
                {
                    set.clear();
                }

                if (j <= i)
                {
                    set.add(enumfacing);
                    i = j;
                }
            }
        }

        return set;
    }

    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        return !(block instanceof BlockDoor) && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds ? (block.blockMaterial == Material.portal ? true : block.blockMaterial.blocksMovement()) : true;
    }

    protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel)
    {
        int i = this.getLevel(worldIn, pos);

        if (i < 0)
        {
            return currentMinLevel;
        }
        else
        {
            if (i == 0)
            {
                ++this.adjacentSourceBlocks;
            }

            if (i >= 8)
            {
                i = 0;
            }

            return currentMinLevel >= 0 && i >= currentMinLevel ? currentMinLevel : i;
        }
    }

    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state)
    {
        Material material = state.getBlock().getMaterial();
        return material != this.blockMaterial && material != Material.lava && !this.isBlocked(worldIn, pos, state);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.checkForMixing(worldIn, pos, state))
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }
}
