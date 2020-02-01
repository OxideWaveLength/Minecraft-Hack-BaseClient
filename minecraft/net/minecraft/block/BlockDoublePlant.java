package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockDoublePlant extends BlockBush implements IGrowable
{
    public static final PropertyEnum<BlockDoublePlant.EnumPlantType> VARIANT = PropertyEnum.<BlockDoublePlant.EnumPlantType>create("variant", BlockDoublePlant.EnumPlantType.class);
    public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.<BlockDoublePlant.EnumBlockHalf>create("half", BlockDoublePlant.EnumBlockHalf.class);
    public static final PropertyEnum<EnumFacing> field_181084_N = BlockDirectional.FACING;

    public BlockDoublePlant()
    {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDoublePlant.EnumPlantType.SUNFLOWER).withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(field_181084_N, EnumFacing.NORTH));
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.setUnlocalizedName("doublePlant");
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public BlockDoublePlant.EnumPlantType getVariant(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this)
        {
            iblockstate = this.getActualState(iblockstate, worldIn, pos);
            return (BlockDoublePlant.EnumPlantType)iblockstate.getValue(VARIANT);
        }
        else
        {
            return BlockDoublePlant.EnumPlantType.FERN;
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
    }

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() != this)
        {
            return true;
        }
        else
        {
            BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = (BlockDoublePlant.EnumPlantType)this.getActualState(iblockstate, worldIn, pos).getValue(VARIANT);
            return blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN || blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS;
        }
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            boolean flag = state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
            BlockPos blockpos = flag ? pos : pos.up();
            BlockPos blockpos1 = flag ? pos.down() : pos;
            Block block = (Block)(flag ? this : worldIn.getBlockState(blockpos).getBlock());
            Block block1 = (Block)(flag ? worldIn.getBlockState(blockpos1).getBlock() : this);

            if (block == this)
            {
                worldIn.setBlockState(blockpos, Blocks.air.getDefaultState(), 2);
            }

            if (block1 == this)
            {
                worldIn.setBlockState(blockpos1, Blocks.air.getDefaultState(), 3);

                if (!flag)
                {
                    this.dropBlockAsItem(worldIn, blockpos1, state, 0);
                }
            }
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            return worldIn.getBlockState(pos.down()).getBlock() == this;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.up());
            return iblockstate.getBlock() == this && super.canBlockStay(worldIn, pos, iblockstate);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            return null;
        }
        else
        {
            BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = (BlockDoublePlant.EnumPlantType)state.getValue(VARIANT);
            return blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.FERN ? null : (blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS ? (rand.nextInt(8) == 0 ? Items.wheat_seeds : null) : Item.getItemFromBlock(this));
        }
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return state.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.UPPER && state.getValue(VARIANT) != BlockDoublePlant.EnumPlantType.GRASS ? ((BlockDoublePlant.EnumPlantType)state.getValue(VARIANT)).getMeta() : 0;
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getVariant(worldIn, pos);
        return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN ? 16777215 : BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
    }

    public void placeAt(World worldIn, BlockPos lowerPos, BlockDoublePlant.EnumPlantType variant, int flags)
    {
        worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(VARIANT, variant), flags);
        worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), flags);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        if (worldIn.isRemote || player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != Items.shears || state.getValue(HALF) != BlockDoublePlant.EnumBlockHalf.LOWER || !this.onHarvest(worldIn, pos, state, player))
        {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            if (worldIn.getBlockState(pos.down()).getBlock() == this)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    IBlockState iblockstate = worldIn.getBlockState(pos.down());
                    BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = (BlockDoublePlant.EnumPlantType)iblockstate.getValue(VARIANT);

                    if (blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS)
                    {
                        worldIn.destroyBlock(pos.down(), true);
                    }
                    else if (!worldIn.isRemote)
                    {
                        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
                        {
                            this.onHarvest(worldIn, pos, iblockstate, player);
                            worldIn.setBlockToAir(pos.down());
                        }
                        else
                        {
                            worldIn.destroyBlock(pos.down(), true);
                        }
                    }
                    else
                    {
                        worldIn.setBlockToAir(pos.down());
                    }
                }
                else
                {
                    worldIn.setBlockToAir(pos.down());
                }
            }
        }
        else if (player.capabilities.isCreativeMode && worldIn.getBlockState(pos.up()).getBlock() == this)
        {
            worldIn.setBlockState(pos.up(), Blocks.air.getDefaultState(), 2);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    private boolean onHarvest(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = (BlockDoublePlant.EnumPlantType)state.getValue(VARIANT);

        if (blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS)
        {
            return false;
        }
        else
        {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            int i = (blockdoubleplant$enumplanttype == BlockDoublePlant.EnumPlantType.GRASS ? BlockTallGrass.EnumType.GRASS : BlockTallGrass.EnumType.FERN).getMeta();
            spawnAsEntity(worldIn, pos, new ItemStack(Blocks.tallgrass, 2, i));
            return true;
        }
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype : BlockDoublePlant.EnumPlantType.values())
        {
            list.add(new ItemStack(itemIn, 1, blockdoubleplant$enumplanttype.getMeta()));
        }
    }

    public int getDamageValue(World worldIn, BlockPos pos)
    {
        return this.getVariant(worldIn, pos).getMeta();
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = this.getVariant(worldIn, pos);
        return blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != BlockDoublePlant.EnumPlantType.FERN;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        spawnAsEntity(worldIn, pos, new ItemStack(this, 1, this.getVariant(worldIn, pos).getMeta()));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER) : this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER).withProperty(VARIANT, BlockDoublePlant.EnumPlantType.byMetadata(meta & 7));
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());

            if (iblockstate.getBlock() == this)
            {
                state = state.withProperty(VARIANT, iblockstate.getValue(VARIANT));
            }
        }

        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 8 | ((EnumFacing)state.getValue(field_181084_N)).getHorizontalIndex() : ((BlockDoublePlant.EnumPlantType)state.getValue(VARIANT)).getMeta();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {HALF, VARIANT, field_181084_N});
    }

    /**
     * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
     */
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    public static enum EnumBlockHalf implements IStringSerializable
    {
        UPPER,
        LOWER;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this == UPPER ? "upper" : "lower";
        }
    }

    public static enum EnumPlantType implements IStringSerializable
    {
        SUNFLOWER(0, "sunflower"),
        SYRINGA(1, "syringa"),
        GRASS(2, "double_grass", "grass"),
        FERN(3, "double_fern", "fern"),
        ROSE(4, "double_rose", "rose"),
        PAEONIA(5, "paeonia");

        private static final BlockDoublePlant.EnumPlantType[] META_LOOKUP = new BlockDoublePlant.EnumPlantType[values().length];
        private final int meta;
        private final String name;
        private final String unlocalizedName;

        private EnumPlantType(int meta, String name)
        {
            this(meta, name, name);
        }

        private EnumPlantType(int meta, String name, String unlocalizedName)
        {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockDoublePlant.EnumPlantType byMetadata(int meta)
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
            for (BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype : values())
            {
                META_LOOKUP[blockdoubleplant$enumplanttype.getMeta()] = blockdoubleplant$enumplanttype;
            }
        }
    }
}
