package me.wavelength.baseclient;

import java.io.File;

import org.lwjgl.opengl.Display;

import com.google.common.collect.Lists;

import me.wavelength.baseclient.account.AccountManager;
import me.wavelength.baseclient.command.CommandManager;
import me.wavelength.baseclient.event.EventManager;
import me.wavelength.baseclient.font.Font;
import me.wavelength.baseclient.friends.FriendsManager;
import me.wavelength.baseclient.gui.altmanager.GuiAltManager;
import me.wavelength.baseclient.gui.clickgui.ClickGui;
import me.wavelength.baseclient.irc.IRCClient;
import me.wavelength.baseclient.module.ModuleManager;
import me.wavelength.baseclient.overlay.HotbarOverlay;
import me.wavelength.baseclient.overlay.TabGui1;
import me.wavelength.baseclient.overlay.ToggledModules1;
import me.wavelength.baseclient.thealtening.AltService;
import me.wavelength.baseclient.utils.Config;
import me.wavelength.baseclient.utils.Files;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Locale;

public class BaseClient {

	/**
	 * @formatter:off
	 * Credits
	 * 
	 * Fonts: Slick's font manager edited by Russian412 and color system by me
	 * Alt Manager: Russian412's Alt Manager with some small bug-fixes by me
	 * The Altening Implementation: Russian412
	 * 
	 * Everything else is made by me
	 * @formatter:on
	 **/

	private final String clientName = "BaseClient";
	private final String clientVersion = "0.1";
	private final String author = "WaveLength";

	public static BaseClient instance;

	private final String defaultUsername = "WaveLength";

	private EventManager eventManager;

	private FriendsManager friendsManager;

	private CommandManager commandManager;
	private ModuleManager moduleManager;

	private IRCClient ircClient;

	private AccountManager accountManager;

	private AltService altService;

	private Font font;

	private String packageBase = "me.wavelength.baseclient";

	private boolean defaultHotbar = false;

	private Config genericConfig;

	private ClickGui clickGui;

	private Locale englishLocale;

	public BaseClient() {
		instance = this;
	}

