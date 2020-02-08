package me.wavelength.baseclient;

import java.io.File;
import java.io.InputStream;

import org.lwjgl.opengl.Display;

import me.wavelength.baseclient.account.AccountManager;
import me.wavelength.baseclient.command.CommandManager;
import me.wavelength.baseclient.event.EventManager;
import me.wavelength.baseclient.font.NahrFont;
import me.wavelength.baseclient.gui.GuiAltManager;
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

public class BaseClient {

	/**
	 * @formatter:off
	 * Credits
	 * 
	 * Fonts: Russian412's ported version of the NahrFont to 1.8.8
	 * Alt Manager: Russian412's Alt Manager with some small bug-fixes by me
	 * The Altening Implementation: Russian412
	 * 
	 * Everything else is made by me
	 * @formatter:on
	 **/

	private String clientName = "BaseClient";
	private String clientVersion = "0.1";
	private String author = "WaveLength";

	public static BaseClient instance;

	private String defaultUsername = "WaveLength";

	private EventManager eventManager;
	private CommandManager commandManager;
	private ModuleManager moduleManager;

	private IRCClient ircClient;

	private AccountManager accountManager;

	private AltService altService;

	private NahrFont fontRenderer;

	private String packageBase = "me.wavelength.baseclient";

	private boolean defaultHotbar = false;

	private Config genericConfig;

	public BaseClient() {
		instance = this;
	}

	public void initialize() {
		Display.setTitle(String.format("%1$s - %2$s | Loading...", clientName, clientVersion));

		ircClient = new IRCClient("chat.freenode.net", 6667, Minecraft.getMinecraft().getSession().getUsername(), "#WaveLengthBaseClient");

		new GuiAltManager(); // We create the instance.

		this.eventManager = new EventManager();
		this.moduleManager = new ModuleManager();
		this.commandManager = new CommandManager(".");

		commandManager.registerCommands(); // Moved here to make sure the CommandManager instance is created, else the
											// "commandManager" variable in the Command class would be null (since we are
											// getting the CommandManager instance from this class)

		this.altService = new AltService();

		String clientFolder = new File(".").getAbsolutePath();

		clientFolder = (clientFolder.contains("jars") ? new File(".").getAbsolutePath().substring(0, clientFolder.length() - 2) : new File(".").getAbsolutePath()) + Strings.getSplitter() + clientName;

		String accountManagerFolder = clientFolder + Strings.getSplitter() + "alts";

		Files.createRecursiveFolder(accountManagerFolder);

		this.accountManager = new AccountManager(new File(accountManagerFolder));
		switchToMojang();

		this.genericConfig = new Config(new File(clientFolder + Strings.getSplitter() + "config.cfg"));
		genericConfig.addDefault("tabguicolor", "5556190");
	}

	public void afterMinecraft() {
		Display.setTitle(String.format("%1$s - %2$s", clientName, clientVersion));

		Object font = "Verdana";

		InputStream stream = BaseClient.class.getResourceAsStream(("/" + packageBase.replace(".", "/") + "/font/fonts/" + "BwModelicaSS01-RegularCondensed.ttf"));

		if (stream != null)
			font = stream;

		this.fontRenderer = new NahrFont(font, (stream == null ? 20 : 25)); // If the font looks weird, change the font size

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

	public NahrFont getFontRenderer() {
		return fontRenderer;
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