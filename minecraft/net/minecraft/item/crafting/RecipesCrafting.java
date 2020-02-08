package net.minecraft.item.crafting;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesCrafting {
	/**
	 * Adds the crafting recipes to the CraftingManager.
	 */
	public void addRecipes(CraftingManager p_77589_1_) {
		p_77589_1_.addRecipe(new ItemStack(Blocks.chest), new Object[] { "###", "# #", "###", '#', Blocks.planks });
		p_77589_1_.addRecipe(new ItemStack(Blocks.trapped_chest), new Object[] { "#-", '#', Blocks.chest, '-', Blocks.tripwire_hook });
		p_77589_1_.addRecipe(new ItemStack(Blocks.ender_chest), new Object[] { "###", "#E#", "###", '#', Blocks.obsidian, 'E', Items.ender_eye });
		p_77589_1_.addRecipe(new ItemStack(Blocks.furnace), new Object[] { "###", "# #", "###", '#', Blocks.cobblestone });
		p_77589_1_.addRecipe(new ItemStack(Blocks.crafting_table), new Object[] { "##", "##", '#', Blocks.planks });
		p_77589_1_.addRecipe(new ItemStack(Blocks.sandstone), new Object[] { "##", "##", '#', new ItemStack(Blocks.sand, 1, BlockSand.EnumType.SAND.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.red_sandstone), new Object[] { "##", "##", '#', new ItemStack(Blocks.sand, 1, BlockSand.EnumType.RED_SAND.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.sandstone, 4, BlockSandStone.EnumType.SMOOTH.getMetadata()), new Object[] { "##", "##", '#', new ItemStack(Blocks.sandstone, 1, BlockSandStone.EnumType.DEFAULT.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.red_sandstone, 4, BlockRedSandstone.EnumType.SMOOTH.getMetadata()), new Object[] { "##", "##", '#', new ItemStack(Blocks.red_sandstone, 1, BlockRedSandstone.EnumType.DEFAULT.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.sandstone, 1, BlockSandStone.EnumType.CHISELED.getMetadata()), new Object[] { "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, BlockStoneSlab.EnumType.SAND.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.red_sandstone, 1, BlockRedSandstone.EnumType.CHISELED.getMetadata()), new Object[] { "#", "#", '#', new ItemStack(Blocks.stone_slab2, 1, BlockStoneSlabNew.EnumType.RED_SANDSTONE.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.quartz_block, 1, BlockQuartz.EnumType.CHISELED.getMetadata()), new Object[] { "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, BlockStoneSlab.EnumType.QUARTZ.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.quartz_block, 2, BlockQuartz.EnumType.LINES_Y.getMetadata()), new Object[] { "#", "#", '#', new ItemStack(Blocks.quartz_block, 1, BlockQuartz.EnumType.DEFAULT.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.stonebrick, 4), new Object[] { "##", "##", '#', new ItemStack(Blocks.stone, 1, BlockStone.EnumType.STONE.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.CHISELED_META), new Object[] { "#", "#", '#', new ItemStack(Blocks.stone_slab, 1, BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()) });
		p_77589_1_.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.MOSSY_META), new Object[] { Blocks.stonebrick, Blocks.vine });
		p_77589_1_.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone, 1), new Object[] { Blocks.cobblestone, Blocks.vine });
		p_77589_1_.addRecipe(new ItemStack(Blocks.iron_bars, 16), new Object[] { "###", "###", '#', Items.iron_ingot });
		p_77589_1_.addRecipe(new ItemStack(Blocks.glass_pane, 16), new Object[] { "###", "###", '#', Blocks.glass });
		p_77589_1_.addRecipe(new ItemStack(Blocks.redstone_lamp, 1), new Object[] { " R ", "RGR", " R ", 'R', Items.redstone, 'G', Blocks.glowstone });
		p_77589_1_.addRecipe(new ItemStack(Blocks.beacon, 1), new Object[] { "GGG", "GSG", "OOO", 'G', Blocks.glass, 'S', Items.nether_star, 'O', Blocks.obsidian });
		p_77589_1_.addRecipe(new ItemStack(Blocks.nether_brick, 1), new Object[] { "NN", "NN", 'N', Items.netherbrick });
		p_77589_1_.addRecipe(new ItemStack(Blocks.stone, 2, BlockStone.EnumType.DIORITE.getMetadata()), new Object[] { "CQ", "QC", 'C', Blocks.cobblestone, 'Q', Items.quartz });
		p_77589_1_.addShapelessRecipe(new ItemStack(Blocks.stone, 1, BlockStone.EnumType.GRANITE.getMetadata()), new Object[] { new ItemStack(Blocks.stone, 1, BlockStone.EnumType.DIORITE.getMetadata()), Items.quartz });
		p_77589_1_.addShapelessRecipe(new ItemStack(Blocks.stone, 2, BlockStone.EnumType.ANDESITE.getMetadata()), new Object[] { new ItemStack(Blocks.stone, 1, BlockStone.EnumType.DIORITE.getMetadata()), Blocks.cobblestone });
		p_77589_1_.addRecipe(new ItemStack(Blocks.dirt, 4, BlockDirt.DirtType.COARSE_DIRT.getMetadata()), new Object[] { "DG", "GD", 'D', new ItemStack(Blocks.dirt, 1, BlockDirt.DirtType.DIRT.getMetadata()), 'G', Blocks.gravel });
		p_77589_1_.addRecipe(new ItemStack(Blocks.stone, 4, BlockStone.EnumType.DIORITE_SMOOTH.getMetadata()), new Object[] { "SS", "SS", 'S', new ItemStack(Blocks.stone, 1, BlockStone.EnumType.DIORITE.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.stone, 4, BlockStone.EnumType.GRANITE_SMOOTH.getMetadata()), new Object[] { "SS", "SS", 'S', new ItemStack(Blocks.stone, 1, BlockStone.EnumType.GRANITE.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.stone, 4, BlockStone.EnumType.ANDESITE_SMOOTH.getMetadata()), new Object[] { "SS", "SS", 'S', new ItemStack(Blocks.stone, 1, BlockStone.EnumType.ANDESITE.getMetadata()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.ROUGH_META), new Object[] { "SS", "SS", 'S', Items.prismarine_shard });
		p_77589_1_.addRecipe(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.BRICKS_META), new Object[] { "SSS", "SSS", "SSS", 'S', Items.prismarine_shard });
		p_77589_1_.addRecipe(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.DARK_META), new Object[] { "SSS", "SIS", "SSS", 'S', Items.prismarine_shard, 'I', new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()) });
		p_77589_1_.addRecipe(new ItemStack(Blocks.sea_lantern, 1, 0), new Object[] { "SCS", "CCC", "SCS", 'S', Items.prismarine_shard, 'C', Items.prismarine_crystals });
	}
}
