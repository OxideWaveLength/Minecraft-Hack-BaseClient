package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
	public GuiListExtended(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
	}

	/**
	 * The element in the slot that was clicked, boolean for whether it was double
	 * clicked or not
	 */
	protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
	}

	/**
	 * Returns true if the element passed in is currently selected
	 */
	protected boolean isSelected(int slotIndex) {
		return false;
	}

	protected void drawBackground() {
	}

	protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
		this.getListEntry(entryID).drawEntry(entryID, p_180791_2_, p_180791_3_, this.getListWidth(), p_180791_4_, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
	}

	protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_) {
		this.getListEntry(p_178040_1_).setSelected(p_178040_1_, p_178040_2_, p_178040_3_);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		if (this.isMouseYWithinSlotBounds(mouseY)) {
			int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

			if (i >= 0) {
				int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
				int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
				int l = mouseX - j;
				int i1 = mouseY - k;

				if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, i1)) {
					this.setEnabled(false);
					return true;
				}
			}
		}

		return false;
	}

	public boolean mouseReleased(int p_148181_1_, int p_148181_2_, int p_148181_3_) {
		for (int i = 0; i < this.getSize(); ++i) {
			int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
			int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
			int l = p_148181_1_ - j;
			int i1 = p_148181_2_ - k;
			this.getListEntry(i).mouseReleased(i, p_148181_1_, p_148181_2_, p_148181_3_, l, i1);
		}

		this.setEnabled(true);
		return false;
	}

	/**
	 * Gets the IGuiListEntry object for the given index
	 */
	public abstract GuiListExtended.IGuiListEntry getListEntry(int index);

	public interface IGuiListEntry {
		void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_);

		void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

		boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_);

		void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
	}
}
