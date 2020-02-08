package net.minecraft.item;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Item {
	public static final RegistryNamespaced<ResourceLocation, Item> itemRegistry = new RegistryNamespaced();
	private static final Map<Block, Item> BLOCK_TO_ITEM = Maps.<Block, Item>newHashMap();
	protected static final UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private CreativeTabs tabToDisplayOn;

	/** The RNG used by the Item subclasses. */
	protected static Random itemRand = new Random();

	/** Maximum size of the stack. */
	protected int maxStackSize = 64;

	/** Maximum damage an item can handle. */
	private int maxDamage;

	/** If true, render the object in full 3D, like weapons and tools. */
	protected boolean bFull3D;

	/**
	 * Some items (like dyes) have multiple subtypes on same item, this is field
	 * define this behavior
	 */
	protected boolean hasSubtypes;
	private Item containerItem;

	/**
	 * The string representing this item's effect on a potion when used as an
	 * ingredient.
	 */
	private String potionEffect;

	/** The unlocalized name of this item. */
	private String unlocalizedName;

	public static int getIdFromItem(Item itemIn) {
		return itemIn == null ? 0 : itemRegistry.getIDForObject(itemIn);
	}

	public static Item getItemById(int id) {
		return (Item) itemRegistry.getObjectById(id);
	}

	public static Item getItemFromBlock(Block blockIn) {
		return (Item) BLOCK_TO_ITEM.get(blockIn);
	}

	/**
	 * Tries to get an Item by it's name (e.g. minecraft:apple) or a String
	 * representation of a numerical ID. If both fail, null is returned.
	 */
	public static Item getByNameOrId(String id) {
		Item item = (Item) itemRegistry.getObject(new ResourceLocation(id));

		if (item == null) {
			try {
				return getItemById(Integer.parseInt(id));
			} catch (NumberFormatException var3) {
				;
			}
		}

		return item;
	}

	/**
	 * Called when an ItemStack with NBT data is read to potentially that
	 * ItemStack's NBT data
	 */
	public boolean updateItemStackNBT(NBTTagCompound nbt) {
		return false;
	}

	public Item setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
		return this;
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public float getStrVsBlock(ItemStack stack, Block block) {
		return 1.0F;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 * Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		return itemStackIn;
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not
	 * called when the player stops using the Item before the action is complete.
	 */
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		return stack;
	}

	/**
	 * Returns the maximum size of the stack for a specific item. *Isn't this more a
	 * Set than a Get?*
	 */
	public int getItemStackLimit() {
		return this.maxStackSize;
	}

	/**
	 * Converts the given ItemStack damage value into a metadata value to be placed
	 * in the world when this Item is placed as a Block (mostly used with
	 * ItemBlocks).
	 */
	public int getMetadata(int damage) {
		return 0;
	}

	public boolean getHasSubtypes() {
		return this.hasSubtypes;
	}

	protected Item setHasSubtypes(boolean hasSubtypes) {
		this.hasSubtypes = hasSubtypes;
		return this;
	}

	/**
	 * Returns the maximum damage an item can take.
	 */
	public int getMaxDamage() {
		return this.maxDamage;
	}

	/**
	 * set max damage of an Item
	 */
	protected Item setMaxDamage(int maxDamageIn) {
		this.maxDamage = maxDamageIn;
		return this;
	}

	public boolean isDamageable() {
		return this.maxDamage > 0 && !this.hasSubtypes;
	}

	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return false;
	}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the
	 * "Use Item" statistic.
	 */
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
		return false;
	}

	/**
	 * Check whether this Item can harvest the given Block
	 */
	public boolean canHarvestBlock(Block blockIn) {
		return false;
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on
	 * sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
		return false;
	}

	/**
	 * Sets bFull3D to True and return the object.
	 */
	public Item setFull3D() {
		this.bFull3D = true;
		return this;
	}

	/**
	 * Returns True is the item is renderer in full 3D when hold.
	 */
	public boolean isFull3D() {
		return this.bFull3D;
	}

	/**
	 * Returns true if this item should be rotated by 180 degrees around the Y axis
	 * when being held in an entities hands.
	 */
	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}

	/**
	 * Sets the unlocalized name of this item to the string passed as the parameter,
	 * prefixed by "item."
	 */
	public Item setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	/**
	 * Translates the unlocalized name of this item, but without the .name suffix,
	 * so the translation fails and the unlocalized name itself is returned.
	 */
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		String s = this.getUnlocalizedName(stack);
		return s == null ? "" : StatCollector.translateToLocal(s);
	}

	/**
	 * Returns the unlocalized name of this item.
	 */
	public String getUnlocalizedName() {
		return "item." + this.unlocalizedName;
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack
	 * so different stacks can have different names based on their damage or NBT.
	 */
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + this.unlocalizedName;
	}

	public Item setContainerItem(Item containerItem) {
		this.containerItem = containerItem;
		return this;
	}

	/**
	 * If this function returns true (or the item is damageable), the ItemStack's
	 * NBT tag will be sent to the client.
	 */
	public boolean getShareTag() {
		return true;
	}

	public Item getContainerItem() {
		return this.containerItem;
	}

	/**
	 * True if this Item has a container item (a.k.a. crafting result)
	 */
	public boolean hasContainerItem() {
		return this.containerItem != null;
	}

	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		return 16777215;
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to
	 * check if is on a player hand and update it's contents.
	 */
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
	}

	/**
	 * Called when item is crafted/smelted. Used only by maps so far.
	 */
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
	}

	/**
	 * false for all Items except sub-classes of ItemMapBase
	 */
	public boolean isMap() {
		return false;
	}

	/**
	 * returns the action that specifies what animation to play when the items is
	 * being used
	 */
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack stack) {
		return 0;
	}

	/**
	 * Called when the player stops using an Item (stops holding the right mouse
	 * button).
	 */
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
	}

	/**
	 * Sets the string representing this item's effect on a potion when used as an
	 * ingredient.
	 */
	protected Item setPotionEffect(String potionEffect) {
		this.potionEffect = potionEffect;
		return this;
	}

	public String getPotionEffect(ItemStack stack) {
		return this.potionEffect;
	}

	public boolean isPotionIngredient(ItemStack stack) {
		return this.getPotionEffect(stack) != null;
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
	}

	public String getItemStackDisplayName(ItemStack stack) {
		return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
	}

	public boolean hasEffect(ItemStack stack) {
		return stack.isItemEnchanted();
	}

	/**
	 * Return an item rarity from EnumRarity
	 */
	public EnumRarity getRarity(ItemStack stack) {
		return stack.isItemEnchanted() ? EnumRarity.RARE : EnumRarity.COMMON;
	}

	/**
	 * Checks isDamagable and if it cannot be stacked
	 */
	public boolean isItemTool(ItemStack stack) {
		return this.getItemStackLimit() == 1 && this.isDamageable();
	}

	protected MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.posX;
		double d1 = playerIn.posY + (double) playerIn.getEyeHeight();
		double d2 = playerIn.posZ;
		Vec3 vec3 = new Vec3(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		Vec3 vec31 = vec3.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
		return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
	}

	/**
	 * Return the enchantability factor of the item, most of the time is based on
	 * material.
	 */
	public int getItemEnchantability() {
		return 0;
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns
	 * 16 items)
	 */
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
	}

	/**
	 * gets the CreativeTab this item is displayed on
	 */
	public CreativeTabs getCreativeTab() {
		return this.tabToDisplayOn;
	}

	/**
	 * returns this;
	 */
	public Item setCreativeTab(CreativeTabs tab) {
		this.tabToDisplayOn = tab;
		return this;
	}

	/**
	 * Returns true if players can use this item to affect the world (e.g. placing
	 * blocks, placing ender eyes in portal) when not in creative
	 */
	public boolean canItemEditBlocks() {
		return false;
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return false;
	}

	public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
		return HashMultimap.<String, AttributeModifier>create();
	}

	public static void registerItems() {
		registerItemBlock(Blocks.stone, (new ItemMultiTexture(Blocks.stone, Blocks.stone, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockStone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("stone"));
		registerItemBlock(Blocks.grass, new ItemColored(Blocks.grass, false));
		registerItemBlock(Blocks.dirt, (new ItemMultiTexture(Blocks.dirt, Blocks.dirt, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockDirt.DirtType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("dirt"));
		registerItemBlock(Blocks.cobblestone);
		registerItemBlock(Blocks.planks, (new ItemMultiTexture(Blocks.planks, Blocks.planks, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("wood"));
		registerItemBlock(Blocks.sapling, (new ItemMultiTexture(Blocks.sapling, Blocks.sapling, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("sapling"));
		registerItemBlock(Blocks.bedrock);
		registerItemBlock(Blocks.sand, (new ItemMultiTexture(Blocks.sand, Blocks.sand, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockSand.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("sand"));
		registerItemBlock(Blocks.gravel);
		registerItemBlock(Blocks.gold_ore);
		registerItemBlock(Blocks.iron_ore);
		registerItemBlock(Blocks.coal_ore);
		registerItemBlock(Blocks.log, (new ItemMultiTexture(Blocks.log, Blocks.log, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("log"));
		registerItemBlock(Blocks.log2, (new ItemMultiTexture(Blocks.log2, Blocks.log2, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockPlanks.EnumType.byMetadata(p_apply_1_.getMetadata() + 4).getUnlocalizedName();
			}
		})).setUnlocalizedName("log"));
		registerItemBlock(Blocks.leaves, (new ItemLeaves(Blocks.leaves)).setUnlocalizedName("leaves"));
		registerItemBlock(Blocks.leaves2, (new ItemLeaves(Blocks.leaves2)).setUnlocalizedName("leaves"));
		registerItemBlock(Blocks.sponge, (new ItemMultiTexture(Blocks.sponge, Blocks.sponge, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return (p_apply_1_.getMetadata() & 1) == 1 ? "wet" : "dry";
			}
		})).setUnlocalizedName("sponge"));
		registerItemBlock(Blocks.glass);
		registerItemBlock(Blocks.lapis_ore);
		registerItemBlock(Blocks.lapis_block);
		registerItemBlock(Blocks.dispenser);
		registerItemBlock(Blocks.sandstone, (new ItemMultiTexture(Blocks.sandstone, Blocks.sandstone, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockSandStone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("sandStone"));
		registerItemBlock(Blocks.noteblock);
		registerItemBlock(Blocks.golden_rail);
		registerItemBlock(Blocks.detector_rail);
		registerItemBlock(Blocks.sticky_piston, new ItemPiston(Blocks.sticky_piston));
		registerItemBlock(Blocks.web);
		registerItemBlock(Blocks.tallgrass, (new ItemColored(Blocks.tallgrass, true)).setSubtypeNames(new String[] { "shrub", "grass", "fern" }));
		registerItemBlock(Blocks.deadbush);
		registerItemBlock(Blocks.piston, new ItemPiston(Blocks.piston));
		registerItemBlock(Blocks.wool, (new ItemCloth(Blocks.wool)).setUnlocalizedName("cloth"));
		registerItemBlock(Blocks.yellow_flower, (new ItemMultiTexture(Blocks.yellow_flower, Blocks.yellow_flower, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.YELLOW, p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("flower"));
		registerItemBlock(Blocks.red_flower, (new ItemMultiTexture(Blocks.red_flower, Blocks.red_flower, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("rose"));
		registerItemBlock(Blocks.brown_mushroom);
		registerItemBlock(Blocks.red_mushroom);
		registerItemBlock(Blocks.gold_block);
		registerItemBlock(Blocks.iron_block);
		registerItemBlock(Blocks.stone_slab, (new ItemSlab(Blocks.stone_slab, Blocks.stone_slab, Blocks.double_stone_slab)).setUnlocalizedName("stoneSlab"));
		registerItemBlock(Blocks.brick_block);
		registerItemBlock(Blocks.tnt);
		registerItemBlock(Blocks.bookshelf);
		registerItemBlock(Blocks.mossy_cobblestone);
		registerItemBlock(Blocks.obsidian);
		registerItemBlock(Blocks.torch);
		registerItemBlock(Blocks.mob_spawner);
		registerItemBlock(Blocks.oak_stairs);
		registerItemBlock(Blocks.chest);
		registerItemBlock(Blocks.diamond_ore);
		registerItemBlock(Blocks.diamond_block);
		registerItemBlock(Blocks.crafting_table);
		registerItemBlock(Blocks.farmland);
		registerItemBlock(Blocks.furnace);
		registerItemBlock(Blocks.lit_furnace);
		registerItemBlock(Blocks.ladder);
		registerItemBlock(Blocks.rail);
		registerItemBlock(Blocks.stone_stairs);
		registerItemBlock(Blocks.lever);
		registerItemBlock(Blocks.stone_pressure_plate);
		registerItemBlock(Blocks.wooden_pressure_plate);
		registerItemBlock(Blocks.redstone_ore);
		registerItemBlock(Blocks.redstone_torch);
		registerItemBlock(Blocks.stone_button);
		registerItemBlock(Blocks.snow_layer, new ItemSnow(Blocks.snow_layer));
		registerItemBlock(Blocks.ice);
		registerItemBlock(Blocks.snow);
		registerItemBlock(Blocks.cactus);
		registerItemBlock(Blocks.clay);
		registerItemBlock(Blocks.jukebox);
		registerItemBlock(Blocks.oak_fence);
		registerItemBlock(Blocks.spruce_fence);
		registerItemBlock(Blocks.birch_fence);
		registerItemBlock(Blocks.jungle_fence);
		registerItemBlock(Blocks.dark_oak_fence);
		registerItemBlock(Blocks.acacia_fence);
		registerItemBlock(Blocks.pumpkin);
		registerItemBlock(Blocks.netherrack);
		registerItemBlock(Blocks.soul_sand);
		registerItemBlock(Blocks.glowstone);
		registerItemBlock(Blocks.lit_pumpkin);
		registerItemBlock(Blocks.trapdoor);
		registerItemBlock(Blocks.monster_egg, (new ItemMultiTexture(Blocks.monster_egg, Blocks.monster_egg, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockSilverfish.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("monsterStoneEgg"));
		registerItemBlock(Blocks.stonebrick, (new ItemMultiTexture(Blocks.stonebrick, Blocks.stonebrick, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockStoneBrick.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("stonebricksmooth"));
		registerItemBlock(Blocks.brown_mushroom_block);
		registerItemBlock(Blocks.red_mushroom_block);
		registerItemBlock(Blocks.iron_bars);
		registerItemBlock(Blocks.glass_pane);
		registerItemBlock(Blocks.melon_block);
		registerItemBlock(Blocks.vine, new ItemColored(Blocks.vine, false));
		registerItemBlock(Blocks.oak_fence_gate);
		registerItemBlock(Blocks.spruce_fence_gate);
		registerItemBlock(Blocks.birch_fence_gate);
		registerItemBlock(Blocks.jungle_fence_gate);
		registerItemBlock(Blocks.dark_oak_fence_gate);
		registerItemBlock(Blocks.acacia_fence_gate);
		registerItemBlock(Blocks.brick_stairs);
		registerItemBlock(Blocks.stone_brick_stairs);
		registerItemBlock(Blocks.mycelium);
		registerItemBlock(Blocks.waterlily, new ItemLilyPad(Blocks.waterlily));
		registerItemBlock(Blocks.nether_brick);
		registerItemBlock(Blocks.nether_brick_fence);
		registerItemBlock(Blocks.nether_brick_stairs);
		registerItemBlock(Blocks.enchanting_table);
		registerItemBlock(Blocks.end_portal_frame);
		registerItemBlock(Blocks.end_stone);
		registerItemBlock(Blocks.dragon_egg);
		registerItemBlock(Blocks.redstone_lamp);
		registerItemBlock(Blocks.wooden_slab, (new ItemSlab(Blocks.wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab)).setUnlocalizedName("woodSlab"));
		registerItemBlock(Blocks.sandstone_stairs);
		registerItemBlock(Blocks.emerald_ore);
		registerItemBlock(Blocks.ender_chest);
		registerItemBlock(Blocks.tripwire_hook);
		registerItemBlock(Blocks.emerald_block);
		registerItemBlock(Blocks.spruce_stairs);
		registerItemBlock(Blocks.birch_stairs);
		registerItemBlock(Blocks.jungle_stairs);
		registerItemBlock(Blocks.command_block);
		registerItemBlock(Blocks.beacon);
		registerItemBlock(Blocks.cobblestone_wall, (new ItemMultiTexture(Blocks.cobblestone_wall, Blocks.cobblestone_wall, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockWall.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("cobbleWall"));
		registerItemBlock(Blocks.wooden_button);
		registerItemBlock(Blocks.anvil, (new ItemAnvilBlock(Blocks.anvil)).setUnlocalizedName("anvil"));
		registerItemBlock(Blocks.trapped_chest);
		registerItemBlock(Blocks.light_weighted_pressure_plate);
		registerItemBlock(Blocks.heavy_weighted_pressure_plate);
		registerItemBlock(Blocks.daylight_detector);
		registerItemBlock(Blocks.redstone_block);
		registerItemBlock(Blocks.quartz_ore);
		registerItemBlock(Blocks.hopper);
		registerItemBlock(Blocks.quartz_block, (new ItemMultiTexture(Blocks.quartz_block, Blocks.quartz_block, new String[] { "default", "chiseled", "lines" })).setUnlocalizedName("quartzBlock"));
		registerItemBlock(Blocks.quartz_stairs);
		registerItemBlock(Blocks.activator_rail);
		registerItemBlock(Blocks.dropper);
		registerItemBlock(Blocks.stained_hardened_clay, (new ItemCloth(Blocks.stained_hardened_clay)).setUnlocalizedName("clayHardenedStained"));
		registerItemBlock(Blocks.barrier);
		registerItemBlock(Blocks.iron_trapdoor);
		registerItemBlock(Blocks.hay_block);
		registerItemBlock(Blocks.carpet, (new ItemCloth(Blocks.carpet)).setUnlocalizedName("woolCarpet"));
		registerItemBlock(Blocks.hardened_clay);
		registerItemBlock(Blocks.coal_block);
		registerItemBlock(Blocks.packed_ice);
		registerItemBlock(Blocks.acacia_stairs);
		registerItemBlock(Blocks.dark_oak_stairs);
		registerItemBlock(Blocks.slime_block);
		registerItemBlock(Blocks.double_plant, (new ItemDoublePlant(Blocks.double_plant, Blocks.double_plant, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockDoublePlant.EnumPlantType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("doublePlant"));
		registerItemBlock(Blocks.stained_glass, (new ItemCloth(Blocks.stained_glass)).setUnlocalizedName("stainedGlass"));
		registerItemBlock(Blocks.stained_glass_pane, (new ItemCloth(Blocks.stained_glass_pane)).setUnlocalizedName("stainedGlassPane"));
		registerItemBlock(Blocks.prismarine, (new ItemMultiTexture(Blocks.prismarine, Blocks.prismarine, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockPrismarine.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("prismarine"));
		registerItemBlock(Blocks.sea_lantern);
		registerItemBlock(Blocks.red_sandstone, (new ItemMultiTexture(Blocks.red_sandstone, Blocks.red_sandstone, new Function<ItemStack, String>() {
			public String apply(ItemStack p_apply_1_) {
				return BlockRedSandstone.EnumType.byMetadata(p_apply_1_.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("redSandStone"));
		registerItemBlock(Blocks.red_sandstone_stairs);
		registerItemBlock(Blocks.stone_slab2, (new ItemSlab(Blocks.stone_slab2, Blocks.stone_slab2, Blocks.double_stone_slab2)).setUnlocalizedName("stoneSlab2"));
		registerItem(256, "iron_shovel", (new ItemSpade(Item.ToolMaterial.IRON)).setUnlocalizedName("shovelIron"));
		registerItem(257, "iron_pickaxe", (new ItemPickaxe(Item.ToolMaterial.IRON)).setUnlocalizedName("pickaxeIron"));
		registerItem(258, "iron_axe", (new ItemAxe(Item.ToolMaterial.IRON)).setUnlocalizedName("hatchetIron"));
		registerItem(259, "flint_and_steel", (new ItemFlintAndSteel()).setUnlocalizedName("flintAndSteel"));
		registerItem(260, "apple", (new ItemFood(4, 0.3F, false)).setUnlocalizedName("apple"));
		registerItem(261, "bow", (new ItemBow()).setUnlocalizedName("bow"));
		registerItem(262, "arrow", (new Item()).setUnlocalizedName("arrow").setCreativeTab(CreativeTabs.tabCombat));
		registerItem(263, "coal", (new ItemCoal()).setUnlocalizedName("coal"));
		registerItem(264, "diamond", (new Item()).setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(265, "iron_ingot", (new Item()).setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(266, "gold_ingot", (new Item()).setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(267, "iron_sword", (new ItemSword(Item.ToolMaterial.IRON)).setUnlocalizedName("swordIron"));
		registerItem(268, "wooden_sword", (new ItemSword(Item.ToolMaterial.WOOD)).setUnlocalizedName("swordWood"));
		registerItem(269, "wooden_shovel", (new ItemSpade(Item.ToolMaterial.WOOD)).setUnlocalizedName("shovelWood"));
		registerItem(270, "wooden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.WOOD)).setUnlocalizedName("pickaxeWood"));
		registerItem(271, "wooden_axe", (new ItemAxe(Item.ToolMaterial.WOOD)).setUnlocalizedName("hatchetWood"));
		registerItem(272, "stone_sword", (new ItemSword(Item.ToolMaterial.STONE)).setUnlocalizedName("swordStone"));
		registerItem(273, "stone_shovel", (new ItemSpade(Item.ToolMaterial.STONE)).setUnlocalizedName("shovelStone"));
		registerItem(274, "stone_pickaxe", (new ItemPickaxe(Item.ToolMaterial.STONE)).setUnlocalizedName("pickaxeStone"));
		registerItem(275, "stone_axe", (new ItemAxe(Item.ToolMaterial.STONE)).setUnlocalizedName("hatchetStone"));
		registerItem(276, "diamond_sword", (new ItemSword(Item.ToolMaterial.EMERALD)).setUnlocalizedName("swordDiamond"));
		registerItem(277, "diamond_shovel", (new ItemSpade(Item.ToolMaterial.EMERALD)).setUnlocalizedName("shovelDiamond"));
		registerItem(278, "diamond_pickaxe", (new ItemPickaxe(Item.ToolMaterial.EMERALD)).setUnlocalizedName("pickaxeDiamond"));
		registerItem(279, "diamond_axe", (new ItemAxe(Item.ToolMaterial.EMERALD)).setUnlocalizedName("hatchetDiamond"));
		registerItem(280, "stick", (new Item()).setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(281, "bowl", (new Item()).setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(282, "mushroom_stew", (new ItemSoup(6)).setUnlocalizedName("mushroomStew"));
		registerItem(283, "golden_sword", (new ItemSword(Item.ToolMaterial.GOLD)).setUnlocalizedName("swordGold"));
		registerItem(284, "golden_shovel", (new ItemSpade(Item.ToolMaterial.GOLD)).setUnlocalizedName("shovelGold"));
		registerItem(285, "golden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.GOLD)).setUnlocalizedName("pickaxeGold"));
		registerItem(286, "golden_axe", (new ItemAxe(Item.ToolMaterial.GOLD)).setUnlocalizedName("hatchetGold"));
		registerItem(287, "string", (new ItemReed(Blocks.tripwire)).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(288, "feather", (new Item()).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(289, "gunpowder", (new Item()).setUnlocalizedName("sulphur").setPotionEffect(PotionHelper.gunpowderEffect).setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(290, "wooden_hoe", (new ItemHoe(Item.ToolMaterial.WOOD)).setUnlocalizedName("hoeWood"));
		registerItem(291, "stone_hoe", (new ItemHoe(Item.ToolMaterial.STONE)).setUnlocalizedName("hoeStone"));
		registerItem(292, "iron_hoe", (new ItemHoe(Item.ToolMaterial.IRON)).setUnlocalizedName("hoeIron"));
		registerItem(293, "diamond_hoe", (new ItemHoe(Item.ToolMaterial.EMERALD)).setUnlocalizedName("hoeDiamond"));
		registerItem(294, "golden_hoe", (new ItemHoe(Item.ToolMaterial.GOLD)).setUnlocalizedName("hoeGold"));
		registerItem(295, "wheat_seeds", (new ItemSeeds(Blocks.wheat, Blocks.farmland)).setUnlocalizedName("seeds"));
		registerItem(296, "wheat", (new Item()).setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(297, "bread", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("bread"));
		registerItem(298, "leather_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 0)).setUnlocalizedName("helmetCloth"));
		registerItem(299, "leather_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 1)).setUnlocalizedName("chestplateCloth"));
		registerItem(300, "leather_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 2)).setUnlocalizedName("leggingsCloth"));
		registerItem(301, "leather_boots", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 3)).setUnlocalizedName("bootsCloth"));
		registerItem(302, "chainmail_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 0)).setUnlocalizedName("helmetChain"));
		registerItem(303, "chainmail_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 1)).setUnlocalizedName("chestplateChain"));
		registerItem(304, "chainmail_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 2)).setUnlocalizedName("leggingsChain"));
		registerItem(305, "chainmail_boots", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 3)).setUnlocalizedName("bootsChain"));
		registerItem(306, "iron_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 0)).setUnlocalizedName("helmetIron"));
		registerItem(307, "iron_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 1)).setUnlocalizedName("chestplateIron"));
		registerItem(308, "iron_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 2)).setUnlocalizedName("leggingsIron"));
		registerItem(309, "iron_boots", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 3)).setUnlocalizedName("bootsIron"));
		registerItem(310, "diamond_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 0)).setUnlocalizedName("helmetDiamond"));
		registerItem(311, "diamond_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 1)).setUnlocalizedName("chestplateDiamond"));
		registerItem(312, "diamond_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 2)).setUnlocalizedName("leggingsDiamond"));
		registerItem(313, "diamond_boots", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 3)).setUnlocalizedName("bootsDiamond"));
		registerItem(314, "golden_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 0)).setUnlocalizedName("helmetGold"));
		registerItem(315, "golden_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 1)).setUnlocalizedName("chestplateGold"));
		registerItem(316, "golden_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 2)).setUnlocalizedName("leggingsGold"));
		registerItem(317, "golden_boots", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 3)).setUnlocalizedName("bootsGold"));
		registerItem(318, "flint", (new Item()).setUnlocalizedName("flint").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(319, "porkchop", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("porkchopRaw"));
		registerItem(320, "cooked_porkchop", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("porkchopCooked"));
		registerItem(321, "painting", (new ItemHangingEntity(EntityPainting.class)).setUnlocalizedName("painting"));
		registerItem(322, "golden_apple", (new ItemAppleGold(4, 1.2F, false)).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 5, 1, 1.0F).setUnlocalizedName("appleGold"));
		registerItem(323, "sign", (new ItemSign()).setUnlocalizedName("sign"));
		registerItem(324, "wooden_door", (new ItemDoor(Blocks.oak_door)).setUnlocalizedName("doorOak"));
		Item item = (new ItemBucket(Blocks.air)).setUnlocalizedName("bucket").setMaxStackSize(16);
		registerItem(325, "bucket", item);
		registerItem(326, "water_bucket", (new ItemBucket(Blocks.flowing_water)).setUnlocalizedName("bucketWater").setContainerItem(item));
		registerItem(327, "lava_bucket", (new ItemBucket(Blocks.flowing_lava)).setUnlocalizedName("bucketLava").setContainerItem(item));
		registerItem(328, "minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.RIDEABLE)).setUnlocalizedName("minecart"));
		registerItem(329, "saddle", (new ItemSaddle()).setUnlocalizedName("saddle"));
		registerItem(330, "iron_door", (new ItemDoor(Blocks.iron_door)).setUnlocalizedName("doorIron"));
		registerItem(331, "redstone", (new ItemRedstone()).setUnlocalizedName("redstone").setPotionEffect(PotionHelper.redstoneEffect));
		registerItem(332, "snowball", (new ItemSnowball()).setUnlocalizedName("snowball"));
		registerItem(333, "boat", (new ItemBoat()).setUnlocalizedName("boat"));
		registerItem(334, "leather", (new Item()).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(335, "milk_bucket", (new ItemBucketMilk()).setUnlocalizedName("milk").setContainerItem(item));
		registerItem(336, "brick", (new Item()).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(337, "clay_ball", (new Item()).setUnlocalizedName("clay").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(338, "reeds", (new ItemReed(Blocks.reeds)).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(339, "paper", (new Item()).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc));
		registerItem(340, "book", (new ItemBook()).setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc));
		registerItem(341, "slime_ball", (new Item()).setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.tabMisc));
		registerItem(342, "chest_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.CHEST)).setUnlocalizedName("minecartChest"));
		registerItem(343, "furnace_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.FURNACE)).setUnlocalizedName("minecartFurnace"));
		registerItem(344, "egg", (new ItemEgg()).setUnlocalizedName("egg"));
		registerItem(345, "compass", (new Item()).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools));
		registerItem(346, "fishing_rod", (new ItemFishingRod()).setUnlocalizedName("fishingRod"));
		registerItem(347, "clock", (new Item()).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools));
		registerItem(348, "glowstone_dust", (new Item()).setUnlocalizedName("yellowDust").setPotionEffect(PotionHelper.glowstoneEffect).setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(349, "fish", (new ItemFishFood(false)).setUnlocalizedName("fish").setHasSubtypes(true));
		registerItem(350, "cooked_fish", (new ItemFishFood(true)).setUnlocalizedName("fish").setHasSubtypes(true));
		registerItem(351, "dye", (new ItemDye()).setUnlocalizedName("dyePowder"));
		registerItem(352, "bone", (new Item()).setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc));
		registerItem(353, "sugar", (new Item()).setUnlocalizedName("sugar").setPotionEffect(PotionHelper.sugarEffect).setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(354, "cake", (new ItemReed(Blocks.cake)).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood));
		registerItem(355, "bed", (new ItemBed()).setMaxStackSize(1).setUnlocalizedName("bed"));
		registerItem(356, "repeater", (new ItemReed(Blocks.unpowered_repeater)).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone));
		registerItem(357, "cookie", (new ItemFood(2, 0.1F, false)).setUnlocalizedName("cookie"));
		registerItem(358, "filled_map", (new ItemMap()).setUnlocalizedName("map"));
		registerItem(359, "shears", (new ItemShears()).setUnlocalizedName("shears"));
		registerItem(360, "melon", (new ItemFood(2, 0.3F, false)).setUnlocalizedName("melon"));
		registerItem(361, "pumpkin_seeds", (new ItemSeeds(Blocks.pumpkin_stem, Blocks.farmland)).setUnlocalizedName("seeds_pumpkin"));
		registerItem(362, "melon_seeds", (new ItemSeeds(Blocks.melon_stem, Blocks.farmland)).setUnlocalizedName("seeds_melon"));
		registerItem(363, "beef", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("beefRaw"));
		registerItem(364, "cooked_beef", (new ItemFood(8, 0.8F, true)).setUnlocalizedName("beefCooked"));
		registerItem(365, "chicken", (new ItemFood(2, 0.3F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName("chickenRaw"));
		registerItem(366, "cooked_chicken", (new ItemFood(6, 0.6F, true)).setUnlocalizedName("chickenCooked"));
		registerItem(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh"));
		registerItem(368, "ender_pearl", (new ItemEnderPearl()).setUnlocalizedName("enderPearl"));
		registerItem(369, "blaze_rod", (new Item()).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials).setFull3D());
		registerItem(370, "ghast_tear", (new Item()).setUnlocalizedName("ghastTear").setPotionEffect(PotionHelper.ghastTearEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(371, "gold_nugget", (new Item()).setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(372, "nether_wart", (new ItemSeeds(Blocks.nether_wart, Blocks.soul_sand)).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4"));
		registerItem(373, "potion", (new ItemPotion()).setUnlocalizedName("potion"));
		registerItem(374, "glass_bottle", (new ItemGlassBottle()).setUnlocalizedName("glassBottle"));
		registerItem(375, "spider_eye", (new ItemFood(2, 0.8F, false)).setPotionEffect(Potion.poison.id, 5, 0, 1.0F).setUnlocalizedName("spiderEye").setPotionEffect(PotionHelper.spiderEyeEffect));
		registerItem(376, "fermented_spider_eye", (new Item()).setUnlocalizedName("fermentedSpiderEye").setPotionEffect(PotionHelper.fermentedSpiderEyeEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(377, "blaze_powder", (new Item()).setUnlocalizedName("blazePowder").setPotionEffect(PotionHelper.blazePowderEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(378, "magma_cream", (new Item()).setUnlocalizedName("magmaCream").setPotionEffect(PotionHelper.magmaCreamEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(379, "brewing_stand", (new ItemReed(Blocks.brewing_stand)).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(380, "cauldron", (new ItemReed(Blocks.cauldron)).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(381, "ender_eye", (new ItemEnderEye()).setUnlocalizedName("eyeOfEnder"));
		registerItem(382, "speckled_melon", (new Item()).setUnlocalizedName("speckledMelon").setPotionEffect(PotionHelper.speckledMelonEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(383, "spawn_egg", (new ItemMonsterPlacer()).setUnlocalizedName("monsterPlacer"));
		registerItem(384, "experience_bottle", (new ItemExpBottle()).setUnlocalizedName("expBottle"));
		registerItem(385, "fire_charge", (new ItemFireball()).setUnlocalizedName("fireball"));
		registerItem(386, "writable_book", (new ItemWritableBook()).setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc));
		registerItem(387, "written_book", (new ItemEditableBook()).setUnlocalizedName("writtenBook").setMaxStackSize(16));
		registerItem(388, "emerald", (new Item()).setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(389, "item_frame", (new ItemHangingEntity(EntityItemFrame.class)).setUnlocalizedName("frame"));
		registerItem(390, "flower_pot", (new ItemReed(Blocks.flower_pot)).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations));
		registerItem(391, "carrot", (new ItemSeedFood(3, 0.6F, Blocks.carrots, Blocks.farmland)).setUnlocalizedName("carrots"));
		registerItem(392, "potato", (new ItemSeedFood(1, 0.3F, Blocks.potatoes, Blocks.farmland)).setUnlocalizedName("potato"));
		registerItem(393, "baked_potato", (new ItemFood(5, 0.6F, false)).setUnlocalizedName("potatoBaked"));
		registerItem(394, "poisonous_potato", (new ItemFood(2, 0.3F, false)).setPotionEffect(Potion.poison.id, 5, 0, 0.6F).setUnlocalizedName("potatoPoisonous"));
		registerItem(395, "map", (new ItemEmptyMap()).setUnlocalizedName("emptyMap"));
		registerItem(396, "golden_carrot", (new ItemFood(6, 1.2F, false)).setUnlocalizedName("carrotGolden").setPotionEffect(PotionHelper.goldenCarrotEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(397, "skull", (new ItemSkull()).setUnlocalizedName("skull"));
		registerItem(398, "carrot_on_a_stick", (new ItemCarrotOnAStick()).setUnlocalizedName("carrotOnAStick"));
		registerItem(399, "nether_star", (new ItemSimpleFoiled()).setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(400, "pumpkin_pie", (new ItemFood(8, 0.3F, false)).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.tabFood));
		registerItem(401, "fireworks", (new ItemFirework()).setUnlocalizedName("fireworks"));
		registerItem(402, "firework_charge", (new ItemFireworkCharge()).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc));
		registerItem(403, "enchanted_book", (new ItemEnchantedBook()).setMaxStackSize(1).setUnlocalizedName("enchantedBook"));
		registerItem(404, "comparator", (new ItemReed(Blocks.unpowered_comparator)).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone));
		registerItem(405, "netherbrick", (new Item()).setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(406, "quartz", (new Item()).setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(407, "tnt_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.TNT)).setUnlocalizedName("minecartTnt"));
		registerItem(408, "hopper_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.HOPPER)).setUnlocalizedName("minecartHopper"));
		registerItem(409, "prismarine_shard", (new Item()).setUnlocalizedName("prismarineShard").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(410, "prismarine_crystals", (new Item()).setUnlocalizedName("prismarineCrystals").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(411, "rabbit", (new ItemFood(3, 0.3F, true)).setUnlocalizedName("rabbitRaw"));
		registerItem(412, "cooked_rabbit", (new ItemFood(5, 0.6F, true)).setUnlocalizedName("rabbitCooked"));
		registerItem(413, "rabbit_stew", (new ItemSoup(10)).setUnlocalizedName("rabbitStew"));
		registerItem(414, "rabbit_foot", (new Item()).setUnlocalizedName("rabbitFoot").setPotionEffect(PotionHelper.rabbitFootEffect).setCreativeTab(CreativeTabs.tabBrewing));
		registerItem(415, "rabbit_hide", (new Item()).setUnlocalizedName("rabbitHide").setCreativeTab(CreativeTabs.tabMaterials));
		registerItem(416, "armor_stand", (new ItemArmorStand()).setUnlocalizedName("armorStand").setMaxStackSize(16));
		registerItem(417, "iron_horse_armor", (new Item()).setUnlocalizedName("horsearmormetal").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
		registerItem(418, "golden_horse_armor", (new Item()).setUnlocalizedName("horsearmorgold").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
		registerItem(419, "diamond_horse_armor", (new Item()).setUnlocalizedName("horsearmordiamond").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc));
		registerItem(420, "lead", (new ItemLead()).setUnlocalizedName("leash"));
		registerItem(421, "name_tag", (new ItemNameTag()).setUnlocalizedName("nameTag"));
		registerItem(422, "command_block_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.COMMAND_BLOCK)).setUnlocalizedName("minecartCommandBlock").setCreativeTab((CreativeTabs) null));
		registerItem(423, "mutton", (new ItemFood(2, 0.3F, true)).setUnlocalizedName("muttonRaw"));
		registerItem(424, "cooked_mutton", (new ItemFood(6, 0.8F, true)).setUnlocalizedName("muttonCooked"));
		registerItem(425, "banner", (new ItemBanner()).setUnlocalizedName("banner"));
		registerItem(427, "spruce_door", (new ItemDoor(Blocks.spruce_door)).setUnlocalizedName("doorSpruce"));
		registerItem(428, "birch_door", (new ItemDoor(Blocks.birch_door)).setUnlocalizedName("doorBirch"));
		registerItem(429, "jungle_door", (new ItemDoor(Blocks.jungle_door)).setUnlocalizedName("doorJungle"));
		registerItem(430, "acacia_door", (new ItemDoor(Blocks.acacia_door)).setUnlocalizedName("doorAcacia"));
		registerItem(431, "dark_oak_door", (new ItemDoor(Blocks.dark_oak_door)).setUnlocalizedName("doorDarkOak"));
		registerItem(2256, "record_13", (new ItemRecord("13")).setUnlocalizedName("record"));
		registerItem(2257, "record_cat", (new ItemRecord("cat")).setUnlocalizedName("record"));
		registerItem(2258, "record_blocks", (new ItemRecord("blocks")).setUnlocalizedName("record"));
		registerItem(2259, "record_chirp", (new ItemRecord("chirp")).setUnlocalizedName("record"));
		registerItem(2260, "record_far", (new ItemRecord("far")).setUnlocalizedName("record"));
		registerItem(2261, "record_mall", (new ItemRecord("mall")).setUnlocalizedName("record"));
		registerItem(2262, "record_mellohi", (new ItemRecord("mellohi")).setUnlocalizedName("record"));
		registerItem(2263, "record_stal", (new ItemRecord("stal")).setUnlocalizedName("record"));
		registerItem(2264, "record_strad", (new ItemRecord("strad")).setUnlocalizedName("record"));
		registerItem(2265, "record_ward", (new ItemRecord("ward")).setUnlocalizedName("record"));
		registerItem(2266, "record_11", (new ItemRecord("11")).setUnlocalizedName("record"));
		registerItem(2267, "record_wait", (new ItemRecord("wait")).setUnlocalizedName("record"));
	}

	/**
	 * Register a default ItemBlock for the given Block.
	 */
	private static void registerItemBlock(Block blockIn) {
		registerItemBlock(blockIn, new ItemBlock(blockIn));
	}

	/**
	 * Register the given Item as the ItemBlock for the given Block.
	 */
	protected static void registerItemBlock(Block blockIn, Item itemIn) {
		registerItem(Block.getIdFromBlock(blockIn), (ResourceLocation) Block.blockRegistry.getNameForObject(blockIn), itemIn);
		BLOCK_TO_ITEM.put(blockIn, itemIn);
	}

	private static void registerItem(int id, String textualID, Item itemIn) {
		registerItem(id, new ResourceLocation(textualID), itemIn);
	}

	private static void registerItem(int id, ResourceLocation textualID, Item itemIn) {
		itemRegistry.register(id, textualID, itemIn);
	}

	public static enum ToolMaterial {
		WOOD(0, 59, 2.0F, 0.0F, 15), STONE(1, 131, 4.0F, 1.0F, 5), IRON(2, 250, 6.0F, 2.0F, 14), EMERALD(3, 1561, 8.0F, 3.0F, 10), GOLD(0, 32, 12.0F, 0.0F, 22);

		private final int harvestLevel;
		private final int maxUses;
		private final float efficiencyOnProperMaterial;
		private final float damageVsEntity;
		private final int enchantability;

		private ToolMaterial(int harvestLevel, int maxUses, float efficiency, float damageVsEntity, int enchantability) {
			this.harvestLevel = harvestLevel;
			this.maxUses = maxUses;
			this.efficiencyOnProperMaterial = efficiency;
			this.damageVsEntity = damageVsEntity;
			this.enchantability = enchantability;
		}

		public int getMaxUses() {
			return this.maxUses;
		}

		public float getEfficiencyOnProperMaterial() {
			return this.efficiencyOnProperMaterial;
		}

		public float getDamageVsEntity() {
			return this.damageVsEntity;
		}

		public int getHarvestLevel() {
			return this.harvestLevel;
		}

		public int getEnchantability() {
			return this.enchantability;
		}

		public Item getRepairItem() {
			return this == WOOD ? Item.getItemFromBlock(Blocks.planks) : (this == STONE ? Item.getItemFromBlock(Blocks.cobblestone) : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == EMERALD ? Items.diamond : null))));
		}
	}
}
