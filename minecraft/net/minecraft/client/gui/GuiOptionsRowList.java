package net.minecraft.client.gui;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionsRowList extends GuiListExtended {
	private final List<GuiOptionsRowList.Row> field_148184_k = Lists.<GuiOptionsRowList.Row>newArrayList();

	public GuiOptionsRowList(Minecraft mcIn, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options... p_i45015_7_) {
		super(mcIn, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
		this.field_148163_i = false;

		for (int i = 0; i < p_i45015_7_.length; i += 2) {
			GameSettings.Options gamesettings$options = p_i45015_7_[i];
			GameSettings.Options gamesettings$options1 = i < p_i45015_7_.length - 1 ? p_i45015_7_[i + 1] : null;
			GuiButton guibutton = this.func_148182_a(mcIn, p_i45015_2_ / 2 - 155, 0, gamesettings$options);
			GuiButton guibutton1 = this.func_148182_a(mcIn, p_i45015_2_ / 2 - 155 + 160, 0, gamesettings$options1);
			this.field_148184_k.add(new GuiOptionsRowList.Row(guibutton, guibutton1));
		}
	}

	private GuiButton func_148182_a(Minecraft mcIn, int p_148182_2_, int p_148182_3_, GameSettings.Options p_148182_4_) {
		if (p_148182_4_ == null) {
			return null;
		} else {
			int i = p_148182_4_.returnEnumOrdinal();
			return (GuiButton) (p_148182_4_.getEnumFloat() ? new GuiOptionSlider(i, p_148182_2_, p_148182_3_, p_148182_4_) : new GuiOptionButton(i, p_148182_2_, p_148182_3_, p_148182_4_, mcIn.gameSettings.getKeyBinding(p_148182_4_)));
		}
	}

	/**
	 * Gets the IGuiListEntry object for the given index
	 */
	public GuiOptionsRowList.Row getListEntry(int index) {
		return (GuiOptionsRowList.Row) this.field_148184_k.get(index);
	}

	protected int getSize() {
		return this.field_148184_k.size();
	}

	/**
	 * Gets the width of the list
	 */
	public int getListWidth() {
		return 400;
	}

	protected int getScrollBarX() {
		return super.getScrollBarX() + 32;
	}

	public static class Row implements GuiListExtended.IGuiListEntry {
		private final Minecraft field_148325_a = Minecraft.getMinecraft();
		private final GuiButton field_148323_b;
		private final GuiButton field_148324_c;

		public Row(GuiButton p_i45014_1_, GuiButton p_i45014_2_) {
			this.field_148323_b = p_i45014_1_;
			this.field_148324_c = p_i45014_2_;
		}

		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			if (this.field_148323_b != null) {
				this.field_148323_b.yPosition = y;
				this.field_148323_b.drawButton(this.field_148325_a, mouseX, mouseY);
			}

			if (this.field_148324_c != null) {
				this.field_148324_c.yPosition = y;
				this.field_148324_c.drawButton(this.field_148325_a, mouseX, mouseY);
			}
		}

		public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
			if (this.field_148323_b.mousePressed(this.field_148325_a, p_148278_2_, p_148278_3_)) {
				if (this.field_148323_b instanceof GuiOptionButton) {
					this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton) this.field_148323_b).returnEnumOptions(), 1);
					this.field_148323_b.displayString = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148323_b.id));
				}

				return true;
			} else if (this.field_148324_c != null && this.field_148324_c.mousePressed(this.field_148325_a, p_148278_2_, p_148278_3_)) {
				if (this.field_148324_c instanceof GuiOptionButton) {
					this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton) this.field_148324_c).returnEnumOptions(), 1);
					this.field_148324_c.displayString = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148324_c.id));
				}

				return true;
			} else {
				return false;
			}
		}

		public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			if (this.field_148323_b != null) {
				this.field_148323_b.mouseReleased(x, y);
			}

			if (this.field_148324_c != null) {
				this.field_148324_c.mouseReleased(x, y);
			}
		}

		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
		}
	}
}
