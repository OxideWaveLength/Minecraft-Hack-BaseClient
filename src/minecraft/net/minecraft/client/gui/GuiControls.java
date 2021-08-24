package net.minecraft.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiControls extends GuiScreen {
	private static final GameSettings.Options[] optionsArr = new GameSettings.Options[] { GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN };

	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	protected String screenTitle = "Controls";

	/** Reference to the GameSettings object. */
	private GameSettings options;

	/** The ID of the button that has been pressed. */
	public KeyBinding buttonId = null;
	public long time;
	private GuiKeyBindingList keyBindingList;
	private GuiButton buttonReset;

	public GuiControls(GuiScreen screen, GameSettings settings) {
		this.parentScreen = screen;
		this.options = settings;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.keyBindingList = new GuiKeyBindingList(this, this.mc);
		this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done", new Object[0])));
		this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll", new Object[0])));
		this.screenTitle = I18n.format("controls.title", new Object[0]);
		int i = 0;

		for (GameSettings.Options gamesettings$options : optionsArr) {
			if (gamesettings$options.getEnumFloat()) {
				this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
			} else {
				this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
			}

			++i;
		}
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.keyBindingList.handleMouseInput();
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 200) {
			this.mc.displayGuiScreen(this.parentScreen);
		} else if (button.id == 201) {
			for (KeyBinding keybinding : this.mc.gameSettings.keyBindings) {
				keybinding.setKeyCode(keybinding.getKeyCodeDefault());
			}

			KeyBinding.resetKeyBindingArrayAndHash();
		} else if (button.id < 100 && button instanceof GuiOptionButton) {
			this.options.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
			button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
		}
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (this.buttonId != null) {
			this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
			this.buttonId = null;
			KeyBinding.resetKeyBindingArrayAndHash();
		} else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	/**
	 * Called when a mouse button is released. Args : mouseX, mouseY, releaseButton
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
			super.mouseReleased(mouseX, mouseY, state);
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.buttonId != null) {
			if (keyCode == 1) {
				this.options.setOptionKeyBinding(this.buttonId, 0);
			} else if (keyCode != 0) {
				this.options.setOptionKeyBinding(this.buttonId, keyCode);
			} else if (typedChar > 0) {
				this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
			}

			this.buttonId = null;
			this.time = Minecraft.getSystemTime();
			KeyBinding.resetKeyBindingArrayAndHash();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
		boolean flag = true;

		for (KeyBinding keybinding : this.options.keyBindings) {
			if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
				flag = false;
				break;
			}
		}

		this.buttonReset.enabled = !flag;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
