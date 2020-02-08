package net.minecraft.block;

import java.util.List;
import java.util.Random;

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
import net.minecraft.world.World;

public abstract class BlockStoneSlab extends BlockSlab {
	public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
	public static final PropertyEnum<BlockStoneSlab.EnumType> VARIANT = PropertyEnum.<BlockStoneSlab.EnumType>create("variant", BlockStoneSlab.EnumType.class);

	public BlockStoneSlab() {
		super(Material.rock);
		IBlockState iblockstate = this.blockState.getBaseState();

		if (this.isDouble()) {
			iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf(false));
		} else {
			iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
		}

		this.setDefaultState(iblockstate.withProperty(VARIANT, BlockStoneSlab.EnumType.STONE));
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.stone_slab);
	}

	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(Blocks.stone_slab);
	}

	/**
	 * Returns the slab block name with the type associated with it
	 */
	public String getUnlocalizedName(int meta) {
		return super.getUnlocalizedName() + "." + BlockStoneSlab.EnumType.byMetadata(meta).getUnlocalizedName();
	}

	public IProperty<?> getVariantProperty() {
		return VARIANT;
	}

	public Object getVariant(ItemStack stack) {
		return BlockStoneSlab.EnumType.byMetadata(stack.getMetadata() & 7);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab)) {
			for (BlockStoneSlab.EnumType blockstoneslab$enumtype : BlockStoneSlab.EnumType.values()) {
				if (blockstoneslab$enumtype != BlockStoneSlab.EnumType.WOOD) {
					list.add(new ItemStack(itemIn, 1, blockstoneslab$enumtype.getMetadata()));
				}
			}
		}
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.byMetadata(meta & 7));

		if (this.isDouble()) {
			iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf((meta & 8) != 0));
		} else {
			iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
		}

		return iblockstate;
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((BlockStoneSlab.EnumType) state.getValue(VARIANT)).getMetadata();

		if (this.isDouble()) {
			if (((Boolean) state.getValue(SEAMLESS)).booleanValue()) {
				i |= 8;
			}
		} else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
			i |= 8;
		}

		return i;
	}

	protected BlockState createBlockState() {
		return this.isDouble() ? new BlockState(this, new IProperty[] { SEAMLESS, VARIANT }) : new BlockState(this, new IProperty[] { HALF, VARIANT });
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when
	 * the block gets destroyed. It returns the metadata of the dropped item based
	 * on the old metadata of the block.
	 */
	public int damageDropped(IBlockState state) {
		return ((BlockStoneSlab.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	/**
	 * Get the MapColor for this Block and the given BlockState
	 */
	public MapColor getMapColor(IBlockState state) {
		return ((BlockStoneSlab.EnumType) state.getValue(VARIANT)).func_181074_c();
	}

	public static enum EnumType implements IStringSerializable {
		STONE(0, MapColor.stoneColor, "stone"), SAND(1, MapColor.sandColor, "sandstone", "sand"), WOOD(2, MapColor.woodColor, "wood_old", "wood"), COBBLESTONE(3, MapColor.stoneColor, "cobblestone", "cobble"), BRICK(4, MapColor.redColor, "brick"), SMOOTHBRICK(5, MapColor.stoneColor, "stone_brick", "smoothStoneBrick"), NETHERBRICK(6, MapColor.netherrackColor, "nether_brick", "netherBrick"), QUARTZ(7, MapColor.quartzColor, "quartz");

		private static final BlockStoneSlab.EnumType[] META_LOOKUP = new BlockStoneSlab.EnumType[values().length];
		private final int meta;
		private final MapColor field_181075_k;
		private final String name;
		private final String unlocalizedName;

		private EnumType(int p_i46381_3_, MapColor p_i46381_4_, String p_i46381_5_) {
			this(p_i46381_3_, p_i46381_4_, p_i46381_5_, p_i46381_5_);
		}

		private EnumType(int p_i46382_3_, MapColor p_i46382_4_, String p_i46382_5_, String p_i46382_6_) {
			this.meta = p_i46382_3_;
			this.field_181075_k = p_i46382_4_;
			this.name = p_i46382_5_;
			this.unlocalizedName = p_i46382_6_;
		}

		public int getMetadata() {
			return this.meta;
		}

		public MapColor func_181074_c() {
			return this.field_181075_k;
		}

		public String toString() {
			return this.name;
		}

		public static BlockStoneSlab.EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		static {
			for (BlockStoneSlab.EnumType blockstoneslab$enumtype : values()) {
				META_LOOKUP[blockstoneslab$enumtype.getMetadata()] = blockstoneslab$enumtype;
			}
		}
	}
}
