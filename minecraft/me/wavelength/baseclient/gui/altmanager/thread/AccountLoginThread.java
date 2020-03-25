package me.wavelength.baseclient.gui.altmanager.thread;

import java.net.Proxy;
import java.util.UUID;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.account.Account;
import me.wavelength.baseclient.gui.altmanager.GuiAltManager;
import me.wavelength.baseclient.gui.altmanager.impl.GuiAlteningLogin;
import me.wavelength.baseclient.thealtening.AltService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.util.Session;

public class AccountLoginThread extends Thread {

	private String email;

	private String password;

	public static boolean unknownBoolean1;

	private String status = "&eWaiting for login...";

	public AccountLoginThread(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public void run() {
		if ((Minecraft.getMinecraft()).currentScreen instanceof GuiAlteningLogin || GuiDisconnected.useTheAltening) {
			BaseClient.instance.switchToTheAltening();
			unknownBoolean1 = false;
			GuiDisconnected.useTheAltening = false;
		} else if (unknownBoolean1 == true) {
			try {
				BaseClient.instance.getAltService().switchService(AltService.EnumAltService.MOJANG);
			} catch (NoSuchFieldException e) {
				System.out.println("Couldnt switch to modank altservice");
			} catch (IllegalAccessException e) {
				System.out.println("Couldnt switch to modank altservice -2");
			}
		}

		if (password == null || password.isEmpty()) {
			Minecraft.getMinecraft().session = new Session(this.email, "", "", "mojang");
			this.status = "&aLogged in as &ecracked&a.";
			return;
		}

		unknownBoolean1 = true;
		this.status = "&6Logging in...";
		YggdrasilAuthenticationService yService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
		UserAuthentication userAuth = yService.createUserAuthentication(Agent.MINECRAFT);
		if (userAuth == null) {
			this.status = "&4Unknown error.";
			return;
		}
		userAuth.setUsername(this.email);
		userAuth.setPassword(this.password);
		try {
			userAuth.logIn();
			Session session = new Session(userAuth.getSelectedProfile().getName(), userAuth.getSelectedProfile().getId().toString(), userAuth.getAuthenticatedToken(), this.email.contains("@") ? "mojang" : "legacy");
			Minecraft.getMinecraft().session = session;
			Account account = BaseClient.instance.getAccountManager().getAccountByEmail(this.email);
			account = (account == null ? new Account(email, password, session.getUsername()) : account);
			account.setName(session.getUsername());
			if (!((Minecraft.getMinecraft()).currentScreen instanceof GuiAlteningLogin) && !((Minecraft.getMinecraft()).currentScreen instanceof GuiDisconnected))
				BaseClient.instance.getAccountManager().setLastAlt(account);
			BaseClient.instance.getAccountManager().save();
			GuiAltManager.INSTANCE.currentAccount = account;
			if (unknownBoolean1 == true) {
				this.status = String.format("&aLogged in as %s.", account.getName());
			}
		} catch (AuthenticationException exception) {
			this.status = "&4Login failed.";
		} catch (NullPointerException exception) {
			this.status = "&4Unknown error.";
		}
	}

	public String getStatus() {
		return this.status;
	}

}