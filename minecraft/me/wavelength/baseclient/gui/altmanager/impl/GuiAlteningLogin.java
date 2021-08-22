package me.wavelength.baseclient.gui.altmanager.impl;

import java.io.IOException;
import java.util.Objects;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.gui.altmanager.thread.AccountLoginThread;
import me.wavelength.baseclient.thealtening.AlteningAlt;
import me.wavelength.baseclient.thealtening.TheAltening;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;

public class GuiAlteningLogin extends GuiScreen {

	private GuiScreen previousScreen;

	public static AccountLoginThread thread;

	public static GuiTextField token;

	public static GuiTextField key;

	public GuiAlteningLogin(GuiScreen previousScreen) {
		this.previousScreen = previousScreen;
	}

	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -1:
			if (key.getText().isEmpty() || token.getText().isEmpty())
				return;
			BaseClient.instance.getAccountManager().setAlteningKey(key.getText());
			BaseClient.instance.getAccountManager().setLastAlteningAlt(token.getText());
			thread = new AccountLoginThread(token.getText().replaceAll(" ", ""), "gaymer");
			thread.run();
			BaseClient.instance.getAccountManager().save();
			break;
		case 0:
			if (key.getText().isEmpty())
				return;
			try {
				TheAltening theAltening = new TheAltening(key.getText());
				AlteningAlt account = theAltening.generateAccount(theAltening.getUser());
				token.setText(((AlteningAlt) Objects.<AlteningAlt>requireNonNull(account)).getToken());
				BaseClient.instance.getAccountManager().save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!token.getText().isEmpty()) {
				BaseClient.instance.getAccountManager().setAlteningKey(key.getText());
				BaseClient.instance.getAccountManager().setLastAlteningAlt(token.getText());
				thread = new AccountLoginThread(token.getText().replaceAll(" ", ""), "gaymer");
				thread.run();
				BaseClient.instance.getAccountManager().save();
			}
			break;
		case 1:
			this.mc.displayGuiScreen(this.previousScreen);
			break;
		case 2:
			this.mc.displayGuiScreen((GuiScreen) new GuiMultiplayer(this));
			break;
		case 3:
			if (key.getText().isEmpty() || BaseClient.instance.getAccountManager().getLastAlteningAlt() == null)
				return;
			BaseClient.instance.getAccountManager().setAlteningKey(key.getText());
			thread = new AccountLoginThread(BaseClient.instance.getAccountManager().getLastAlteningAlt().replaceAll(" ", ""), "gaymer");
			thread.run();
			BaseClient.instance.getAccountManager().save();
			break;
		}
	}

	public void drawScreen(int x, int y, float z) {
		drawDefaultBackground();
		ScaledResolution sr = new ScaledResolution(this.mc);
		token.drawTextBox();
		key.drawTextBox();
		drawCenteredString(this.mc.fontRendererObj, "The Altening Login", this.width / 2, sr.getScaledHeight() / 4, 16777215);
		drawCenteredString(this.fontRendererObj, this.mc.session.getUsername(), this.width / 2, sr.getScaledHeight() / 4 + 12, 16777215);
		if (token.getText().isEmpty() && !token.isFocused())
			drawString(this.mc.fontRendererObj, "Token", this.width / 2 - 94, this.height / 4 + 48, 16777215);
		if (key.getText().isEmpty() && !key.isFocused())
			drawString(this.mc.fontRendererObj, "Altening Key", this.width / 2 - 94, this.height / 4 + 78, 16777215);
		super.drawScreen(x, y, z);
	}

	public void initGui() {
		this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height / 4 + 124, "Login"));
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 148, "Generate and Login"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 196, "Back"));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 100, "Go to Multiplayer"));
		this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 172, "Last Alt"));
		token = new GuiTextField(this.height / 4 + 24, this.mc.fontRendererObj, this.width / 2 - 98, this.height / 4 + 42, 196, 20);
		token.setMaxStringLength(2147483647);
		key = new GuiTextField(this.height / 4 + 22, this.mc.fontRendererObj, this.width / 2 - 98, this.height / 4 + 72, 196, 20);
		key.setMaxStringLength(2147483647);
		if (BaseClient.instance.getAccountManager().getAlteningKey() != null)
			key.setText(BaseClient.instance.getAccountManager().getAlteningKey());
		Keyboard.enableRepeatEvents(true);
	}

	protected void keyTyped(char character, int key) {
		try {
			super.keyTyped(character, key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (character == '\t' && token.isFocused())
			token.setFocused(token.isFocused());
		if (character == '\r')
			actionPerformed(this.buttonList.get(0));
		if (character == '\t' && GuiAlteningLogin.key.isFocused())
			GuiAlteningLogin.key.setFocused(GuiAlteningLogin.key.isFocused());
		token.textboxKeyTyped(character, key);
		GuiAlteningLogin.key.textboxKeyTyped(character, key);
	}

	public void updateScreen() {
		token.updateCursorCounter();
		key.updateCursorCounter();
	}

	protected void mouseClicked(int x, int y, int button) {
		try {
			super.mouseClicked(x, y, button);
		} catch (IOException e) {
			e.printStackTrace();
		}
		token.mouseClicked(x, y, button);
		key.mouseClicked(x, y, button);
	}

}