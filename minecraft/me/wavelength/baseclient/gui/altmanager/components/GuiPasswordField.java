package me.wavelength.baseclient.gui.altmanager.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiPasswordField extends Gui {

	private final FontRenderer fontRenderer;

	private final int xPos;

	private final int yPos;

	private final int width;

	private final int height;

	private String text = "";

	private int maxStringLength = 50;

	private int cursorCounter;

	private boolean enableBackgroundDrawing = true;

	private boolean canLoseFocus = true;

	public boolean isFocused = false;

	private boolean isEnabled = true;

	private int field_73816_n = 0;

	private int cursorPosition = 0;

	private int selectionEnd = 0;

	private int enabledColor = 14737632;

	private int disabledColor = 7368816;

	private boolean field_73823_s = true;

	public GuiPasswordField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
		this.fontRenderer = par1FontRenderer;
		this.xPos = par2;
		this.yPos = par3;
		this.width = par4;
		this.height = par5;
	}

	public void updateCursorCounter() {
		this.cursorCounter++;
	}

	public void setText(String par1Str) {
		if (par1Str.length() > this.maxStringLength) {
			this.text = par1Str.substring(0, this.maxStringLength);
		} else {
			this.text = par1Str;
		}
		setCursorPositionEnd();
	}

	public String getText() {
		return text;
	}

	public String getSelectedtext() {
		int var1 = (this.cursorPosition < this.selectionEnd) ? this.cursorPosition : this.selectionEnd;
		int var2 = (this.cursorPosition < this.selectionEnd) ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(var1, var2);
	}

	public void writeText(String par1Str) {
		int var8;
		String var2 = "";
		String var3 = ChatAllowedCharacters.filterAllowedCharacters(par1Str);
		int var4 = (this.cursorPosition < this.selectionEnd) ? this.cursorPosition : this.selectionEnd;
		int var5 = (this.cursorPosition < this.selectionEnd) ? this.selectionEnd : this.cursorPosition;
		int var6 = this.maxStringLength - this.text.length() - var4 - this.selectionEnd;
		if (this.text.length() > 0)
			var2 = var2 + this.text.substring(0, var4);
		if (var6 < var3.length()) {
			var2 = var2 + var3.substring(0, var6);
			var8 = var6;
		} else {
			var2 = var2 + var3;
			var8 = var3.length();
		}
		if (this.text.length() > 0 && var5 < this.text.length())
			var2 = var2 + this.text.substring(var5);
		this.text = var2.replaceAll(" ", "");
		func_73784_d(var4 - this.selectionEnd + var8);
	}

	public void func_73779_a(int par1) {
		if (this.text.length() != 0)
			if (this.selectionEnd != this.cursorPosition) {
				writeText("");
			} else {
				deleteFromCursor(getNthWordFromCursor(par1) - this.cursorPosition);
			}
	}

	public void deleteFromCursor(int par1) {
		if (this.text.length() != 0)
			if (this.selectionEnd != this.cursorPosition) {
				writeText("");
			} else {
				boolean var2 = (par1 < 0);
				int var3 = var2 ? (this.cursorPosition + par1) : this.cursorPosition;
				int var4 = var2 ? this.cursorPosition : (this.cursorPosition + par1);
				String var5 = "";
				if (var3 >= 0)
					var5 = this.text.substring(0, var3);
				if (var4 < this.text.length())
					var5 = var5 + this.text.substring(var4);
				this.text = var5;
				if (var2)
					func_73784_d(par1);
			}
	}

	public int getNthWordFromCursor(int par1) {
		return getNthWordFromPos(par1, getCursorPosition());
	}

	public int getNthWordFromPos(int par1, int par2) {
		return func_73798_a(par1, getCursorPosition(), true);
	}

	public int func_73798_a(int par1, int par2, boolean par3) {
		int var4 = par2;
		boolean var5 = (par1 < 0);
		int var6 = Math.abs(par1);
		for (int var7 = 0; var7 < var6; var7++) {
			if (!var5) {
				int var8 = this.text.length();
				var4 = this.text.indexOf(' ', var4);
				if (var4 == -1) {
					var4 = var8;
				} else {
					while (par3 && var4 < var8 && this.text.charAt(var4) == ' ')
						var4++;
				}
			} else {
				while (par3 && var4 > 0 && this.text.charAt(var4 - 1) == ' ')
					var4--;
				while (var4 > 0 && this.text.charAt(var4 - 1) != ' ')
					var4--;
			}
		}
		return var4;
	}

	public void func_73784_d(int par1) {
		setCursorPosition(this.selectionEnd + par1);
	}

	public void setCursorPosition(int par1) {
		this.cursorPosition = par1;
		int var2 = this.text.length();
		if (this.cursorPosition < 0)
			this.cursorPosition = 0;
		if (this.cursorPosition > var2)
			this.cursorPosition = var2;
		func_73800_i(this.cursorPosition);
	}

	public void setCursorPositionZero() {
		setCursorPosition(0);
	}

	public void setCursorPositionEnd() {
		setCursorPosition(this.text.length());
	}

	public boolean textboxKeyTyped(char par1, int par2) {
		if (this.isEnabled && this.isFocused) {
			switch (par1) {
			case '\001':
				setCursorPositionEnd();
				func_73800_i(0);
				return true;
			case '\003':
				GuiScreen.setClipboardString(getSelectedtext());
				return true;
			case '\026':
				writeText(GuiScreen.getClipboardString());
				return true;
			case '\030':
				GuiScreen.setClipboardString(getSelectedtext());
				writeText("");
				return true;
			}
			switch (par2) {
			case 14:
				if (GuiScreen.isCtrlKeyDown()) {
					func_73779_a(-1);
				} else {
					deleteFromCursor(-1);
				}
				return true;
			case 199:
				if (GuiScreen.isShiftKeyDown()) {
					func_73800_i(0);
				} else {
					setCursorPositionZero();
				}
				return true;
			case 203:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						func_73800_i(getNthWordFromPos(-1, getSelectionEnd()));
					} else {
						func_73800_i(getSelectionEnd() - 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					setCursorPosition(getNthWordFromCursor(-1));
				} else {
					func_73784_d(-1);
				}
				return true;
			case 205:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						func_73800_i(getNthWordFromPos(1, getSelectionEnd()));
					} else {
						func_73800_i(getSelectionEnd() + 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					setCursorPosition(getNthWordFromCursor(1));
				} else {
					func_73784_d(1);
				}
				return true;
			case 207:
				if (GuiScreen.isShiftKeyDown()) {
					func_73800_i(this.text.length());
				} else {
					setCursorPositionEnd();
				}
				return true;
			case 211:
				if (GuiScreen.isCtrlKeyDown()) {
					func_73779_a(1);
				} else {
					deleteFromCursor(1);
				}
				return true;
			}
			if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
				writeText(Character.toString(par1));
				return true;
			}
			return false;
		}
		return false;
	}

	public void mouseClicked(int par1, int par2, int par3) {
		boolean var4 = (par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height);
		if (this.canLoseFocus)
			setFocused((this.isEnabled && var4));
		if (this.isFocused && par3 == 0) {
			int var5 = par1 - this.xPos;
			if (this.enableBackgroundDrawing)
				var5 -= 4;
			String var6 = this.fontRenderer.trimStringToWidth(this.text.substring(this.field_73816_n), getWidth());
			setCursorPosition(this.fontRenderer.trimStringToWidth(var6, var5).length() + this.field_73816_n);
		}
	}

	public void drawTextBox() {
		if (func_73778_q()) {
			if (getEnableBackgroundDrawing()) {
				drawRect((this.xPos - 1), (this.yPos - 1), (this.xPos + this.width + 1), (this.yPos + this.height + 1), -6250336);
				drawRect(this.xPos, this.yPos, (this.xPos + this.width), (this.yPos + this.height), -16777216);
			}
			int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
			int var2 = this.cursorPosition - this.field_73816_n;
			int var3 = this.selectionEnd - this.field_73816_n;
			String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.field_73816_n), getWidth());
			boolean var5 = (var2 >= 0 && var2 <= var4.length());
			boolean var6 = (this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5);
			int var7 = this.enableBackgroundDrawing ? (this.xPos + 4) : this.xPos;
			int var8 = this.enableBackgroundDrawing ? (this.yPos + (this.height - 8) / 2) : this.yPos;
			int var9 = var7;
			if (var3 > var4.length())
				var3 = var4.length();
			if (var4.length() > 0) {
				if (var5)
					var4.substring(0, var2);
				var9 = (Minecraft.getMinecraft()).fontRendererObj.drawStringWithShadow(this.text.replaceAll("(?s).", "*"), var7, var8, var1);
			}
			boolean var13 = (this.cursorPosition < this.text.length() || this.text.length() >= getMaxStringLength());
			int var11 = var9;
			if (!var5) {
				var11 = (var2 > 0) ? (var7 + this.width) : var7;
			} else if (var13) {
				var11 = var9 - 1;
				var9--;
			}
			if (var4.length() > 0 && var5 && var2 < var4.length())
				(Minecraft.getMinecraft()).fontRendererObj.drawStringWithShadow(var4.substring(var2), var9, var8, var1);
			if (var6)
				if (var13) {
					Gui.drawRect(var11, (var8 - 1), (var11 + 1), (var8 + 1 + this.fontRenderer.FONT_HEIGHT), -3092272);
				} else {
					(Minecraft.getMinecraft()).fontRendererObj.drawStringWithShadow("_", var11, var8, var1);
				}
		}
	}

	public void setMaxStringLength(int par1) {
		this.maxStringLength = par1;
		if (this.text.length() > par1)
			this.text = this.text.substring(0, par1);
	}

	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	public int getCursorPosition() {
		return this.cursorPosition;
	}

	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	public void setEnableBackgroundDrawing(boolean par1) {
		this.enableBackgroundDrawing = par1;
	}

	public void func_73794_g(int par1) {
		this.enabledColor = par1;
	}

	public void setFocused(boolean par1) {
		if (par1 && !this.isFocused)
			this.cursorCounter = 0;
		this.isFocused = par1;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	public int getWidth() {
		return getEnableBackgroundDrawing() ? (this.width - 8) : this.width;
	}

	public void func_73800_i(int par1) {
		int var2 = this.text.length();
		if (par1 > var2)
			par1 = var2;
		if (par1 < 0)
			par1 = 0;
		this.selectionEnd = par1;
		if (this.fontRenderer != null) {
			if (this.field_73816_n > var2)
				this.field_73816_n = var2;
			int var3 = getWidth();
			String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.field_73816_n), var3);
			int var5 = var4.length() + this.field_73816_n;
			if (par1 == this.field_73816_n)
				this.field_73816_n -= this.fontRenderer.trimStringToWidth(this.text, var3, true).length();
			if (par1 > var5) {
				this.field_73816_n += par1 - var5;
			} else if (par1 <= this.field_73816_n) {
				this.field_73816_n -= this.field_73816_n - par1;
			}
			if (this.field_73816_n < 0)
				this.field_73816_n = 0;
			if (this.field_73816_n > var2)
				this.field_73816_n = var2;
		}
	}

	public void setCanLoseFocus(boolean par1) {
		this.canLoseFocus = par1;
	}

	public boolean func_73778_q() {
		return this.field_73823_s;
	}

	public void func_73790_e(boolean par1) {
		this.field_73823_s = par1;
	}

}