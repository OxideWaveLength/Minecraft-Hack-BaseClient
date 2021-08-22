package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class RealmsServerStatusPinger {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<NetworkManager> connections = Collections.<NetworkManager>synchronizedList(Lists.<NetworkManager>newArrayList());

	public void pingServer(final String p_pingServer_1_, final RealmsServerPing p_pingServer_2_) throws UnknownHostException {
		if (p_pingServer_1_ != null && !p_pingServer_1_.startsWith("0.0.0.0") && !p_pingServer_1_.isEmpty()) {
			RealmsServerAddress realmsserveraddress = RealmsServerAddress.parseString(p_pingServer_1_);
			final NetworkManager networkmanager = NetworkManager.func_181124_a(InetAddress.getByName(realmsserveraddress.getHost()), realmsserveraddress.getPort(), false);
			this.connections.add(networkmanager);
			networkmanager.setNetHandler(new INetHandlerStatusClient() {
				private boolean field_154345_e = false;

				public void handleServerInfo(S00PacketServerInfo packetIn) {
					ServerStatusResponse serverstatusresponse = packetIn.getResponse();

					if (serverstatusresponse.getPlayerCountData() != null) {
						p_pingServer_2_.nrOfPlayers = String.valueOf(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount());

						if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayerCountData().getPlayers())) {
							StringBuilder stringbuilder = new StringBuilder();

							for (GameProfile gameprofile : serverstatusresponse.getPlayerCountData().getPlayers()) {
								if (stringbuilder.length() > 0) {
									stringbuilder.append("\n");
								}

								stringbuilder.append(gameprofile.getName());
							}

							if (serverstatusresponse.getPlayerCountData().getPlayers().length < serverstatusresponse.getPlayerCountData().getOnlinePlayerCount()) {
								if (stringbuilder.length() > 0) {
									stringbuilder.append("\n");
								}

								stringbuilder.append("... and ").append(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() - serverstatusresponse.getPlayerCountData().getPlayers().length).append(" more ...");
							}

							p_pingServer_2_.playerList = stringbuilder.toString();
						}
					} else {
						p_pingServer_2_.playerList = "";
					}

					networkmanager.sendPacket(new C01PacketPing(Realms.currentTimeMillis()));
					this.field_154345_e = true;
				}

				public void handlePong(S01PacketPong packetIn) {
					networkmanager.closeChannel(new ChatComponentText("Finished"));
				}

				public void onDisconnect(IChatComponent reason) {
					if (!this.field_154345_e) {
						RealmsServerStatusPinger.LOGGER.error("Can\'t ping " + p_pingServer_1_ + ": " + reason.getUnformattedText());
					}
				}
			});

			try {
				networkmanager.sendPacket(new C00Handshake(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, realmsserveraddress.getHost(), realmsserveraddress.getPort(), EnumConnectionState.STATUS));
				networkmanager.sendPacket(new C00PacketServerQuery());
			} catch (Throwable throwable) {
				LOGGER.error((Object) throwable);
			}
		}
	}

	public void tick() {
		synchronized (this.connections) {
			Iterator<NetworkManager> iterator = this.connections.iterator();

			while (iterator.hasNext()) {
				NetworkManager networkmanager = (NetworkManager) iterator.next();

				if (networkmanager.isChannelOpen()) {
					networkmanager.processReceivedPackets();
				} else {
					iterator.remove();
					networkmanager.checkDisconnected();
				}
			}
		}
	}

	public void removeAll() {
		synchronized (this.connections) {
			Iterator<NetworkManager> iterator = this.connections.iterator();

			while (iterator.hasNext()) {
				NetworkManager networkmanager = (NetworkManager) iterator.next();

				if (networkmanager.isChannelOpen()) {
					iterator.remove();
					networkmanager.closeChannel(new ChatComponentText("Cancelled"));
				}
			}
		}
	}
}
