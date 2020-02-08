package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiListButton extends GuiButton {
	private boolean field_175216_o;

	/** The localization string used by this control. */
	private String localizationStr;

	/** The GuiResponder Object reference. */
	private final GuiPageButtonList.GuiResponder guiResponder;

	public GuiListButton(GuiPageButtonList.GuiResponder responder, int p_i45539_2_, int p_i45539_3_, int p_i45539_4_, String p_i45539_5_, boolean p_i45539_6_) {
		super(p_i45539_2_, p_i45539_3_, p_i45539_4_, 150, 20, "");
		this.localizationStr = p_i45539_5_;
		this.field_175216_o = p_i45539_6_;
		this.displayString = this.buildDisplayString();
		this.guiResponder = responder;
	}

	/**
	 * Builds the localized display string for this GuiListButton
	 */
	private String buildDisplayString() {
		return I18n.format(this.localizationStr, new Object[0]) + ": " + (this.field_175216_o ? I18n.format("gui.yes", new Object[0]) : I18n.format("gui.no", new Object[0]));
	}

	public void func_175212_b(boolean p_175212_1_) {
		this.field_175216_o = p_175212_1_;
		this.displayString = this.buildDisplayString();
		this.guiResponder.func_175321_a(this.id, p_175212_1_);
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (super.mousePressed(mc, mouseX, mouseY)) {
			this.field_175216_o = !this.field_175216_o;
			this.displayString = this.buildDisplayString();
			this.guiResponder.func_175321_a(this.id, this.field_175216_o);
			return true;
		} else {
			return false;
		}
	}
}
