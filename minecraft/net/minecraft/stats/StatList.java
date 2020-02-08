package net.minecraft.stats;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

public class StatList {
	protected static Map<String, StatBase> oneShotStats = Maps.<String, StatBase>newHashMap();
	public static List<StatBase> allStats = Lists.<StatBase>newArrayList();
	public static List<StatBase> generalStats = Lists.<StatBase>newArrayList();
	public static List<StatCrafting> itemStats = Lists.<StatCrafting>newArrayList();
	public static List<StatCrafting> objectMineStats = Lists.<StatCrafting>newArrayList();

	/** number of times you've left a game */
	public static StatBase leaveGameStat = (new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame", new Object[0]))).initIndependentStat().registerStat();

	/** number of minutes you have played */
	public static StatBase minutesPlayedStat = (new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute", new Object[0]), StatBase.timeStatType)).initIndependentStat().registerStat();
	public static StatBase timeSinceDeathStat = (new StatBasic("stat.timeSinceDeath", new ChatComponentTranslation("stat.timeSinceDeath", new Object[0]), StatBase.timeStatType)).initIndependentStat().registerStat();

	/** distance you've walked */
	public static StatBase distanceWalkedStat = (new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
	public static StatBase distanceCrouchedStat = (new StatBasic("stat.crouchOneCm", new ChatComponentTranslation("stat.crouchOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
	public static StatBase distanceSprintedStat = (new StatBasic("stat.sprintOneCm", new ChatComponentTranslation("stat.sprintOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** distance you have swam */
	public static StatBase distanceSwumStat = (new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you have fallen */
	public static StatBase distanceFallenStat = (new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you've climbed */
	public static StatBase distanceClimbedStat = (new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you've flown */
	public static StatBase distanceFlownStat = (new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you've dived */
	public static StatBase distanceDoveStat = (new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you've traveled by minecart */
	public static StatBase distanceByMinecartStat = (new StatBasic("stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you've traveled by boat */
	public static StatBase distanceByBoatStat = (new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the distance you've traveled by pig */
	public static StatBase distanceByPigStat = (new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();
	public static StatBase distanceByHorseStat = (new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm", new Object[0]), StatBase.distanceStatType)).initIndependentStat().registerStat();

	/** the times you've jumped */
	public static StatBase jumpStat = (new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump", new Object[0]))).initIndependentStat().registerStat();

	/** the distance you've dropped (or times you've fallen?) */
	public static StatBase dropStat = (new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop", new Object[0]))).initIndependentStat().registerStat();

	/** the amount of damage you've dealt */
	public static StatBase damageDealtStat = (new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt", new Object[0]), StatBase.field_111202_k)).registerStat();

	/** the amount of damage you have taken */
	public static StatBase damageTakenStat = (new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken", new Object[0]), StatBase.field_111202_k)).registerStat();

	/** the number of times you have died */
	public static StatBase deathsStat = (new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths", new Object[0]))).registerStat();

	/** the number of mobs you have killed */
	public static StatBase mobKillsStat = (new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills", new Object[0]))).registerStat();

	/** the number of animals you have bred */
	public static StatBase animalsBredStat = (new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred", new Object[0]))).registerStat();

	/** counts the number of times you've killed a player */
	public static StatBase playerKillsStat = (new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills", new Object[0]))).registerStat();
	public static StatBase fishCaughtStat = (new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught", new Object[0]))).registerStat();
	public static StatBase junkFishedStat = (new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished", new Object[0]))).registerStat();
	public static StatBase treasureFishedStat = (new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished", new Object[0]))).registerStat();
	public static StatBase timesTalkedToVillagerStat = (new StatBasic("stat.talkedToVillager", new ChatComponentTranslation("stat.talkedToVillager", new Object[0]))).registerStat();
	public static StatBase timesTradedWithVillagerStat = (new StatBasic("stat.tradedWithVillager", new ChatComponentTranslation("stat.tradedWithVillager", new Object[0]))).registerStat();
	public static StatBase field_181724_H = (new StatBasic("stat.cakeSlicesEaten", new ChatComponentTranslation("stat.cakeSlicesEaten", new Object[0]))).registerStat();
	public static StatBase field_181725_I = (new StatBasic("stat.cauldronFilled", new ChatComponentTranslation("stat.cauldronFilled", new Object[0]))).registerStat();
	public static StatBase field_181726_J = (new StatBasic("stat.cauldronUsed", new ChatComponentTranslation("stat.cauldronUsed", new Object[0]))).registerStat();
	public static StatBase field_181727_K = (new StatBasic("stat.armorCleaned", new ChatComponentTranslation("stat.armorCleaned", new Object[0]))).registerStat();
	public static StatBase field_181728_L = (new StatBasic("stat.bannerCleaned", new ChatComponentTranslation("stat.bannerCleaned", new Object[0]))).registerStat();
	public static StatBase field_181729_M = (new StatBasic("stat.brewingstandInteraction", new ChatComponentTranslation("stat.brewingstandInteraction", new Object[0]))).registerStat();
	public static StatBase field_181730_N = (new StatBasic("stat.beaconInteraction", new ChatComponentTranslation("stat.beaconInteraction", new Object[0]))).registerStat();
	public static StatBase field_181731_O = (new StatBasic("stat.dropperInspected", new ChatComponentTranslation("stat.dropperInspected", new Object[0]))).registerStat();
	public static StatBase field_181732_P = (new StatBasic("stat.hopperInspected", new ChatComponentTranslation("stat.hopperInspected", new Object[0]))).registerStat();
	public static StatBase field_181733_Q = (new StatBasic("stat.dispenserInspected", new ChatComponentTranslation("stat.dispenserInspected", new Object[0]))).registerStat();
	public static StatBase field_181734_R = (new StatBasic("stat.noteblockPlayed", new ChatComponentTranslation("stat.noteblockPlayed", new Object[0]))).registerStat();
	public static StatBase field_181735_S = (new StatBasic("stat.noteblockTuned", new ChatComponentTranslation("stat.noteblockTuned", new Object[0]))).registerStat();
	public static StatBase field_181736_T = (new StatBasic("stat.flowerPotted", new ChatComponentTranslation("stat.flowerPotted", new Object[0]))).registerStat();
	public static StatBase field_181737_U = (new StatBasic("stat.trappedChestTriggered", new ChatComponentTranslation("stat.trappedChestTriggered", new Object[0]))).registerStat();
	public static StatBase field_181738_V = (new StatBasic("stat.enderchestOpened", new ChatComponentTranslation("stat.enderchestOpened", new Object[0]))).registerStat();
	public static StatBase field_181739_W = (new StatBasic("stat.itemEnchanted", new ChatComponentTranslation("stat.itemEnchanted", new Object[0]))).registerStat();
	public static StatBase field_181740_X = (new StatBasic("stat.recordPlayed", new ChatComponentTranslation("stat.recordPlayed", new Object[0]))).registerStat();
	public static StatBase field_181741_Y = (new StatBasic("stat.furnaceInteraction", new ChatComponentTranslation("stat.furnaceInteraction", new Object[0]))).registerStat();
	public static StatBase field_181742_Z = (new StatBasic("stat.craftingTableInteraction", new ChatComponentTranslation("stat.workbenchInteraction", new Object[0]))).registerStat();
	public static StatBase field_181723_aa = (new StatBasic("stat.chestOpened", new ChatComponentTranslation("stat.chestOpened", new Object[0]))).registerStat();
	public static final StatBase[] mineBlockStatArray = new StatBase[4096];

	/** Tracks the number of items a given block or item has been crafted. */
	public static final StatBase[] objectCraftStats = new StatBase[32000];

	/** Tracks the number of times a given block or item has been used. */
	public static final StatBase[] objectUseStats = new StatBase[32000];

	/** Tracks the number of times a given block or item has been broken. */
	public static final StatBase[] objectBreakStats = new StatBase[32000];

	public static void init() {
		initMiningStats();
		initStats();
		initItemDepleteStats();
		initCraftableStats();
		AchievementList.init();
		EntityList.func_151514_a();
	}

	/**
	 * Initializes statistics related to craftable items. Is only called after both
	 * block and item stats have been initialized.
	 */
	private static void initCraftableStats() {
		Set<Item> set = Sets.<Item>newHashSet();

		for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
			if (irecipe.getRecipeOutput() != null) {
				set.add(irecipe.getRecipeOutput().getItem());
			}
		}

		for (ItemStack itemstack : FurnaceRecipes.instance().getSmeltingList().values()) {
			set.add(itemstack.getItem());
		}

		for (Item item : set) {
			if (item != null) {
				int i = Item.getIdFromItem(item);
				String s = func_180204_a(item);

				if (s != null) {
					objectCraftStats[i] = (new StatCrafting("stat.craftItem.", s, new ChatComponentTranslation("stat.craftItem", new Object[] { (new ItemStack(item)).getChatComponent() }), item)).registerStat();
				}
			}
		}

		replaceAllSimilarBlocks(objectCraftStats);
	}

	private static void initMiningStats() {
		for (Block block : Block.blockRegistry) {
			Item item = Item.getItemFromBlock(block);

			if (item != null) {
				int i = Block.getIdFromBlock(block);
				String s = func_180204_a(item);

				if (s != null && block.getEnableStats()) {
					mineBlockStatArray[i] = (new StatCrafting("stat.mineBlock.", s, new ChatComponentTranslation("stat.mineBlock", new Object[] { (new ItemStack(block)).getChatComponent() }), item)).registerStat();
					objectMineStats.add((StatCrafting) mineBlockStatArray[i]);
				}
			}
		}

		replaceAllSimilarBlocks(mineBlockStatArray);
	}

	private static void initStats() {
		for (Item item : Item.itemRegistry) {
			if (item != null) {
				int i = Item.getIdFromItem(item);
				String s = func_180204_a(item);

				if (s != null) {
					objectUseStats[i] = (new StatCrafting("stat.useItem.", s, new ChatComponentTranslation("stat.useItem", new Object[] { (new ItemStack(item)).getChatComponent() }), item)).registerStat();

					if (!(item instanceof ItemBlock)) {
						itemStats.add((StatCrafting) objectUseStats[i]);
					}
				}
			}
		}

		replaceAllSimilarBlocks(objectUseStats);
	}

	private static void initItemDepleteStats() {
		for (Item item : Item.itemRegistry) {
			if (item != null) {
				int i = Item.getIdFromItem(item);
				String s = func_180204_a(item);

				if (s != null && item.isDamageable()) {
					objectBreakStats[i] = (new StatCrafting("stat.breakItem.", s, new ChatComponentTranslation("stat.breakItem", new Object[] { (new ItemStack(item)).getChatComponent() }), item)).registerStat();
				}
			}
		}

		replaceAllSimilarBlocks(objectBreakStats);
	}

	private static String func_180204_a(Item p_180204_0_) {
		ResourceLocation resourcelocation = (ResourceLocation) Item.itemRegistry.getNameForObject(p_180204_0_);
		return resourcelocation != null ? resourcelocation.toString().replace(':', '.') : null;
	}

	/**
	 * Forces all dual blocks to count for each other on the stats list
	 */
	private static void replaceAllSimilarBlocks(StatBase[] p_75924_0_) {
		mergeStatBases(p_75924_0_, Blocks.water, Blocks.flowing_water);
		mergeStatBases(p_75924_0_, Blocks.lava, Blocks.flowing_lava);
		mergeStatBases(p_75924_0_, Blocks.lit_pumpkin, Blocks.pumpkin);
		mergeStatBases(p_75924_0_, Blocks.lit_furnace, Blocks.furnace);
		mergeStatBases(p_75924_0_, Blocks.lit_redstone_ore, Blocks.redstone_ore);
		mergeStatBases(p_75924_0_, Blocks.powered_repeater, Blocks.unpowered_repeater);
		mergeStatBases(p_75924_0_, Blocks.powered_comparator, Blocks.unpowered_comparator);
		mergeStatBases(p_75924_0_, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
		mergeStatBases(p_75924_0_, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
		mergeStatBases(p_75924_0_, Blocks.double_stone_slab, Blocks.stone_slab);
		mergeStatBases(p_75924_0_, Blocks.double_wooden_slab, Blocks.wooden_slab);
		mergeStatBases(p_75924_0_, Blocks.double_stone_slab2, Blocks.stone_slab2);
		mergeStatBases(p_75924_0_, Blocks.grass, Blocks.dirt);
		mergeStatBases(p_75924_0_, Blocks.farmland, Blocks.dirt);
	}

	/**
	 * Merge {@link StatBase} object references for similar blocks
	 */
	private static void mergeStatBases(StatBase[] statBaseIn, Block p_151180_1_, Block p_151180_2_) {
		int i = Block.getIdFromBlock(p_151180_1_);
		int j = Block.getIdFromBlock(p_151180_2_);

		if (statBaseIn[i] != null && statBaseIn[j] == null) {
			statBaseIn[j] = statBaseIn[i];
		} else {
			allStats.remove(statBaseIn[i]);
			objectMineStats.remove(statBaseIn[i]);
			generalStats.remove(statBaseIn[i]);
			statBaseIn[i] = statBaseIn[j];
		}
	}

	public static StatBase getStatKillEntity(EntityList.EntityEggInfo eggInfo) {
		String s = EntityList.getStringFromID(eggInfo.spawnedID);
		return s == null ? null : (new StatBase("stat.killEntity." + s, new ChatComponentTranslation("stat.entityKill", new Object[] { new ChatComponentTranslation("entity." + s + ".name", new Object[0]) }))).registerStat();
	}

	public static StatBase getStatEntityKilledBy(EntityList.EntityEggInfo eggInfo) {
		String s = EntityList.getStringFromID(eggInfo.spawnedID);
		return s == null ? null : (new StatBase("stat.entityKilledBy." + s, new ChatComponentTranslation("stat.entityKilledBy", new Object[] { new ChatComponentTranslation("entity." + s + ".name", new Object[0]) }))).registerStat();
	}

	public static StatBase getOneShotStat(String p_151177_0_) {
		return (StatBase) oneShotStats.get(p_151177_0_);
	}
}
