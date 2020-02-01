package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity
{
    COMMON(EnumChatFormatting.WHITE, "Common"),
    UNCOMMON(EnumChatFormatting.YELLOW, "Uncommon"),
    RARE(EnumChatFormatting.AQUA, "Rare"),
    EPIC(EnumChatFormatting.LIGHT_PURPLE, "Epic");

    /**
     * A decimal representation of the hex color codes of a the color assigned to this rarity type. (13 becomes d as in
     * \247d which is light purple)
     */
    public final EnumChatFormatting rarityColor;

    /** Rarity name. */
    public final String rarityName;

    private EnumRarity(EnumChatFormatting color, String name)
    {
        this.rarityColor = color;
        this.rarityName = name;
    }
}
