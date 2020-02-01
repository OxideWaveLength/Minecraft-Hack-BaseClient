package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class BlockModelShapes
{
    private final Map<IBlockState, IBakedModel> bakedModelStore = Maps.<IBlockState, IBakedModel>newIdentityHashMap();
    private final BlockStateMapper blockStateMapper = new BlockStateMapper();
    private final ModelManager modelManager;

    public BlockModelShapes(ModelManager manager)
    {
        this.modelManager = manager;
        this.registerAllBlocks();
    }

    public BlockStateMapper getBlockStateMapper()
    {
        return this.blockStateMapper;
    }

    public TextureAtlasSprite getTexture(IBlockState state)
    {
        Block block = state.getBlock();
        IBakedModel ibakedmodel = this.getModelForState(state);

        if (ibakedmodel == null || ibakedmodel == this.modelManager.getMissingModel())
        {
            if (block == Blocks.wall_sign || block == Blocks.standing_sign || block == Blocks.chest || block == Blocks.trapped_chest || block == Blocks.standing_banner || block == Blocks.wall_banner)
            {
                return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/planks_oak");
            }

            if (block == Blocks.ender_chest)
            {
                return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/obsidian");
            }

            if (block == Blocks.flowing_lava || block == Blocks.lava)
            {
                return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/lava_still");
            }

            if (block == Blocks.flowing_water || block == Blocks.water)
            {
                return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/water_still");
            }

            if (block == Blocks.skull)
            {
                return this.modelManager.getTextureMap().getAtlasSprite("minecraft:blocks/soul_sand");
            }

            if (block == Blocks.barrier)
            {
                return this.modelManager.getTextureMap().getAtlasSprite("minecraft:items/barrier");
            }
        }

        if (ibakedmodel == null)
        {
            ibakedmodel = this.modelManager.getMissingModel();
        }

        return ibakedmodel.getParticleTexture();
    }

    public IBakedModel getModelForState(IBlockState state)
    {
        IBakedModel ibakedmodel = (IBakedModel)this.bakedModelStore.get(state);

        if (ibakedmodel == null)
        {
            ibakedmodel = this.modelManager.getMissingModel();
        }

        return ibakedmodel;
    }

    public ModelManager getModelManager()
    {
        return this.modelManager;
    }

    public void reloadModels()
    {
        this.bakedModelStore.clear();

        for (Entry<IBlockState, ModelResourceLocation> entry : this.blockStateMapper.putAllStateModelLocations().entrySet())
        {
            this.bakedModelStore.put(entry.getKey(), this.modelManager.getModel((ModelResourceLocation)entry.getValue()));
        }
    }

    public void registerBlockWithStateMapper(Block assoc, IStateMapper stateMapper)
    {
        this.blockStateMapper.registerBlockStateMapper(assoc, stateMapper);
    }

    public void registerBuiltInBlocks(Block... builtIns)
    {
        this.blockStateMapper.registerBuiltInBlocks(builtIns);
    }

    private void registerAllBlocks()
    {
        this.registerBuiltInBlocks(new Block[] {Blocks.air, Blocks.flowing_water, Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.piston_extension, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.standing_sign, Blocks.skull, Blocks.end_portal, Blocks.barrier, Blocks.wall_sign, Blocks.wall_banner, Blocks.standing_banner});
        this.registerBlockWithStateMapper(Blocks.stone, (new StateMap.Builder()).withName(BlockStone.VARIANT).build());
        this.registerBlockWithStateMapper(Blocks.prismarine, (new StateMap.Builder()).withName(BlockPrismarine.VARIANT).build());
        this.registerBlockWithStateMapper(Blocks.leaves, (new StateMap.Builder()).withName(BlockOldLeaf.VARIANT).withSuffix("_leaves").ignore(new IProperty[] {BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE}).build());
        this.registerBlockWithStateMapper(Blocks.leaves2, (new StateMap.Builder()).withName(BlockNewLeaf.VARIANT).withSuffix("_leaves").ignore(new IProperty[] {BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE}).build());
        this.registerBlockWithStateMapper(Blocks.cactus, (new StateMap.Builder()).ignore(new IProperty[] {BlockCactus.AGE}).build());
        this.registerBlockWithStateMapper(Blocks.reeds, (new StateMap.Builder()).ignore(new IProperty[] {BlockReed.AGE}).build());
        this.registerBlockWithStateMapper(Blocks.jukebox, (new StateMap.Builder()).ignore(new IProperty[] {BlockJukebox.HAS_RECORD}).build());
        this.registerBlockWithStateMapper(Blocks.command_block, (new StateMap.Builder()).ignore(new IProperty[] {BlockCommandBlock.TRIGGERED}).build());
        this.registerBlockWithStateMapper(Blocks.cobblestone_wall, (new StateMap.Builder()).withName(BlockWall.VARIANT).withSuffix("_wall").build());
        this.registerBlockWithStateMapper(Blocks.double_plant, (new StateMap.Builder()).withName(BlockDoublePlant.VARIANT).ignore(new IProperty[] {BlockDoublePlant.field_181084_N}).build());
        this.registerBlockWithStateMapper(Blocks.oak_fence_gate, (new StateMap.Builder()).ignore(new IProperty[] {BlockFenceGate.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.spruce_fence_gate, (new StateMap.Builder()).ignore(new IProperty[] {BlockFenceGate.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.birch_fence_gate, (new StateMap.Builder()).ignore(new IProperty[] {BlockFenceGate.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.jungle_fence_gate, (new StateMap.Builder()).ignore(new IProperty[] {BlockFenceGate.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.dark_oak_fence_gate, (new StateMap.Builder()).ignore(new IProperty[] {BlockFenceGate.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.acacia_fence_gate, (new StateMap.Builder()).ignore(new IProperty[] {BlockFenceGate.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.tripwire, (new StateMap.Builder()).ignore(new IProperty[] {BlockTripWire.DISARMED, BlockTripWire.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.double_wooden_slab, (new StateMap.Builder()).withName(BlockPlanks.VARIANT).withSuffix("_double_slab").build());
        this.registerBlockWithStateMapper(Blocks.wooden_slab, (new StateMap.Builder()).withName(BlockPlanks.VARIANT).withSuffix("_slab").build());
        this.registerBlockWithStateMapper(Blocks.tnt, (new StateMap.Builder()).ignore(new IProperty[] {BlockTNT.EXPLODE}).build());
        this.registerBlockWithStateMapper(Blocks.fire, (new StateMap.Builder()).ignore(new IProperty[] {BlockFire.AGE}).build());
        this.registerBlockWithStateMapper(Blocks.redstone_wire, (new StateMap.Builder()).ignore(new IProperty[] {BlockRedstoneWire.POWER}).build());
        this.registerBlockWithStateMapper(Blocks.oak_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.spruce_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.birch_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.jungle_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.acacia_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.dark_oak_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.iron_door, (new StateMap.Builder()).ignore(new IProperty[] {BlockDoor.POWERED}).build());
        this.registerBlockWithStateMapper(Blocks.wool, (new StateMap.Builder()).withName(BlockColored.COLOR).withSuffix("_wool").build());
        this.registerBlockWithStateMapper(Blocks.carpet, (new StateMap.Builder()).withName(BlockColored.COLOR).withSuffix("_carpet").build());
        this.registerBlockWithStateMapper(Blocks.stained_hardened_clay, (new StateMap.Builder()).withName(BlockColored.COLOR).withSuffix("_stained_hardened_clay").build());
        this.registerBlockWithStateMapper(Blocks.stained_glass_pane, (new StateMap.Builder()).withName(BlockColored.COLOR).withSuffix("_stained_glass_pane").build());
        this.registerBlockWithStateMapper(Blocks.stained_glass, (new StateMap.Builder()).withName(BlockColored.COLOR).withSuffix("_stained_glass").build());
        this.registerBlockWithStateMapper(Blocks.sandstone, (new StateMap.Builder()).withName(BlockSandStone.TYPE).build());
        this.registerBlockWithStateMapper(Blocks.red_sandstone, (new StateMap.Builder()).withName(BlockRedSandstone.TYPE).build());
        this.registerBlockWithStateMapper(Blocks.tallgrass, (new StateMap.Builder()).withName(BlockTallGrass.TYPE).build());
        this.registerBlockWithStateMapper(Blocks.bed, (new StateMap.Builder()).ignore(new IProperty[] {BlockBed.OCCUPIED}).build());
        this.registerBlockWithStateMapper(Blocks.yellow_flower, (new StateMap.Builder()).withName(Blocks.yellow_flower.getTypeProperty()).build());
        this.registerBlockWithStateMapper(Blocks.red_flower, (new StateMap.Builder()).withName(Blocks.red_flower.getTypeProperty()).build());
        this.registerBlockWithStateMapper(Blocks.stone_slab, (new StateMap.Builder()).withName(BlockStoneSlab.VARIANT).withSuffix("_slab").build());
        this.registerBlockWithStateMapper(Blocks.stone_slab2, (new StateMap.Builder()).withName(BlockStoneSlabNew.VARIANT).withSuffix("_slab").build());
        this.registerBlockWithStateMapper(Blocks.monster_egg, (new StateMap.Builder()).withName(BlockSilverfish.VARIANT).withSuffix("_monster_egg").build());
        this.registerBlockWithStateMapper(Blocks.stonebrick, (new StateMap.Builder()).withName(BlockStoneBrick.VARIANT).build());
        this.registerBlockWithStateMapper(Blocks.dispenser, (new StateMap.Builder()).ignore(new IProperty[] {BlockDispenser.TRIGGERED}).build());
        this.registerBlockWithStateMapper(Blocks.dropper, (new StateMap.Builder()).ignore(new IProperty[] {BlockDropper.TRIGGERED}).build());
        this.registerBlockWithStateMapper(Blocks.log, (new StateMap.Builder()).withName(BlockOldLog.VARIANT).withSuffix("_log").build());
        this.registerBlockWithStateMapper(Blocks.log2, (new StateMap.Builder()).withName(BlockNewLog.VARIANT).withSuffix("_log").build());
        this.registerBlockWithStateMapper(Blocks.planks, (new StateMap.Builder()).withName(BlockPlanks.VARIANT).withSuffix("_planks").build());
        this.registerBlockWithStateMapper(Blocks.sapling, (new StateMap.Builder()).withName(BlockSapling.TYPE).withSuffix("_sapling").build());
        this.registerBlockWithStateMapper(Blocks.sand, (new StateMap.Builder()).withName(BlockSand.VARIANT).build());
        this.registerBlockWithStateMapper(Blocks.hopper, (new StateMap.Builder()).ignore(new IProperty[] {BlockHopper.ENABLED}).build());
        this.registerBlockWithStateMapper(Blocks.flower_pot, (new StateMap.Builder()).ignore(new IProperty[] {BlockFlowerPot.LEGACY_DATA}).build());
        this.registerBlockWithStateMapper(Blocks.quartz_block, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                BlockQuartz.EnumType blockquartz$enumtype = (BlockQuartz.EnumType)state.getValue(BlockQuartz.VARIANT);

                switch (blockquartz$enumtype)
                {
                    case DEFAULT:
                    default:
                        return new ModelResourceLocation("quartz_block", "normal");

                    case CHISELED:
                        return new ModelResourceLocation("chiseled_quartz_block", "normal");

                    case LINES_Y:
                        return new ModelResourceLocation("quartz_column", "axis=y");

                    case LINES_X:
                        return new ModelResourceLocation("quartz_column", "axis=x");

                    case LINES_Z:
                        return new ModelResourceLocation("quartz_column", "axis=z");
                }
            }
        });
        this.registerBlockWithStateMapper(Blocks.deadbush, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return new ModelResourceLocation("dead_bush", "normal");
            }
        });
        this.registerBlockWithStateMapper(Blocks.pumpkin_stem, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newLinkedHashMap(state.getProperties());

                if (state.getValue(BlockStem.FACING) != EnumFacing.UP)
                {
                    map.remove(BlockStem.AGE);
                }

                return new ModelResourceLocation((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock()), this.getPropertyString(map));
            }
        });
        this.registerBlockWithStateMapper(Blocks.melon_stem, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newLinkedHashMap(state.getProperties());

                if (state.getValue(BlockStem.FACING) != EnumFacing.UP)
                {
                    map.remove(BlockStem.AGE);
                }

                return new ModelResourceLocation((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock()), this.getPropertyString(map));
            }
        });
        this.registerBlockWithStateMapper(Blocks.dirt, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newLinkedHashMap(state.getProperties());
                String s = BlockDirt.VARIANT.getName((BlockDirt.DirtType)map.remove(BlockDirt.VARIANT));

                if (BlockDirt.DirtType.PODZOL != state.getValue(BlockDirt.VARIANT))
                {
                    map.remove(BlockDirt.SNOWY);
                }

                return new ModelResourceLocation(s, this.getPropertyString(map));
            }
        });
        this.registerBlockWithStateMapper(Blocks.double_stone_slab, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newLinkedHashMap(state.getProperties());
                String s = BlockStoneSlab.VARIANT.getName((BlockStoneSlab.EnumType)map.remove(BlockStoneSlab.VARIANT));
                map.remove(BlockStoneSlab.SEAMLESS);
                String s1 = ((Boolean)state.getValue(BlockStoneSlab.SEAMLESS)).booleanValue() ? "all" : "normal";
                return new ModelResourceLocation(s + "_double_slab", s1);
            }
        });
        this.registerBlockWithStateMapper(Blocks.double_stone_slab2, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                Map<IProperty, Comparable> map = Maps.<IProperty, Comparable>newLinkedHashMap(state.getProperties());
                String s = BlockStoneSlabNew.VARIANT.getName((BlockStoneSlabNew.EnumType)map.remove(BlockStoneSlabNew.VARIANT));
                map.remove(BlockStoneSlab.SEAMLESS);
                String s1 = ((Boolean)state.getValue(BlockStoneSlabNew.SEAMLESS)).booleanValue() ? "all" : "normal";
                return new ModelResourceLocation(s + "_double_slab", s1);
            }
        });
    }
}
