package me.wavelength.baseclient.gui.altmanager.impl;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.account.Account;
import me.wavelength.baseclient.gui.altmanager.components.GuiPasswordField;
import me.wavelength.baseclient.gui.altmanager.thread.AccountLoginThread;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiAltLogin extends GuiScreen {

	private GuiTextField email;

	private GuiPasswordField password;

	private AccountLoginThread loginThread;

	private GuiScreen parent;

	public GuiAltLogin(GuiScreen parent) {
		this.parent = parent;
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Login"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Back"));
		this.email = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
		this.email.setMaxStringLength(2147483647);
		this.email.setFocused(true);
		this.password = new GuiPasswordField(this.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
		this.password.setMaxStringLength(2147483647);
	}

	public void keyTyped(char character, int keyCode) throws IOException {
		this.email.textboxKeyTyped(character, keyCode);
		this.password.textboxKeyTyped(character, keyCode);
		if (keyCode == 15) {
			this.email.setFocused(!this.email.isFocused());
			this.password.setFocused(!this.password.isFocused());
		}
		
		if (keyCode == 28)
			actionPerformed(this.buttonList.get(0));
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.email.mouseClicked(mouseX, mouseY, mouseButton);
		this.password.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(this.mc.fontRendererObj, "Direct Login", this.width / 2, 20, -1);
		if (this.email.getText().isEmpty())
			drawString(this.mc.fontRendererObj, "Username / Email", this.width / 2 - 96, 66, -7829368);
		if (this.password.getText().isEmpty())
			drawString(this.mc.fontRendererObj, "Password", this.width / 2 - 96, 106, -7829368);
		this.email.drawTextBox();
		this.password.drawTextBox();
		drawCenteredString(this.mc.fontRendererObj, Strings.simpleTranslateColors((this.loginThread == null) ? "&eWaiting for login..." : this.loginThread.getStatus()), this.width / 2, 30, -1);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			if (!this.email.getText().isEmpty()) {
				if (this.email.getText().contains(":")) {
					String[] split = this.email.getText().split(":");
					Account account1 = new Account(split[0], split[1], split[0]);
					this.loginThread = new AccountLoginThread(account1.getEmail(), account1.getPassword());
					this.loginThread.start();
				}
				Account account = new Account(this.email.getText(), this.password.getText(), this.email.getText());
				this.loginThread = new AccountLoginThread(account.getEmail(), account.getPassword());
				this.loginThread.start();
			}
			break;
		case 1:
			this.mc.displayGuiScreen(this.parent);
			break;
		}
	}

}