package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class GuiSlotRealmsProxy extends GuiSlot
{
    private final RealmsScrolledSelectionList selectionList;

    public GuiSlotRealmsProxy(RealmsScrolledSelectionList selectionListIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.selectionList = selectionListIn;
    }

    protected int getSize()
    {
        return this.selectionList.getItemCount();
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
        this.selectionList.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return this.selectionList.isSelectedItem(slotIndex);
    }

    protected void drawBackground()
    {
        this.selectionList.renderBackground();
    }

    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
    {
        this.selectionList.renderItem(entryID, p_180791_2_, p_180791_3_, p_180791_4_, mouseXIn, mouseYIn);
    }

    public int func_154338_k()
    {
        return super.width;
    }

    public int func_154339_l()
    {
        return super.mouseY;
    }

    public int func_154337_m()
    {
        return super.mouseX;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.selectionList.getMaxPosition();
    }

    protected int getScrollBarX()
    {
        return this.selectionList.getScrollbarPosition();
    }

    public void handleMouseInput()
    {
        super.handleMouseInput();
    }
}
