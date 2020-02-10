package net.minecraft.server.integrated;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import optfine.Reflector;
import optfine.WorldServerOF;

public class IntegratedServer extends MinecraftServer {
	private static final Logger logger = LogManager.getLogger();

	/** The Minecraft instance. */
	private final Minecraft mc;
	private final WorldSettings theWorldSettings;
	private boolean isGamePaused;
	private boolean isPublic;
	private ThreadLanServerPing lanServerPing;
	

	public IntegratedServer(Minecraft mcIn) {
		super(mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
		this.mc = mcIn;
		this.theWorldSettings = null;
	}

	public IntegratedServer(Minecraft mcIn, String folderName, String worldName, WorldSettings settings) {
		super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
		this.setServerOwner(mcIn.getSession().getUsername());
		this.setFolderName(folderName);
		this.setWorldName(worldName);
		this.setDemo(mcIn.isDemo());
		this.canCreateBonusChest(settings.isBonusChestEnabled());
		this.setBuildLimit(256);
		this.setConfigManager(new IntegratedPlayerList(this));
		this.mc = mcIn;
		this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : settings;
		Reflector.callVoid(Reflector.ModLoader_registerServer, new Object[] { this });
	}

	protected ServerCommandManager createNewCommandManager() {
		return new IntegratedServerCommandManager();
	}

	protected void loadAllWorlds(String p_71247_1_, String p_71247_2_, long seed, WorldType type, String p_71247_6_) {
		this.convertMapIfNeeded(p_71247_1_);
		ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(p_71247_1_, true);
		WorldInfo worldinfo = isavehandler.loadWorldInfo();

		if (Reflector.DimensionManager.exists()) {
			WorldServer worldserver = this.isDemo() ? (WorldServer) ((WorldServer) (new DemoWorldServer(this, isavehandler, worldinfo, 0, this.theProfiler)).init()) : (WorldServer) (new WorldServerOF(this, isavehandler, worldinfo, 0, this.theProfiler)).init();
			worldserver.initialize(this.theWorldSettings);
			Integer[] ainteger = (Integer[]) ((Integer[]) Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs, new Object[0]));
			Integer[] ainteger1 = ainteger;
			int i = ainteger.length;

			for (int j = 0; j < i; ++j) {
				int k = ainteger1[j].intValue();
				WorldServer worldserver1 = k == 0 ? worldserver : (WorldServer) ((WorldServer) (new WorldServerMulti(this, isavehandler, k, worldserver, this.theProfiler)).init());
				worldserver1.addWorldAccess(new WorldManager(this, worldserver1));

				if (!this.isSinglePlayer()) {
					worldserver1.getWorldInfo().setGameType(this.getGameType());
				}

				if (Reflector.EventBus.exists()) {
					Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, new Object[] { worldserver1 });
				}
			}

			this.getConfigurationManager().setPlayerManager(new WorldServer[] { worldserver });

			if (worldserver.getWorldInfo().getDifficulty() == null) {
				this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
			}
		} else {
			this.worldServers = new WorldServer[3];
			this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
			this.setResourcePackFromWorld(this.getFolderName(), isavehandler);

			if (worldinfo == null) {
				worldinfo = new WorldInfo(this.theWorldSettings, p_71247_2_);
			} else {
				worldinfo.setWorldName(p_71247_2_);
			}

			for (int l = 0; l < this.worldServers.length; ++l) {
				byte b0 = 0;

				if (l == 1) {
					b0 = -1;
				}

				if (l == 2) {
					b0 = 1;
				}

				if (l == 0) {
					if (this.isDemo()) {
						this.worldServers[l] = (WorldServer) (new DemoWorldServer(this, isavehandler, worldinfo, b0, this.theProfiler)).init();
					} else {
						this.worldServers[l] = (WorldServer) (new WorldServerOF(this, isavehandler, worldinfo, b0, this.theProfiler)).init();
					}

					this.worldServers[l].initialize(this.theWorldSettings);
				} else {
					this.worldServers[l] = (WorldServer) (new WorldServerMulti(this, isavehandler, b0, this.worldServers[0], this.theProfiler)).init();
				}

				this.worldServers[l].addWorldAccess(new WorldManager(this, this.worldServers[l]));
			}

			this.getConfigurationManager().setPlayerManager(this.worldServers);

			if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
				this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
			}
		}

		this.initialWorldChunkLoad();
	}

	/**
	 * Initialises the server and starts it.
	 */
	protected boolean startServer() throws IOException {
		logger.info("Starting integrated minecraft server version 1.8.8");
		this.setOnlineMode(true);
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
		logger.info("Generating keypair");
		this.setKeyPair(CryptManager.generateKeyPair());

		if (Reflector.FMLCommonHandler_handleServerAboutToStart.exists()) {
			Object object = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);

			if (!Reflector.callBoolean(object, Reflector.FMLCommonHandler_handleServerAboutToStart, new Object[] { this })) {
				return false;
			}
		}

		this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.getWorldName());
		this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());

		if (Reflector.FMLCommonHandler_handleServerStarting.exists()) {
			Object object1 = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);

			if (Reflector.FMLCommonHandler_handleServerStarting.getReturnType() == Boolean.TYPE) {
				return Reflector.callBoolean(object1, Reflector.FMLCommonHandler_handleServerStarting, new Object[] { this });
			}

			Reflector.callVoid(object1, Reflector.FMLCommonHandler_handleServerStarting, new Object[] { this });
		}

		return true;
	}

	/**
	 * Main function called by run() every loop.
	 */
	public void tick() {
		boolean flag = this.isGamePaused;
		this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();

		if (!flag && this.isGamePaused) {
			logger.info("Saving and pausing game...");
			this.getConfigurationManager().saveAllPlayerData();
			this.saveAllWorlds(false);
		}

		if (this.isGamePaused) {
			Queue var3 = this.futureTaskQueue;

			synchronized (this.futureTaskQueue) {
				while (!this.futureTaskQueue.isEmpty()) {
					Util.func_181617_a((FutureTask) this.futureTaskQueue.poll(), logger);
				}
			}
		} else {
			super.tick();

			if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
				logger.info("Changing view distance to {}, from {}", new Object[] { Integer.valueOf(this.mc.gameSettings.renderDistanceChunks), Integer.valueOf(this.getConfigurationManager().getViewDistance()) });
				this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
			}

			if (this.mc.theWorld != null) {
				WorldInfo worldinfo = this.worldServers[0].getWorldInfo();
				WorldInfo worldinfo1 = this.mc.theWorld.getWorldInfo();

				if (!worldinfo.isDifficultyLocked() && worldinfo1.getDifficulty() != worldinfo.getDifficulty()) {
					logger.info("Changing difficulty to {}, from {}", new Object[] { worldinfo1.getDifficulty(), worldinfo.getDifficulty() });
					this.setDifficultyForAllWorlds(worldinfo1.getDifficulty());
				} else if (worldinfo1.isDifficultyLocked() && !worldinfo.isDifficultyLocked()) {
					logger.info("Locking difficulty to {}", new Object[] { worldinfo1.getDifficulty() });

					for (WorldServer worldserver : this.worldServers) {
						if (worldserver != null) {
							worldserver.getWorldInfo().setDifficultyLocked(true);
						}
					}
				}
			}
		}
	}

	public boolean canStructuresSpawn() {
		return false;
	}

	public WorldSettings.GameType getGameType() {
		return this.theWorldSettings.getGameType();
	}

	/**
	 * Get the server's difficulty
	 */
	public EnumDifficulty getDifficulty() {
		return this.mc.theWorld == null ? this.mc.gameSettings.difficulty : this.mc.theWorld.getWorldInfo().getDifficulty();
	}

	/**
	 * Defaults to false.
	 */
	public boolean isHardcore() {
		return this.theWorldSettings.getHardcoreEnabled();
	}

	public boolean func_181034_q() {
		return true;
	}

	public boolean func_183002_r() {
		return true;
	}

	public File getDataDirectory() {
		return this.mc.mcDataDir;
	}

	public boolean func_181035_ah() {
		return false;
	}

	public boolean isDedicatedServer() {
		return false;
	}

	/**
	 * Called on exit from the main run() loop.
	 */
	protected void finalTick(CrashReport report) {
		this.mc.crashed(report);
	}

	/**
	 * Adds the server info, including from theWorldServer, to the crash report.
	 */
	public CrashReport addServerInfoToCrashReport(CrashReport report) {
		report = super.addServerInfoToCrashReport(report);
		report.getCategory().addCrashSectionCallable("Type", new Callable() {
			

			public String call() throws Exception {
				return "Integrated Server (map_client.txt)";
			}
		});
		report.getCategory().addCrashSectionCallable("Is Modded", new Callable() {
			

			public String call() throws Exception {
				String s = ClientBrandRetriever.getClientModName();

				if (!s.equals("vanilla")) {
					return "Definitely; Client brand changed to \'" + s + "\'";
				} else {
					s = IntegratedServer.this.getServerModName();
					return !s.equals("vanilla") ? "Definitely; Server brand changed to \'" + s + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.");
				}
			}
		});
		return report;
	}

	public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
		super.setDifficultyForAllWorlds(difficulty);

		if (this.mc.theWorld != null) {
			this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
		}
	}

	public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
		super.addServerStatsToSnooper(playerSnooper);
		playerSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
	}

	/**
	 * Returns whether snooping is enabled or not.
	 */
	public boolean isSnooperEnabled() {
		return Minecraft.getMinecraft().isSnooperEnabled();
	}

	/**
	 * On dedicated does nothing. On integrated, sets commandsAllowedForAll,
	 * gameType and allows external connections.
	 */
	public String shareToLAN(WorldSettings.GameType type, boolean allowCheats) {
		try {
			int i = -1;

			try {
				i = HttpUtil.getSuitableLanPort();
			} catch (IOException var5) {
				;
			}

			if (i <= 0) {
				i = 25564;
			}

			this.getNetworkSystem().addLanEndpoint((InetAddress) null, i);
			logger.info("Started on " + i);
			this.isPublic = true;
			this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), i + "");
			this.lanServerPing.start();
			this.getConfigurationManager().setGameType(type);
			this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
			return i + "";
		} catch (IOException var6) {
			return null;
		}
	}

	/**
	 * Saves all necessary data as preparation for stopping the server.
	 */
	public void stopServer() {
		super.stopServer();

		if (this.lanServerPing != null) {
			this.lanServerPing.interrupt();
			this.lanServerPing = null;
		}
	}

	/**
	 * Sets the serverRunning variable to false, in order to get the server to shut
	 * down.
	 */
	public void initiateShutdown() {
		Futures.getUnchecked(this.addScheduledTask(new Runnable() {
			

			public void run() {
				for (EntityPlayerMP entityplayermp : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().func_181057_v())) {
					IntegratedServer.this.getConfigurationManager().playerLoggedOut(entityplayermp);
				}
			}
		}));
		super.initiateShutdown();

		if (this.lanServerPing != null) {
			this.lanServerPing.interrupt();
			this.lanServerPing = null;
		}
	}

	public void setStaticInstance() {
		this.setInstance();
	}

	/**
	 * Returns true if this integrated server is open to LAN
	 */
	public boolean getPublic() {
		return this.isPublic;
	}

	/**
	 * Sets the game type for all worlds.
	 */
	public void setGameType(WorldSettings.GameType gameMode) {
		this.getConfigurationManager().setGameType(gameMode);
	}

	/**
	 * Return whether command blocks are enabled.
	 */
	public boolean isCommandBlockEnabled() {
		return true;
	}

	public int getOpPermissionLevel() {
		return 4;
	}
}
