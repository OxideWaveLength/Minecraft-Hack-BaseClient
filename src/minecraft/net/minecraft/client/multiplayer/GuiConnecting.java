package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.ServerConnectingEvent;
import me.wavelength.baseclient.event.events.ServerJoinEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class GuiConnecting extends GuiScreen {
	private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private NetworkManager networkManager;
	private boolean cancel;
	private final GuiScreen previousGuiScreen;

	public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_) {
		this.mc = mcIn;
		this.previousGuiScreen = p_i1181_1_;
		ServerAddress serveraddress = ServerAddress.func_78860_a(p_i1181_3_.serverIP);
		mcIn.loadWorld((WorldClient) null);
		mcIn.setServerData(p_i1181_3_);
		this.connect(serveraddress.getIP(), serveraddress.getPort());
	}

	public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port) {
		this.mc = mcIn;
		this.previousGuiScreen = p_i1182_1_;
		mcIn.loadWorld((WorldClient) null);
		this.connect(hostName, port);
	}

	private void connect(final String ip, final int port) {
		logger.info("Connecting to " + ip + ", " + port);
		(new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
			public void run() {
				InetAddress inetaddress = null;

				try {
					if (GuiConnecting.this.cancel) {
						return;
					}

					/** Handle the ServerConnectingEvent - Start */
					ServerConnectingEvent event = ((ServerConnectingEvent) BaseClient.instance.getEventManager().call(new ServerConnectingEvent(ip, port)));

					cancel = event.isCancelled();

					if (cancel) {
						GuiConnecting.logger.error((String) "Connection to the server cancelled");
						GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { event.getCancelReason() }), false));
						return;
					}
					/** Handle the ServerConnectingEvent - End */

					inetaddress = InetAddress.getByName(ip);
					GuiConnecting.this.networkManager = NetworkManager.func_181124_a(inetaddress, port, GuiConnecting.this.mc.gameSettings.func_181148_f());
					GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
					GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
					GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));

					/** Handle the ServerJoinEvent - Start */
					new Thread(() -> {
						while (mc.currentScreen == null && mc.theWorld == null && mc.thePlayer == null && mc.getCurrentServerData() == null)
							synchronized (BaseClient.instance.getEventManager()) {
								if (cancel)
									return;
							}

						if (!mc.getCurrentServerData().isConnected())
							return;

						BaseClient.instance.getEventManager().call(new ServerJoinEvent(mc.getCurrentServerData()));
					}).start();
					/** Handle the ServerJoinEvent - End */
				} catch (UnknownHostException unknownhostexception) {
					if (GuiConnecting.this.cancel) {
						return;
					}

					GuiConnecting.logger.error((String) "Couldn\'t connect to server", (Throwable) unknownhostexception);
					GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { "Unknown host" })));
					cancel = true;
				} catch (Exception exception) {
					if (GuiConnecting.this.cancel) {
						return;
					}

					GuiConnecting.logger.error((String) "Couldn\'t connect to server", (Throwable) exception);
					String s = exception.toString();

					if (inetaddress != null) {
						String s1 = inetaddress.toString() + ":" + port;
						s = s.replaceAll(s1, "");
					}

					GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { s })));
					cancel = true;
				}
			}
		}).start();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (this.networkManager != null) {
			if (this.networkManager.isChannelOpen()) {
				this.networkManager.processReceivedPackets();
			} else {
				this.networkManager.checkDisconnected();
			}
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.cancel = true;

			if (this.networkManager != null) {
				this.networkManager.closeChannel(new ChatComponentText("Aborted"));
			}

			this.mc.displayGuiScreen(this.previousGuiScreen);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		if (this.networkManager == null) {
			this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
		} else {
			this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
