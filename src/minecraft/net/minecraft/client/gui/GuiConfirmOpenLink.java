package net.minecraft.client.gui;

import java.io.IOException;

import net.minecraft.client.resources.I18n;

public class GuiConfirmOpenLink extends GuiYesNo {
	/** Text to warn players from opening unsafe links. */
	private final String openLinkWarning;

	/** Label for the Copy to Clipboard button. */
	private final String copyLinkButtonText;
	private final String linkText;
	private boolean showSecurityWarning = true;

	public GuiConfirmOpenLink(GuiYesNoCallback p_i1084_1_, String linkTextIn, int p_i1084_3_, boolean p_i1084_4_) {
		super(p_i1084_1_, I18n.format(p_i1084_4_ ? "chat.link.confirmTrusted" : "chat.link.confirm", new Object[0]), linkTextIn, p_i1084_3_);
		this.confirmButtonText = I18n.format(p_i1084_4_ ? "chat.link.open" : "gui.yes", new Object[0]);
		this.cancelButtonText = I18n.format(p_i1084_4_ ? "gui.cancel" : "gui.no", new Object[0]);
		this.copyLinkButtonText = I18n.format("chat.copy", new Object[0]);
		this.openLinkWarning = I18n.format("chat.link.warning", new Object[0]);
		this.linkText = linkTextIn;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 2) {
			this.copyLinkToClipboard();
		}

		this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
	}

	/**
	 * Copies the link to the system clipboard.
	 */
	public void copyLinkToClipboard() {
		setClipboardString(this.linkText);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.showSecurityWarning) {
			this.drawCenteredString(this.fontRendererObj, this.openLinkWarning, this.width / 2, 110, 16764108);
		}
	}

	public void disableSecurityWarning() {
		this.showSecurityWarning = false;
	}
}
