package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabs
{
    public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
    public static final CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks")
    {
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.brick_block);
        }
    };
    public static final CreativeTabs tabDecorations = new CreativeTabs(1, "decorations")
    {
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.double_plant);
        }
        public int getIconItemDamage()
        {
            return BlockDoublePlant.EnumPlantType.PAEONIA.getMeta();
        }
    };
    public static final CreativeTabs tabRedstone = new CreativeTabs(2, "redstone")
    {
        public Item getTabIconItem()
        {
            return Items.redstone;
        }
    };
    public static final CreativeTabs tabTransport = new CreativeTabs(3, "transportation")
    {
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.golden_rail);
        }
    };
    public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc")
    {
        public Item getTabIconItem()
        {
            return Items.lava_bucket;
        }
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.ALL});
    public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search")
    {
        public Item getTabIconItem()
        {
            return Items.compass;
        }
    }).setBackgroundImageName("item_search.png");
    public static final CreativeTabs tabFood = new CreativeTabs(6, "food")
    {
        public Item getTabIconItem()
        {
            return Items.apple;
        }
    };
    public static final CreativeTabs tabTools = (new CreativeTabs(7, "tools")
    {
        public Item getTabIconItem()
        {
            return Items.iron_axe;
        }
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE});
    public static final CreativeTabs tabCombat = (new CreativeTabs(8, "combat")
    {
        public Item getTabIconItem()
        {
            return Items.golden_sword;
        }
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_TORSO, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON});
    public static final CreativeTabs tabBrewing = new CreativeTabs(9, "brewing")
    {
        public Item getTabIconItem()
        {
            return Items.potionitem;
        }
    };
    public static final CreativeTabs tabMaterials = new CreativeTabs(10, "materials")
    {
        public Item getTabIconItem()
        {
            return Items.stick;
        }
    };
    public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory")
    {
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.chest);
        }
    }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;

    /** Texture to use. */
    private String theTexture = "items.png";
    private boolean hasScrollbar = true;

    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean drawTitle = true;
    private EnumEnchantmentType[] enchantmentTypes;
    private ItemStack iconItemStack;

    public CreativeTabs(int index, String label)
    {
        this.tabIndex = index;
        this.tabLabel = label;
        creativeTabArray[index] = this;
    }

    public int getTabIndex()
    {
        return this.tabIndex;
    }

    public String getTabLabel()
    {
        return this.tabLabel;
    }

    /**
     * Gets the translated Label.
     */
    public String getTranslatedTabLabel()
    {
        return "itemGroup." + this.getTabLabel();
    }

    public ItemStack getIconItemStack()
    {
        if (this.iconItemStack == null)
        {
            this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
        }

        return this.iconItemStack;
    }

    public abstract Item getTabIconItem();

    public int getIconItemDamage()
    {
        return 0;
    }

    public String getBackgroundImageName()
    {
        return this.theTexture;
    }

    public CreativeTabs setBackgroundImageName(String texture)
    {
        this.theTexture = texture;
        return this;
    }

    public boolean drawInForegroundOfTab()
    {
        return this.drawTitle;
    }

    public CreativeTabs setNoTitle()
    {
        this.drawTitle = false;
        return this;
    }

    public boolean shouldHidePlayerInventory()
    {
        return this.hasScrollbar;
    }

    public CreativeTabs setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    /**
     * returns index % 6
     */
    public int getTabColumn()
    {
        return this.tabIndex % 6;
    }

    /**
     * returns tabIndex < 6
     */
    public boolean isTabInFirstRow()
    {
        return this.tabIndex < 6;
    }

    /**
     * Returns the enchantment types relevant to this tab
     */
    public EnumEnchantmentType[] getRelevantEnchantmentTypes()
    {
        return this.enchantmentTypes;
    }

    /**
     * Sets the enchantment types for populating this tab with enchanting books
     */
    public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... types)
    {
        this.enchantmentTypes = types;
        return this;
    }

    public boolean hasRelevantEnchantmentType(EnumEnchantmentType enchantmentType)
    {
        if (this.enchantmentTypes == null)
        {
            return false;
        }
        else
        {
            for (EnumEnchantmentType enumenchantmenttype : this.enchantmentTypes)
            {
                if (enumenchantmenttype == enchantmentType)
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * only shows items which have tabToDisplayOn == this
     */
    public void displayAllReleventItems(List<ItemStack> p_78018_1_)
    {
        for (Item item : Item.itemRegistry)
        {
            if (item != null && item.getCreativeTab() == this)
            {
                item.getSubItems(item, this, p_78018_1_);
            }
        }

        if (this.getRelevantEnchantmentTypes() != null)
        {
            this.addEnchantmentBooksToList(p_78018_1_, this.getRelevantEnchantmentTypes());
        }
    }

    /**
     * Adds the enchantment books from the supplied EnumEnchantmentType to the given list.
     */
    public void addEnchantmentBooksToList(List<ItemStack> itemList, EnumEnchantmentType... enchantmentType)
    {
        for (Enchantment enchantment : Enchantment.enchantmentsBookList)
        {
            if (enchantment != null && enchantment.type != null)
            {
                boolean flag = false;

                for (int i = 0; i < enchantmentType.length && !flag; ++i)
                {
                    if (enchantment.type == enchantmentType[i])
                    {
                        flag = true;
                    }
                }

                if (flag)
                {
                    itemList.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
                }
            }
        }
    }
}
