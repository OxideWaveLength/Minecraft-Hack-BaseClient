package optfine;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiAnimationSettingsOF extends GuiScreen {
	private GuiScreen prevScreen;
	protected String title = "Animation Settings";
	private GameSettings settings;
	private static GameSettings.Options[] enumOptions = new GameSettings.Options[] { GameSettings.Options.ANIMATED_WATER, GameSettings.Options.ANIMATED_LAVA, GameSettings.Options.ANIMATED_FIRE, GameSettings.Options.ANIMATED_PORTAL, GameSettings.Options.ANIMATED_REDSTONE, GameSettings.Options.ANIMATED_EXPLOSION, GameSettings.Options.ANIMATED_FLAME, GameSettings.Options.ANIMATED_SMOKE, GameSettings.Options.VOID_PARTICLES, GameSettings.Options.WATER_PARTICLES, GameSettings.Options.RAIN_SPLASH, GameSettings.Options.PORTAL_PARTICLES, GameSettings.Options.POTION_PARTICLES, GameSettings.Options.DRIPPING_WATER_LAVA, GameSettings.Options.ANIMATED_TERRAIN, GameSettings.Options.ANIMATED_TEXTURES, GameSettings.Options.FIREWORK_PARTICLES, GameSettings.Options.PARTICLES };

	public GuiAnimationSettingsOF(GuiScreen p_i34_1_, GameSettings p_i34_2_) {
		this.prevScreen = p_i34_1_;
		this.settings = p_i34_2_;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		int i = 0;

		for (GameSettings.Options gamesettings$options : enumOptions) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 21 * (i / 2) - 10;

			if (!gamesettings$options.getEnumFloat()) {
				this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
			} else {
				this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
			}

			++i;
		}

		this.buttonList.add(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, "All ON"));
		this.buttonList.add(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, "All OFF"));
		this.buttonList.add(new GuiOptionButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done", new Object[0])));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id < 200 && button instanceof GuiOptionButton) {
				this.settings.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
				button.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
			}

			if (button.id == 200) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.prevScreen);
			}

			if (button.id == 210) {
				this.mc.gameSettings.setAllAnimations(true);
			}

			if (button.id == 211) {
				this.mc.gameSettings.setAllAnimations(false);
			}

			if (button.id != GameSettings.Options.CLOUD_HEIGHT.ordinal()) {
				ScaledResolution scaledresolution = new ScaledResolution(this.mc);
				int i = scaledresolution.getScaledWidth();
				int j = scaledresolution.getScaledHeight();
				this.setWorldAndResolution(this.mc, i, j);
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
}
