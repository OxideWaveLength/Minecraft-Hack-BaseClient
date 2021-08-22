package net.minecraft.server.network;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
	private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final Random RANDOM = new Random();
	private final byte[] verifyToken = new byte[4];
	private final MinecraftServer server;
	public final NetworkManager networkManager;
	private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;

	/** How long has player been trying to login into the server. */
	private int connectionTimer;
	private GameProfile loginGameProfile;
	private String serverId = "";
	private SecretKey secretKey;
	private EntityPlayerMP field_181025_l;

	public NetHandlerLoginServer(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_) {
		this.server = p_i45298_1_;
		this.networkManager = p_i45298_2_;
		RANDOM.nextBytes(this.verifyToken);
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
			this.tryAcceptPlayer();
		} else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
			EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());

			if (entityplayermp == null) {
				this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
				this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.field_181025_l);
				this.field_181025_l = null;
			}
		}

		if (this.connectionTimer++ == 600) {
			this.closeConnection("Took too long to log in");
		}
	}

	public void closeConnection(String reason) {
		try {
			logger.info("Disconnecting " + this.getConnectionInfo() + ": " + reason);
			ChatComponentText chatcomponenttext = new ChatComponentText(reason);
			this.networkManager.sendPacket(new S00PacketDisconnect(chatcomponenttext));
			this.networkManager.closeChannel(chatcomponenttext);
		} catch (Exception exception) {
			logger.error((String) "Error whilst disconnecting player", (Throwable) exception);
		}
	}

	public void tryAcceptPlayer() {
		if (!this.loginGameProfile.isComplete()) {
			this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
		}

		String s = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);

		if (s != null) {
			this.closeConnection(s);
		} else {
			this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;

			if (this.server.getNetworkCompressionTreshold() >= 0 && !this.networkManager.isLocalChannel()) {
				this.networkManager.sendPacket(new S03PacketEnableCompression(this.server.getNetworkCompressionTreshold()), new ChannelFutureListener() {
					public void operationComplete(ChannelFuture p_operationComplete_1_) throws Exception {
						NetHandlerLoginServer.this.networkManager.setCompressionTreshold(NetHandlerLoginServer.this.server.getNetworkCompressionTreshold());
					}
				}, new GenericFutureListener[0]);
			}

			this.networkManager.sendPacket(new S02PacketLoginSuccess(this.loginGameProfile));
			EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());

			if (entityplayermp != null) {
				this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
				this.field_181025_l = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
			} else {
				this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
			}
		}
	}

	/**
	 * Invoked when disconnecting, the parameter is a ChatComponent describing the
	 * reason for termination
	 */
	public void onDisconnect(IChatComponent reason) {
		logger.info(this.getConnectionInfo() + " lost connection: " + reason.getUnformattedText());
	}

	public String getConnectionInfo() {
		return this.loginGameProfile != null ? this.loginGameProfile.toString() + " (" + this.networkManager.getRemoteAddress().toString() + ")" : String.valueOf((Object) this.networkManager.getRemoteAddress());
	}

	public void processLoginStart(C00PacketLoginStart packetIn) {
		Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet", new Object[0]);
		this.loginGameProfile = packetIn.getProfile();

		if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
			this.currentLoginState = NetHandlerLoginServer.LoginState.KEY;
			this.networkManager.sendPacket(new S01PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.verifyToken));
		} else {
			this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
		}
	}

	public void processEncryptionResponse(C01PacketEncryptionResponse packetIn) {
		Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet", new Object[0]);
		PrivateKey privatekey = this.server.getKeyPair().getPrivate();

		if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey))) {
			throw new IllegalStateException("Invalid nonce!");
		} else {
			this.secretKey = packetIn.getSecretKey(privatekey);
			this.currentLoginState = NetHandlerLoginServer.LoginState.AUTHENTICATING;
			this.networkManager.enableEncryption(this.secretKey);
			(new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
				public void run() {
					GameProfile gameprofile = NetHandlerLoginServer.this.loginGameProfile;

					try {
						String s = (new BigInteger(CryptManager.getServerIdHash(NetHandlerLoginServer.this.serverId, NetHandlerLoginServer.this.server.getKeyPair().getPublic(), NetHandlerLoginServer.this.secretKey))).toString(16);
						NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s);

						if (NetHandlerLoginServer.this.loginGameProfile != null) {
							NetHandlerLoginServer.logger.info("UUID of player " + NetHandlerLoginServer.this.loginGameProfile.getName() + " is " + NetHandlerLoginServer.this.loginGameProfile.getId());
							NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
						} else if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
							NetHandlerLoginServer.logger.warn("Failed to verify username but will let them in anyway!");
							NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
							NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
						} else {
							NetHandlerLoginServer.this.closeConnection("Failed to verify username!");
							NetHandlerLoginServer.logger.error("Username \'" + NetHandlerLoginServer.this.loginGameProfile.getName() + "\' tried to join with an invalid session");
						}
					} catch (AuthenticationUnavailableException var3) {
						if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
							NetHandlerLoginServer.logger.warn("Authentication servers are down but will let them in anyway!");
							NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
							NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
						} else {
							NetHandlerLoginServer.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
							NetHandlerLoginServer.logger.error("Couldn\'t verify username because servers are unavailable");
						}
					}
				}
			}).start();
		}
	}

	protected GameProfile getOfflineProfile(GameProfile original) {
		UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
		return new GameProfile(uuid, original.getName());
	}

	static enum LoginState {
		HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;
	}
}
