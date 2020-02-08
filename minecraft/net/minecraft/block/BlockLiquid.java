package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block {
	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

	protected BlockLiquid(Material materialIn) {
		super(materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setTickRandomly(true);
	}

	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return this.blockMaterial != Material.lava;
	}

	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
		return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 16777215;
	}

	/**
	 * Returns the percentage of the liquid block that is air, based on the given
	 * flow decay of the liquid
	 */
	public static float getLiquidHeightPercent(int meta) {
		if (meta >= 8) {
			meta = 0;
		}

		return (float) (meta + 1) / 9.0F;
	}

	protected int getLevel(IBlockAccess worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? ((Integer) worldIn.getBlockState(pos).getValue(LEVEL)).intValue() : -1;
	}

	protected int getEffectiveFlowDecay(IBlockAccess worldIn, BlockPos pos) {
		int i = this.getLevel(worldIn, pos);
		return i >= 8 ? 0 : i;
	}

	public boolean isFullCube() {
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for
	 * render
	 */
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return hitIfLiquid && ((Integer) state.getValue(LEVEL)).intValue() == 0;
	}

	/**
	 * Whether this Block is solid on the given Side
	 */
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
		return material == this.blockMaterial ? false : (side == EnumFacing.UP ? true : (material == Material.ice ? false : super.isBlockSolid(worldIn, pos, side)));
	}

	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? false : (side == EnumFacing.UP ? true : super.shouldSideBeRendered(worldIn, pos, side));
	}

	public boolean func_176364_g(IBlockAccess blockAccess, BlockPos pos) {
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				IBlockState iblockstate = blockAccess.getBlockState(pos.add(i, 0, j));
				Block block = iblockstate.getBlock();
				Material material = block.getMaterial();

				if (material != this.blockMaterial && !block.isFullBlock()) {
					return true;
				}
			}
		}

		return false;
	}

	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

	/**
	 * The type of render function called. 3 for standard block models, 2 for
	 * TESR's, 1 for liquids, -1 is no render
	 */
	public int getRenderType() {
		return 1;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return 0;
	}

	protected Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos) {
		Vec3 vec3 = new Vec3(0.0D, 0.0D, 0.0D);
		int i = this.getEffectiveFlowDecay(worldIn, pos);

		for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL) {
			EnumFacing enumfacing = (EnumFacing) enumfacing0;
			BlockPos blockpos = pos.offset(enumfacing);
			int j = this.getEffectiveFlowDecay(worldIn, blockpos);

			if (j < 0) {
				if (!worldIn.getBlockState(blockpos).getBlock().getMaterial().blocksMovement()) {
					j = this.getEffectiveFlowDecay(worldIn, blockpos.down());

					if (j >= 0) {
						int k = j - (i - 8);
						vec3 = vec3.addVector((double) ((blockpos.getX() - pos.getX()) * k), (double) ((blockpos.getY() - pos.getY()) * k), (double) ((blockpos.getZ() - pos.getZ()) * k));
					}
				}
			} else if (j >= 0) {
				int l = j - i;
				vec3 = vec3.addVector((double) ((blockpos.getX() - pos.getX()) * l), (double) ((blockpos.getY() - pos.getY()) * l), (double) ((blockpos.getZ() - pos.getZ()) * l));
			}
		}

		if (((Integer) worldIn.getBlockState(pos).getValue(LEVEL)).intValue() >= 8) {
			for (Object enumfacing10 : EnumFacing.Plane.HORIZONTAL) {
				EnumFacing enumfacing1 = (EnumFacing) enumfacing10;
				BlockPos blockpos1 = pos.offset(enumfacing1);

				if (this.isBlockSolid(worldIn, blockpos1, enumfacing1) || this.isBlockSolid(worldIn, blockpos1.up(), enumfacing1)) {
					vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
					break;
				}
			}
		}

		return vec3.normalize();
	}

	public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
		return motion.add(this.getFlowVector(worldIn, pos));
	}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn) {
		return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.getHasNoSky() ? 10 : 30) : 0);
	}

	public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
		int i = worldIn.getCombinedLight(pos, 0);
		int j = worldIn.getCombinedLight(pos.up(), 0);
		int k = i & 255;
		int l = j & 255;
		int i1 = i >> 16 & 255;
		int j1 = j >> 16 & 255;
		return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
	}

	public EnumWorldBlockLayer getBlockLayer() {
		return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
	}

	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		double d0 = (double) pos.getX();
		double d1 = (double) pos.getY();
		double d2 = (double) pos.getZ();

		if (this.blockMaterial == Material.water) {
			int i = ((Integer) state.getValue(LEVEL)).intValue();

			if (i > 0 && i < 8) {
				if (rand.nextInt(64) == 0) {
					worldIn.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F, false);
				}
			} else if (rand.nextInt(10) == 0) {
				worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + (double) rand.nextFloat(), d1 + (double) rand.nextFloat(), d2 + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}

		if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.up()).getBlock().getMaterial() == Material.air && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
			if (rand.nextInt(100) == 0) {
				double d8 = d0 + (double) rand.nextFloat();
				double d4 = d1 + this.maxY;
				double d6 = d2 + (double) rand.nextFloat();
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D, new int[0]);
				worldIn.playSound(d8, d4, d6, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}

			if (rand.nextInt(200) == 0) {
				worldIn.playSound(d0, d1, d2, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}
		}

		if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
			Material material = worldIn.getBlockState(pos.down(2)).getBlock().getMaterial();

			if (!material.blocksMovement() && !material.isLiquid()) {
				double d3 = d0 + (double) rand.nextFloat();
				double d5 = d1 - 1.05D;
				double d7 = d2 + (double) rand.nextFloat();

				if (this.blockMaterial == Material.water) {
					worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d3, d5, d7, 0.0D, 0.0D, 0.0D, new int[0]);
				} else {
					worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, d3, d5, d7, 0.0D, 0.0D, 0.0D, new int[0]);
				}
			}
		}
	}

	public static double getFlowDirection(IBlockAccess worldIn, BlockPos pos, Material materialIn) {
		Vec3 vec3 = getFlowingBlock(materialIn).getFlowVector(worldIn, pos);
		return vec3.xCoord == 0.0D && vec3.zCoord == 0.0D ? -1000.0D : MathHelper.func_181159_b(vec3.zCoord, vec3.xCoord) - (Math.PI / 2D);
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.checkForMixing(worldIn, pos, state);
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		this.checkForMixing(worldIn, pos, state);
	}

	public boolean checkForMixing(World worldIn, BlockPos pos, IBlockState state) {
		if (this.blockMaterial == Material.lava) {
			boolean flag = false;

			for (EnumFacing enumfacing : EnumFacing.values()) {
				if (enumfacing != EnumFacing.DOWN && worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial() == Material.water) {
					flag = true;
					break;
				}
			}

			if (flag) {
				Integer integer = (Integer) state.getValue(LEVEL);

				if (integer.intValue() == 0) {
					worldIn.setBlockState(pos, Blocks.obsidian.getDefaultState());
					this.triggerMixEffects(worldIn, pos);
					return true;
				}

				if (integer.intValue() <= 4) {
					worldIn.setBlockState(pos, Blocks.cobblestone.getDefaultState());
					this.triggerMixEffects(worldIn, pos);
					return true;
				}
			}
		}

		return false;
	}

	protected void triggerMixEffects(World worldIn, BlockPos pos) {
		double d0 = (double) pos.getX();
		double d1 = (double) pos.getY();
		double d2 = (double) pos.getZ();
		worldIn.playSoundEffect(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

		for (int i = 0; i < 8; ++i) {
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(LEVEL)).intValue();
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { LEVEL });
	}

	public static BlockDynamicLiquid getFlowingBlock(Material materialIn) {
		if (materialIn == Material.water) {
			return Blocks.flowing_water;
		} else if (materialIn == Material.lava) {
			return Blocks.flowing_lava;
		} else {
			throw new IllegalArgumentException("Invalid material");
		}
	}

	public static BlockStaticLiquid getStaticBlock(Material materialIn) {
		if (materialIn == Material.water) {
			return Blocks.water;
		} else if (materialIn == Material.lava) {
			return Blocks.lava;
		} else {
			throw new IllegalArgumentException("Invalid material");
		}
	}
}