	public void initialize() {
		Display.setTitle(String.format("%1$s - %2$s | Loading...", clientName, clientVersion));

		this.englishLocale = new Locale();

		this.ircClient = new IRCClient("chat.freenode.net", 6667, Minecraft.getMinecraft().getSession().getUsername(), "#WaveLengthBaseClient");

		new GuiAltManager(); // We create the instance.

		String clientFolder = new File(".").getAbsolutePath();

		clientFolder = (clientFolder.contains("jars") ? new File(".").getAbsolutePath().substring(0, clientFolder.length() - 2) : new File(".").getAbsolutePath()) + Strings.getSplitter() + clientName;

		String accountManagerFolder = clientFolder + Strings.getSplitter() + "alts";

		Files.createRecursiveFolder(accountManagerFolder);

		this.accountManager = new AccountManager(new File(accountManagerFolder));

		this.clickGui = new ClickGui();

		this.eventManager = new EventManager();

		this.friendsManager = new FriendsManager();

		this.moduleManager = new ModuleManager();
		this.commandManager = new CommandManager(".");

		commandManager.registerCommands(); // Moved here to make sure the CommandManager instance is created, else the
											// "commandManager" variable in the Command class would be null (since we are
											// getting the CommandManager instance from this class)

		this.altService = new AltService();

		switchToMojang();

		this.genericConfig = new Config(new File(clientFolder + Strings.getSplitter() + "config.cfg"));
		genericConfig.addDefault("tabguicolor", "5556190");

		/** Setting a custom icon */

		/** Both 16x16 and 32x32 version encoded in Base64 */
		String icon16x16 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAACH1BMVEUcL0wcL0wcL0wcL0weME0dME0cL0wcL0wcL0wcL0wcL0wcL0wbL0wTJ0UUKEYdME0eME0aLUscL0wcL0wcL0wbLksfMU5CUmo/T2gZLUoXKkgkNlIbLkscL0wdME0XK0g3SGJfbIFkcYVkcYRea4A7TGUXK0gdME0dME0XK0g3R2Fda4BjcIRmc4Zhb4M7SmMXK0gdME0cL0wdME0dME0dME0bL0wmQlsaLUobLktFVGxKWHAjP1kcL0wdME0dME0dME0cL0waLksVKUcYK0kaLUoZLEkZOFIbOFIcOVMPLUgOLUgZN1EaLUoZLEkYK0kVKUcbLksoOlZjcIQ0RV8iNVEoOlYjNVEnOlUfM09RYHZRYHZDUmolN1MoOlYsPVg9TWYiNVEzRF6Fj59xfY9/ipp0gJJ8h5d/ipo/T2dUYnhKWnF3gpSFj599iJhxfY93gpQkNlIoOlVTYXdIV25XZXpKWXBPXXRLWHEfMU9MWnJFU21CUWpOXXRFVWw2R2BFVWwkNlIaLUsVKUcVKUYUKEYWKUcUKkUVMUUaM0oVLkYVMEUVK0YVKEYXKkgYK0kWKkcbLkwcL0weMU0eME0eMU0eL00dNUwbQUgaPEgbP0kbQUgdNEweME0dME0dME0cL0wcL0wcL0wcLkwcLE0cLU0cLU0cLU0cLkwcL0wcL0wcL0wcL0wcL0wcMEwcL0wcL0wcMEwcL0wcL0z///9JKFXpAAAAAWJLR0S0RAlq3QAAAAd0SU1FB+QDEAAECDwPk00AAADdSURBVBjTY2AgBjAyMbOwsrFzcDJCBbi4eXj5+AUEhYShAiKiYuISklLSMrJQATl5BUUlZRVVNXWICQwamlraOrp6+gaGIL6RsYmpmbmFpZW1ja2dvYMjg5Ozi6ubu4enl7ePr59/QCBDUHBIaFh4RGRUdExsXHxCIkNSckpqWnpGZlZ2Tm5efkEhQ1FxSWlZeUVlVXVNbV19QyNDU3NLa1t7R2dXd09vX/+EiQyTJk+ZOm36jJmzZs+ZO2++8QKQzQsXLV6ydNnyFStXQV26es3ades3bNy0ajOQAwAsgT/ObWFqTwAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAyMC0wMy0xNlQwMDowNDowOC0wNzowMAp3d0UAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMjAtMDMtMTZUMDA6MDQ6MDgtMDc6MDB7Ks/5AAAAAElFTkSuQmCC";
		String icon32x32 = "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";

		/** Calling the #setWindowIcon() method with the two encoded icons */
		Minecraft.getMinecraft().setWindowIcon(icon16x16, icon32x32);
	}

	public void afterMinecraft() {
		Display.setTitle(String.format("%1$s - %2$s", clientName, clientVersion));

		this.font = new Font(packageBase + ".font.fonts", "BwModelicaSS01-RegularCondensed", 50, 25, 30, 33);

		registerHuds();
	}

	private void registerHuds() {
		new HotbarOverlay();
		new ToggledModules1();
		new TabGui1();
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public String getAuthor() {
		return author;
	}

	public String getDefaultUsername() {
		return defaultUsername;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public FriendsManager getFriendsManager() {
		return friendsManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public IRCClient getIRCClient() {
		return ircClient;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public AltService getAltService() {
		return altService;
	}

	@Deprecated
	public Font getFontRenderer() {
		return font;
	}

	public Font getFont() {
		return font;
	}

	public String getPackageBase() {
		return packageBase;
	}

	public boolean isDefaultHotbar() {
		return defaultHotbar;
	}

	public Config getGenericConfig() {
		return genericConfig;
	}

	public ClickGui getClickGui() {
		return clickGui;
	}

	public Locale getEnglishLocale() {
		return englishLocale;
	}

	public void switchToMojang() {
		try {
			this.altService.switchService(AltService.EnumAltService.MOJANG);
		} catch (NoSuchFieldException e) {
			System.out.println("Couldn't switch to modank altservice");
		} catch (IllegalAccessException e) {
			System.out.println("Couldn't switch to modank altservice -2");
		}
	}

	public void switchToTheAltening() {
		try {
			this.altService.switchService(AltService.EnumAltService.THEALTENING);
		} catch (NoSuchFieldException e) {
			System.out.println("Couldn't switch to altening altservice");
		} catch (IllegalAccessException e) {
			System.out.println("Couldn't switch to altening altservice -2");
		}
	}

}