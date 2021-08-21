package net.minecraft.realms;

import java.lang.reflect.Constructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class RealmsBridge extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private GuiScreen previousScreen;

	public void switchToRealms(GuiScreen p_switchToRealms_1_) {
		this.previousScreen = p_switchToRealms_1_;

		try {
			Class<?> oclass = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
			Constructor<?> constructor = oclass.getDeclaredConstructor(new Class[] { RealmsScreen.class });
			constructor.setAccessible(true);
			Object object = constructor.newInstance(new Object[] { this });
			Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen) object).getProxy());
		} catch (Exception exception) {
			LOGGER.error((String) "Realms module missing", (Throwable) exception);
		}
	}

	public void init() {
		Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
	}
}
