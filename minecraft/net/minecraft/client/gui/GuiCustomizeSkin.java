package net.minecraft.client.gui;

import java.io.IOException;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class GuiCustomizeSkin extends GuiScreen {
	/** The parent GUI for this GUI */
	private final GuiScreen parentScreen;

	/** The title of the GUI. */
	private String title;

	public GuiCustomizeSkin(GuiScreen parentScreenIn) {
		this.parentScreen = parentScreenIn;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		int i = 0;
		this.title = I18n.format("options.skinCustomisation.title", new Object[0]);

		for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
			this.buttonList.add(new GuiCustomizeSkin.ButtonPart(enumplayermodelparts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
			++i;
		}

		if (i % 2 == 1) {
			++i;
		}

		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done", new Object[0])));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 200) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentScreen);
			} else if (button instanceof GuiCustomizeSkin.ButtonPart) {
				EnumPlayerModelParts enumplayermodelparts = ((GuiCustomizeSkin.ButtonPart) button).playerModelParts;
				this.mc.gameSettings.switchModelPartEnabled(enumplayermodelparts);
				button.displayString = this.func_175358_a(enumplayermodelparts);
			}
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private String func_175358_a(EnumPlayerModelParts playerModelParts) {
		String s;

		if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
			s = I18n.format("options.on", new Object[0]);
		} else {
			s = I18n.format("options.off", new Object[0]);
		}

		return playerModelParts.func_179326_d().getFormattedText() + ": " + s;
	}

	class ButtonPart extends GuiButton {
		private final EnumPlayerModelParts playerModelParts;

		private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts playerModelParts) {
			super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.func_175358_a(playerModelParts));
			this.playerModelParts = playerModelParts;
		}
	}
}
