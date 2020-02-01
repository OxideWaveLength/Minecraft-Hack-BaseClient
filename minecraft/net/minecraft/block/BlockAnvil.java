package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);

    protected BlockAnvil()
    {
        super(Material.anvil);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DAMAGE, Integer.valueOf(0)));
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing enumfacing = placer.getHorizontalFacing().rotateY();
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, enumfacing).withProperty(DAMAGE, Integer.valueOf(meta >> 2));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            playerIn.displayGui(new BlockAnvil.Anvil(worldIn, pos));
        }

        return true;
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((Integer)state.getValue(DAMAGE)).intValue();
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing enumfacing = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);

        if (enumfacing.getAxis() == EnumFacing.Axis.X)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        }
        else
        {
            this.setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
    }

    protected void onStartFalling(EntityFallingBlock fallingEntity)
    {
        fallingEntity.setHurtEntities(true);
    }

    public void onEndFalling(World worldIn, BlockPos pos)
    {
        worldIn.playAuxSFX(1022, pos, 0);
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(DAMAGE, Integer.valueOf((meta & 15) >> 2));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        i = i | ((Integer)state.getValue(DAMAGE)).intValue() << 2;
        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, DAMAGE});
    }

    public static class Anvil implements IInteractionObject
    {
        private final World world;
        private final BlockPos position;

        public Anvil(World worldIn, BlockPos pos)
        {
            this.world = worldIn;
            this.position = pos;
        }

        public String getName()
        {
            return "anvil";
        }

        public boolean hasCustomName()
        {
            return false;
        }

        public IChatComponent getDisplayName()
        {
            return new ChatComponentTranslation(Blocks.anvil.getUnlocalizedName() + ".name", new Object[0]);
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerRepair(playerInventory, this.world, this.position, playerIn);
        }

        public String getGuiID()
        {
            return "minecraft:anvil";
        }
    }
}
