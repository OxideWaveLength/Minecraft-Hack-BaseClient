package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;

public class RealmsConnect {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen onlineScreen;
	private volatile boolean aborted = false;
	private NetworkManager connection;

	public RealmsConnect(RealmsScreen p_i1079_1_) {
		this.onlineScreen = p_i1079_1_;
	}

	public void connect(final String p_connect_1_, final int p_connect_2_) {
		Realms.setConnectedToRealms(true);
		(new Thread("Realms-connect-task") {
			public void run() {
				InetAddress inetaddress = null;

				try {
					inetaddress = InetAddress.getByName(p_connect_1_);

					if (RealmsConnect.this.aborted) {
						return;
					}

					RealmsConnect.this.connection = NetworkManager.func_181124_a(inetaddress, p_connect_2_, Minecraft.getMinecraft().gameSettings.func_181148_f());

					if (RealmsConnect.this.aborted) {
						return;
					}

					RealmsConnect.this.connection.setNetHandler(new NetHandlerLoginClient(RealmsConnect.this.connection, Minecraft.getMinecraft(), RealmsConnect.this.onlineScreen.getProxy()));

					if (RealmsConnect.this.aborted) {
						return;
					}

					RealmsConnect.this.connection.sendPacket(new C00Handshake(47, p_connect_1_, p_connect_2_, EnumConnectionState.LOGIN));

					if (RealmsConnect.this.aborted) {
						return;
					}

					RealmsConnect.this.connection.sendPacket(new C00PacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
				} catch (UnknownHostException unknownhostexception) {
					Realms.clearResourcePack();

					if (RealmsConnect.this.aborted) {
						return;
					}

					RealmsConnect.LOGGER.error((String) "Couldn\'t connect to world", (Throwable) unknownhostexception);
					Minecraft.getMinecraft().getResourcePackRepository().func_148529_f();
					Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { "Unknown host \'" + p_connect_1_ + "\'" })));
				} catch (Exception exception) {
					Realms.clearResourcePack();

					if (RealmsConnect.this.aborted) {
						return;
					}

					RealmsConnect.LOGGER.error((String) "Couldn\'t connect to world", (Throwable) exception);
					String s = exception.toString();

					if (inetaddress != null) {
						String s1 = inetaddress.toString() + ":" + p_connect_2_;
						s = s.replaceAll(s1, "");
					}

					Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[] { s })));
				}
			}
		}).start();
	}

	public void abort() {
		this.aborted = true;
	}

	public void tick() {
		if (this.connection != null) {
			if (this.connection.isChannelOpen()) {
				this.connection.processReceivedPackets();
			} else {
				this.connection.checkDisconnected();
			}
		}
	}
}
