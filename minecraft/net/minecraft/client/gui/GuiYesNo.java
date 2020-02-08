package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.I18n;

public class GuiYesNo extends GuiScreen {
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	protected GuiYesNoCallback parentScreen;
	protected String messageLine1;
	private String messageLine2;
	private final List<String> field_175298_s = Lists.<String>newArrayList();

	/** The text shown for the first button in GuiYesNo */
	protected String confirmButtonText;

	/** The text shown for the second button in GuiYesNo */
	protected String cancelButtonText;
	protected int parentButtonClickedId;
	private int ticksUntilEnable;

	public GuiYesNo(GuiYesNoCallback p_i1082_1_, String p_i1082_2_, String p_i1082_3_, int p_i1082_4_) {
		this.parentScreen = p_i1082_1_;
		this.messageLine1 = p_i1082_2_;
		this.messageLine2 = p_i1082_3_;
		this.parentButtonClickedId = p_i1082_4_;
		this.confirmButtonText = I18n.format("gui.yes", new Object[0]);
		this.cancelButtonText = I18n.format("gui.no", new Object[0]);
	}

	public GuiYesNo(GuiYesNoCallback p_i1083_1_, String p_i1083_2_, String p_i1083_3_, String p_i1083_4_, String p_i1083_5_, int p_i1083_6_) {
		this.parentScreen = p_i1083_1_;
		this.messageLine1 = p_i1083_2_;
		this.messageLine2 = p_i1083_3_;
		this.confirmButtonText = p_i1083_4_;
		this.cancelButtonText = p_i1083_5_;
		this.parentButtonClickedId = p_i1083_6_;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
		this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
		this.field_175298_s.clear();
		this.field_175298_s.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
		int i = 90;

		for (String s : this.field_175298_s) {
			this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
			i += this.fontRendererObj.FONT_HEIGHT;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Sets the number of ticks to wait before enabling the buttons.
	 */
	public void setButtonDelay(int p_146350_1_) {
		this.ticksUntilEnable = p_146350_1_;

		for (GuiButton guibutton : this.buttonList) {
			guibutton.enabled = false;
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();

		if (--this.ticksUntilEnable == 0) {
			for (GuiButton guibutton : this.buttonList) {
				guibutton.enabled = true;
			}
		}
	}
}
