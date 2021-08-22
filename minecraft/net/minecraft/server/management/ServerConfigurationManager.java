package net.minecraft.server.management;

import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;

public abstract class ServerConfigurationManager {
	public static final File FILE_PLAYERBANS = new File("banned-players.json");
	public static final File FILE_IPBANS = new File("banned-ips.json");
	public static final File FILE_OPS = new File("ops.json");
	public static final File FILE_WHITELIST = new File("whitelist.json");
	private static final Logger logger = LogManager.getLogger();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

	/** Reference to the MinecraftServer object. */
	private final MinecraftServer mcServer;
	private final List<EntityPlayerMP> playerEntityList = Lists.<EntityPlayerMP>newArrayList();
	private final Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.<UUID, EntityPlayerMP>newHashMap();
	private final UserListBans bannedPlayers;
	private final BanList bannedIPs;

	/** A set containing the OPs. */
	private final UserListOps ops;

	/** The Set of all whitelisted players. */
	private final UserListWhitelist whiteListedPlayers;
	private final Map<UUID, StatisticsFile> playerStatFiles;

	/** Reference to the PlayerNBTManager object. */
	private IPlayerFileData playerNBTManagerObj;

	/**
	 * Server setting to only allow OPs and whitelisted players to join the server.
	 */
	private boolean whiteListEnforced;

	/** The maximum number of players that can be connected at a time. */
	protected int maxPlayers;
	private int viewDistance;
	private WorldSettings.GameType gameType;

	/** True if all players are allowed to use commands (cheats). */
	private boolean commandsAllowedForAll;

	/**
	 * index into playerEntities of player to ping, updated every tick; currently
	 * hardcoded to max at 200 players
	 */
	private int playerPingIndex;

	public ServerConfigurationManager(MinecraftServer server) {
		this.bannedPlayers = new UserListBans(FILE_PLAYERBANS);
		this.bannedIPs = new BanList(FILE_IPBANS);
		this.ops = new UserListOps(FILE_OPS);
		this.whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
		this.playerStatFiles = Maps.<UUID, StatisticsFile>newHashMap();
		this.mcServer = server;
		this.bannedPlayers.setLanServer(false);
		this.bannedIPs.setLanServer(false);
		this.maxPlayers = 8;
	}

	public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn) {
		GameProfile gameprofile = playerIn.getGameProfile();
		PlayerProfileCache playerprofilecache = this.mcServer.getPlayerProfileCache();
		GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
		String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
		playerprofilecache.addEntry(gameprofile);
		NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(playerIn);
		playerIn.setWorld(this.mcServer.worldServerForDimension(playerIn.dimension));
		playerIn.theItemInWorldManager.setWorld((WorldServer) playerIn.worldObj);
		String s1 = "local";

		if (netManager.getRemoteAddress() != null) {
			s1 = netManager.getRemoteAddress().toString();
		}

		logger.info(playerIn.getName() + "[" + s1 + "] logged in with entity id " + playerIn.getEntityId() + " at (" + playerIn.posX + ", " + playerIn.posY + ", " + playerIn.posZ + ")");
		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);
		WorldInfo worldinfo = worldserver.getWorldInfo();
		BlockPos blockpos = worldserver.getSpawnPoint();
		this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP) null, worldserver);
		NetHandlerPlayServer nethandlerplayserver = new NetHandlerPlayServer(this.mcServer, netManager, playerIn);
		nethandlerplayserver.sendPacket(new S01PacketJoinGame(playerIn.getEntityId(), playerIn.theItemInWorldManager.getGameType(), worldinfo.isHardcoreModeEnabled(), worldserver.provider.getDimensionId(), worldserver.getDifficulty(), this.getMaxPlayers(), worldinfo.getTerrainType(), worldserver.getGameRules().getBoolean("reducedDebugInfo")));
		nethandlerplayserver.sendPacket(new S3FPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(this.getServerInstance().getServerModName())));
		nethandlerplayserver.sendPacket(new S41PacketServerDifficulty(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
		nethandlerplayserver.sendPacket(new S05PacketSpawnPosition(blockpos));
		nethandlerplayserver.sendPacket(new S39PacketPlayerAbilities(playerIn.capabilities));
		nethandlerplayserver.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
		playerIn.getStatFile().func_150877_d();
		playerIn.getStatFile().sendAchievements(playerIn);
		this.sendScoreboard((ServerScoreboard) worldserver.getScoreboard(), playerIn);
		this.mcServer.refreshStatusNextTick();
		ChatComponentTranslation chatcomponenttranslation;

		if (!playerIn.getName().equalsIgnoreCase(s)) {
			chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", new Object[] { playerIn.getDisplayName(), s });
		} else {
			chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined", new Object[] { playerIn.getDisplayName() });
		}

		chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
		this.sendChatMsg(chatcomponenttranslation);
		this.playerLoggedIn(playerIn);
		nethandlerplayserver.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
		this.updateTimeAndWeatherForPlayer(playerIn, worldserver);

		if (this.mcServer.getResourcePackUrl().length() > 0) {
			playerIn.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
		}

		for (PotionEffect potioneffect : playerIn.getActivePotionEffects()) {
			nethandlerplayserver.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potioneffect));
		}

		playerIn.addSelfToInternalCraftingInventory();

		if (nbttagcompound != null && nbttagcompound.hasKey("Riding", 10)) {
			Entity entity = EntityList.createEntityFromNBT(nbttagcompound.getCompoundTag("Riding"), worldserver);

			if (entity != null) {
				entity.forceSpawn = true;
				worldserver.spawnEntityInWorld(entity);
				playerIn.mountEntity(entity);
				entity.forceSpawn = false;
			}
		}
	}

	protected void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn) {
		Set<ScoreObjective> set = Sets.<ScoreObjective>newHashSet();

		for (ScorePlayerTeam scoreplayerteam : scoreboardIn.getTeams()) {
			playerIn.playerNetServerHandler.sendPacket(new S3EPacketTeams(scoreplayerteam, 0));
		}

		for (int i = 0; i < 19; ++i) {
			ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);

			if (scoreobjective != null && !set.contains(scoreobjective)) {
				for (Packet packet : scoreboardIn.func_96550_d(scoreobjective)) {
					playerIn.playerNetServerHandler.sendPacket(packet);
				}

				set.add(scoreobjective);
			}
		}
	}

	/**
	 * Sets the NBT manager to the one for the WorldServer given.
	 */
	public void setPlayerManager(WorldServer[] worldServers) {
		this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
		worldServers[0].getWorldBorder().addListener(new IBorderListener() {
			public void onSizeChanged(WorldBorder border, double newSize) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_SIZE));
			}

			public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.LERP_SIZE));
			}

			public void onCenterChanged(WorldBorder border, double x, double z) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_CENTER));
			}

			public void onWarningTimeChanged(WorldBorder border, int newTime) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_TIME));
			}

			public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
			}

			public void onDamageAmountChanged(WorldBorder border, double newAmount) {
			}

			public void onDamageBufferChanged(WorldBorder border, double newSize) {
			}
		});
	}

	public void preparePlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
		WorldServer worldserver = playerIn.getServerForPlayer();

		if (worldIn != null) {
			worldIn.getPlayerManager().removePlayer(playerIn);
		}

		worldserver.getPlayerManager().addPlayer(playerIn);
		worldserver.theChunkProviderServer.loadChunk((int) playerIn.posX >> 4, (int) playerIn.posZ >> 4);
	}

	public int getEntityViewDistance() {
		return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
	}

	/**
	 * called during player login. reads the player information from disk.
	 */
	public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn) {
		NBTTagCompound nbttagcompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
		NBTTagCompound nbttagcompound1;

		if (playerIn.getName().equals(this.mcServer.getServerOwner()) && nbttagcompound != null) {
			playerIn.readFromNBT(nbttagcompound);
			nbttagcompound1 = nbttagcompound;
			logger.debug("loading single player");
		} else {
			nbttagcompound1 = this.playerNBTManagerObj.readPlayerData(playerIn);
		}

		return nbttagcompound1;
	}

	/**
	 * also stores the NBTTags if this is an intergratedPlayerList
	 */
	protected void writePlayerData(EntityPlayerMP playerIn) {
		this.playerNBTManagerObj.writePlayerData(playerIn);
		StatisticsFile statisticsfile = (StatisticsFile) this.playerStatFiles.get(playerIn.getUniqueID());

		if (statisticsfile != null) {
			statisticsfile.saveStatFile();
		}
	}

	/**
	 * Called when a player successfully logs in. Reads player data from disk and
	 * inserts the player into the world.
	 */
	public void playerLoggedIn(EntityPlayerMP playerIn) {
		this.playerEntityList.add(playerIn);
		this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
		this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[] { playerIn }));
		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);
		worldserver.spawnEntityInWorld(playerIn);
		this.preparePlayer(playerIn, (WorldServer) null);

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);
			playerIn.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[] { entityplayermp }));
		}
	}

	/**
	 * using player's dimension, update their movement when in a vehicle (e.g. cart,
	 * boat)
	 */
	public void serverUpdateMountedMovingPlayer(EntityPlayerMP playerIn) {
		playerIn.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(playerIn);
	}

	/**
	 * Called when a player disconnects from the game. Writes player data to disk
	 * and removes them from the world.
	 */
	public void playerLoggedOut(EntityPlayerMP playerIn) {
		playerIn.triggerAchievement(StatList.leaveGameStat);
		this.writePlayerData(playerIn);
		WorldServer worldserver = playerIn.getServerForPlayer();

		if (playerIn.ridingEntity != null) {
			worldserver.removePlayerEntityDangerously(playerIn.ridingEntity);
			logger.debug("removing player mount");
		}

		worldserver.removeEntity(playerIn);
		worldserver.getPlayerManager().removePlayer(playerIn);
		this.playerEntityList.remove(playerIn);
		UUID uuid = playerIn.getUniqueID();
		EntityPlayerMP entityplayermp = (EntityPlayerMP) this.uuidToPlayerMap.get(uuid);

		if (entityplayermp == playerIn) {
			this.uuidToPlayerMap.remove(uuid);
			this.playerStatFiles.remove(uuid);
		}

		this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.REMOVE_PLAYER, new EntityPlayerMP[] { playerIn }));
	}

	/**
	 * checks ban-lists, then white-lists, then space for the server. Returns null
	 * on success, or an error message
	 */
	public String allowUserToConnect(SocketAddress address, GameProfile profile) {
		if (this.bannedPlayers.isBanned(profile)) {
			UserListBansEntry userlistbansentry = (UserListBansEntry) this.bannedPlayers.getEntry(profile);
			String s1 = "You are banned from this server!\nReason: " + userlistbansentry.getBanReason();

			if (userlistbansentry.getBanEndDate() != null) {
				s1 = s1 + "\nYour ban will be removed on " + dateFormat.format(userlistbansentry.getBanEndDate());
			}

			return s1;
		} else if (!this.canJoin(profile)) {
			return "You are not white-listed on this server!";
		} else if (this.bannedIPs.isBanned(address)) {
			IPBanEntry ipbanentry = this.bannedIPs.getBanEntry(address);
			String s = "Your IP address is banned from this server!\nReason: " + ipbanentry.getBanReason();

			if (ipbanentry.getBanEndDate() != null) {
				s = s + "\nYour ban will be removed on " + dateFormat.format(ipbanentry.getBanEndDate());
			}

			return s;
		} else {
			return this.playerEntityList.size() >= this.maxPlayers && !this.func_183023_f(profile) ? "The server is full!" : null;
		}
	}

	/**
	 * also checks for multiple logins across servers
	 */
	public EntityPlayerMP createPlayerForUser(GameProfile profile) {
		UUID uuid = EntityPlayer.getUUID(profile);
		List<EntityPlayerMP> list = Lists.<EntityPlayerMP>newArrayList();

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);

			if (entityplayermp.getUniqueID().equals(uuid)) {
				list.add(entityplayermp);
			}
		}

		EntityPlayerMP entityplayermp2 = (EntityPlayerMP) this.uuidToPlayerMap.get(profile.getId());

		if (entityplayermp2 != null && !list.contains(entityplayermp2)) {
			list.add(entityplayermp2);
		}

		for (EntityPlayerMP entityplayermp1 : list) {
			entityplayermp1.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
		}

		ItemInWorldManager iteminworldmanager;

		if (this.mcServer.isDemo()) {
			iteminworldmanager = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
		} else {
			iteminworldmanager = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
		}

		return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, iteminworldmanager);
	}

	/**
	 * Called on respawn
	 */
	public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd) {
		playerIn.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(playerIn);
		playerIn.getServerForPlayer().getEntityTracker().untrackEntity(playerIn);
		playerIn.getServerForPlayer().getPlayerManager().removePlayer(playerIn);
		this.playerEntityList.remove(playerIn);
		this.mcServer.worldServerForDimension(playerIn.dimension).removePlayerEntityDangerously(playerIn);
		BlockPos blockpos = playerIn.getBedLocation();
		boolean flag = playerIn.isSpawnForced();
		playerIn.dimension = dimension;
		ItemInWorldManager iteminworldmanager;

		if (this.mcServer.isDemo()) {
			iteminworldmanager = new DemoWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
		} else {
			iteminworldmanager = new ItemInWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
		}

		EntityPlayerMP entityplayermp = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(playerIn.dimension), playerIn.getGameProfile(), iteminworldmanager);
		entityplayermp.playerNetServerHandler = playerIn.playerNetServerHandler;
		entityplayermp.clonePlayer(playerIn, conqueredEnd);
		entityplayermp.setEntityId(playerIn.getEntityId());
		entityplayermp.func_174817_o(playerIn);
		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);
		this.setPlayerGameTypeBasedOnOther(entityplayermp, playerIn, worldserver);

		if (blockpos != null) {
			BlockPos blockpos1 = EntityPlayer.getBedSpawnLocation(this.mcServer.worldServerForDimension(playerIn.dimension), blockpos, flag);

			if (blockpos1 != null) {
				entityplayermp.setLocationAndAngles((double) ((float) blockpos1.getX() + 0.5F), (double) ((float) blockpos1.getY() + 0.1F), (double) ((float) blockpos1.getZ() + 0.5F), 0.0F, 0.0F);
				entityplayermp.setSpawnPoint(blockpos, flag);
			} else {
				entityplayermp.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
			}
		}

		worldserver.theChunkProviderServer.loadChunk((int) entityplayermp.posX >> 4, (int) entityplayermp.posZ >> 4);

		while (!worldserver.getCollidingBoundingBoxes(entityplayermp, entityplayermp.getEntityBoundingBox()).isEmpty() && entityplayermp.posY < 256.0D) {
			entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0D, entityplayermp.posZ);
		}

		entityplayermp.playerNetServerHandler.sendPacket(new S07PacketRespawn(entityplayermp.dimension, entityplayermp.worldObj.getDifficulty(), entityplayermp.worldObj.getWorldInfo().getTerrainType(), entityplayermp.theItemInWorldManager.getGameType()));
		BlockPos blockpos2 = worldserver.getSpawnPoint();
		entityplayermp.playerNetServerHandler.setPlayerLocation(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
		entityplayermp.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(blockpos2));
		entityplayermp.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityplayermp.experience, entityplayermp.experienceTotal, entityplayermp.experienceLevel));
		this.updateTimeAndWeatherForPlayer(entityplayermp, worldserver);
		worldserver.getPlayerManager().addPlayer(entityplayermp);
		worldserver.spawnEntityInWorld(entityplayermp);
		this.playerEntityList.add(entityplayermp);
		this.uuidToPlayerMap.put(entityplayermp.getUniqueID(), entityplayermp);
		entityplayermp.addSelfToInternalCraftingInventory();
		entityplayermp.setHealth(entityplayermp.getHealth());
		return entityplayermp;
	}

	/**
	 * moves provided player from overworld to nether or vice versa
	 */
	public void transferPlayerToDimension(EntityPlayerMP playerIn, int dimension) {
		int i = playerIn.dimension;
		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);
		playerIn.dimension = dimension;
		WorldServer worldserver1 = this.mcServer.worldServerForDimension(playerIn.dimension);
		playerIn.playerNetServerHandler.sendPacket(new S07PacketRespawn(playerIn.dimension, playerIn.worldObj.getDifficulty(), playerIn.worldObj.getWorldInfo().getTerrainType(), playerIn.theItemInWorldManager.getGameType()));
		worldserver.removePlayerEntityDangerously(playerIn);
		playerIn.isDead = false;
		this.transferEntityToWorld(playerIn, i, worldserver, worldserver1);
		this.preparePlayer(playerIn, worldserver);
		playerIn.playerNetServerHandler.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
		playerIn.theItemInWorldManager.setWorld(worldserver1);
		this.updateTimeAndWeatherForPlayer(playerIn, worldserver1);
		this.syncPlayerInventory(playerIn);

		for (PotionEffect potioneffect : playerIn.getActivePotionEffects()) {
			playerIn.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potioneffect));
		}
	}

	/**
	 * Transfers an entity from a world to another world.
	 */
	public void transferEntityToWorld(Entity entityIn, int p_82448_2_, WorldServer p_82448_3_, WorldServer p_82448_4_) {
		double d0 = entityIn.posX;
		double d1 = entityIn.posZ;
		double d2 = 8.0D;
		float f = entityIn.rotationYaw;
		p_82448_3_.theProfiler.startSection("moving");

		if (entityIn.dimension == -1) {
			d0 = MathHelper.clamp_double(d0 / d2, p_82448_4_.getWorldBorder().minX() + 16.0D, p_82448_4_.getWorldBorder().maxX() - 16.0D);
			d1 = MathHelper.clamp_double(d1 / d2, p_82448_4_.getWorldBorder().minZ() + 16.0D, p_82448_4_.getWorldBorder().maxZ() - 16.0D);
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);

			if (entityIn.isEntityAlive()) {
				p_82448_3_.updateEntityWithOptionalForce(entityIn, false);
			}
		} else if (entityIn.dimension == 0) {
			d0 = MathHelper.clamp_double(d0 * d2, p_82448_4_.getWorldBorder().minX() + 16.0D, p_82448_4_.getWorldBorder().maxX() - 16.0D);
			d1 = MathHelper.clamp_double(d1 * d2, p_82448_4_.getWorldBorder().minZ() + 16.0D, p_82448_4_.getWorldBorder().maxZ() - 16.0D);
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);

			if (entityIn.isEntityAlive()) {
				p_82448_3_.updateEntityWithOptionalForce(entityIn, false);
			}
		} else {
			BlockPos blockpos;

			if (p_82448_2_ == 1) {
				blockpos = p_82448_4_.getSpawnPoint();
			} else {
				blockpos = p_82448_4_.getSpawnCoordinate();
			}

			d0 = (double) blockpos.getX();
			entityIn.posY = (double) blockpos.getY();
			d1 = (double) blockpos.getZ();
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, 90.0F, 0.0F);

			if (entityIn.isEntityAlive()) {
				p_82448_3_.updateEntityWithOptionalForce(entityIn, false);
			}
		}

		p_82448_3_.theProfiler.endSection();

		if (p_82448_2_ != 1) {
			p_82448_3_.theProfiler.startSection("placing");
			d0 = (double) MathHelper.clamp_int((int) d0, -29999872, 29999872);
			d1 = (double) MathHelper.clamp_int((int) d1, -29999872, 29999872);

			if (entityIn.isEntityAlive()) {
				entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
				p_82448_4_.getDefaultTeleporter().placeInPortal(entityIn, f);
				p_82448_4_.spawnEntityInWorld(entityIn);
				p_82448_4_.updateEntityWithOptionalForce(entityIn, false);
			}

			p_82448_3_.theProfiler.endSection();
		}

		entityIn.setWorld(p_82448_4_);
	}

	/**
	 * self explanitory
	 */
	public void onTick() {
		if (++this.playerPingIndex > 600) {
			this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
			this.playerPingIndex = 0;
		}
	}

	public void sendPacketToAllPlayers(Packet packetIn) {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			((EntityPlayerMP) this.playerEntityList.get(i)).playerNetServerHandler.sendPacket(packetIn);
		}
	}

	public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension) {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);

			if (entityplayermp.dimension == dimension) {
				entityplayermp.playerNetServerHandler.sendPacket(packetIn);
			}
		}
	}

	public void sendMessageToAllTeamMembers(EntityPlayer player, IChatComponent message) {
		Team team = player.getTeam();

		if (team != null) {
			for (String s : team.getMembershipCollection()) {
				EntityPlayerMP entityplayermp = this.getPlayerByUsername(s);

				if (entityplayermp != null && entityplayermp != player) {
					entityplayermp.addChatMessage(message);
				}
			}
		}
	}

	public void sendMessageToTeamOrEvryPlayer(EntityPlayer player, IChatComponent message) {
		Team team = player.getTeam();

		if (team == null) {
			this.sendChatMsg(message);
		} else {
			for (int i = 0; i < this.playerEntityList.size(); ++i) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);

				if (entityplayermp.getTeam() != team) {
					entityplayermp.addChatMessage(message);
				}
			}
		}
	}

	public String func_181058_b(boolean p_181058_1_) {
		String s = "";
		List<EntityPlayerMP> list = Lists.newArrayList(this.playerEntityList);

		for (int i = 0; i < ((List) list).size(); ++i) {
			if (i > 0) {
				s = s + ", ";
			}

			s = s + ((EntityPlayerMP) list.get(i)).getName();

			if (p_181058_1_) {
				s = s + " (" + ((EntityPlayerMP) list.get(i)).getUniqueID().toString() + ")";
			}
		}

		return s;
	}

	/**
	 * Returns an array of the usernames of all the connected players.
	 */
	public String[] getAllUsernames() {
		String[] astring = new String[this.playerEntityList.size()];

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			astring[i] = ((EntityPlayerMP) this.playerEntityList.get(i)).getName();
		}

		return astring;
	}

	public GameProfile[] getAllProfiles() {
		GameProfile[] agameprofile = new GameProfile[this.playerEntityList.size()];

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			agameprofile[i] = ((EntityPlayerMP) this.playerEntityList.get(i)).getGameProfile();
		}

		return agameprofile;
	}

	public UserListBans getBannedPlayers() {
		return this.bannedPlayers;
	}

	public BanList getBannedIPs() {
		return this.bannedIPs;
	}

	public void addOp(GameProfile profile) {
		this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel(), this.ops.func_183026_b(profile)));
	}

	public void removeOp(GameProfile profile) {
		this.ops.removeEntry(profile);
	}

	public boolean canJoin(GameProfile profile) {
		return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
	}

	public boolean canSendCommands(GameProfile profile) {
		return this.ops.hasEntry(profile) || this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName()) || this.commandsAllowedForAll;
	}

	public EntityPlayerMP getPlayerByUsername(String username) {
		for (EntityPlayerMP entityplayermp : this.playerEntityList) {
			if (entityplayermp.getName().equalsIgnoreCase(username)) {
				return entityplayermp;
			}
		}

		return null;
	}

	/**
	 * params: x,y,z,r,dimension. The packet is sent to all players within r radius
	 * of x,y,z (r^2>x^2+y^2+z^2)
	 */
	public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn) {
		this.sendToAllNearExcept((EntityPlayer) null, x, y, z, radius, dimension, packetIn);
	}

	/**
	 * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer,
	 * but all other players within the search radius
	 */
	public void sendToAllNearExcept(EntityPlayer p_148543_1_, double x, double y, double z, double radius, int dimension, Packet p_148543_11_) {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) this.playerEntityList.get(i);

			if (entityplayermp != p_148543_1_ && entityplayermp.dimension == dimension) {
				double d0 = x - entityplayermp.posX;
				double d1 = y - entityplayermp.posY;
				double d2 = z - entityplayermp.posZ;

				if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
					entityplayermp.playerNetServerHandler.sendPacket(p_148543_11_);
				}
			}
		}
	}

	/**
	 * Saves all of the players' current states.
	 */
	public void saveAllPlayerData() {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			this.writePlayerData((EntityPlayerMP) this.playerEntityList.get(i));
		}
	}

	public void addWhitelistedPlayer(GameProfile profile) {
		this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(profile));
	}

	public void removePlayerFromWhitelist(GameProfile profile) {
		this.whiteListedPlayers.removeEntry(profile);
	}

	public UserListWhitelist getWhitelistedPlayers() {
		return this.whiteListedPlayers;
	}

	public String[] getWhitelistedPlayerNames() {
		return this.whiteListedPlayers.getKeys();
	}

	public UserListOps getOppedPlayers() {
		return this.ops;
	}

	public String[] getOppedPlayerNames() {
		return this.ops.getKeys();
	}

	/**
	 * Either does nothing, or calls readWhiteList.
	 */
	public void loadWhiteList() {
	}

	/**
	 * Updates the time and weather for the given player to those of the given world
	 */
	public void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
		WorldBorder worldborder = this.mcServer.worldServers[0].getWorldBorder();
		playerIn.playerNetServerHandler.sendPacket(new S44PacketWorldBorder(worldborder, S44PacketWorldBorder.Action.INITIALIZE));
		playerIn.playerNetServerHandler.sendPacket(new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));

		if (worldIn.isRaining()) {
			playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
			playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
			playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
		}
	}

	/**
	 * sends the players inventory to himself
	 */
	public void syncPlayerInventory(EntityPlayerMP playerIn) {
		playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
		playerIn.setPlayerHealthUpdated();
		playerIn.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
	}

	/**
	 * Returns the number of players currently on the server.
	 */
	public int getCurrentPlayerCount() {
		return this.playerEntityList.size();
	}

	/**
	 * Returns the maximum number of players allowed on the server.
	 */
	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	/**
	 * Returns an array of usernames for which player.dat exists for.
	 */
	public String[] getAvailablePlayerDat() {
		return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
	}

	public void setWhiteListEnabled(boolean whitelistEnabled) {
		this.whiteListEnforced = whitelistEnabled;
	}

	public List<EntityPlayerMP> getPlayersMatchingAddress(String address) {
		List<EntityPlayerMP> list = Lists.<EntityPlayerMP>newArrayList();

		for (EntityPlayerMP entityplayermp : this.playerEntityList) {
			if (entityplayermp.getPlayerIP().equals(address)) {
				list.add(entityplayermp);
			}
		}

		return list;
	}

	/**
	 * Gets the View Distance.
	 */
	public int getViewDistance() {
		return this.viewDistance;
	}

	public MinecraftServer getServerInstance() {
		return this.mcServer;
	}

	/**
	 * On integrated servers, returns the host's player data to be written to
	 * level.dat.
	 */
	public NBTTagCompound getHostPlayerData() {
		return null;
	}

	public void setGameType(WorldSettings.GameType p_152604_1_) {
		this.gameType = p_152604_1_;
	}

	private void setPlayerGameTypeBasedOnOther(EntityPlayerMP p_72381_1_, EntityPlayerMP p_72381_2_, World worldIn) {
		if (p_72381_2_ != null) {
			p_72381_1_.theItemInWorldManager.setGameType(p_72381_2_.theItemInWorldManager.getGameType());
		} else if (this.gameType != null) {
			p_72381_1_.theItemInWorldManager.setGameType(this.gameType);
		}

		p_72381_1_.theItemInWorldManager.initializeGameType(worldIn.getWorldInfo().getGameType());
	}

	/**
	 * Sets whether all players are allowed to use commands (cheats) on the server.
	 */
	public void setCommandsAllowedForAll(boolean p_72387_1_) {
		this.commandsAllowedForAll = p_72387_1_;
	}

	/**
	 * Kicks everyone with "Server closed" as reason.
	 */
	public void removeAllPlayers() {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			((EntityPlayerMP) this.playerEntityList.get(i)).playerNetServerHandler.kickPlayerFromServer("Server closed");
		}
	}

	public void sendChatMsgImpl(IChatComponent component, boolean isChat) {
		this.mcServer.addChatMessage(component);
		byte b0 = (byte) (isChat ? 1 : 0);
		this.sendPacketToAllPlayers(new S02PacketChat(component, b0));
	}

	/**
	 * Sends the given string to every player as chat message.
	 */
	public void sendChatMsg(IChatComponent component) {
		this.sendChatMsgImpl(component, true);
	}

	public StatisticsFile getPlayerStatsFile(EntityPlayer playerIn) {
		UUID uuid = playerIn.getUniqueID();
		StatisticsFile statisticsfile = uuid == null ? null : (StatisticsFile) this.playerStatFiles.get(uuid);

		if (statisticsfile == null) {
			File file1 = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
			File file2 = new File(file1, uuid.toString() + ".json");

			if (!file2.exists()) {
				File file3 = new File(file1, playerIn.getName() + ".json");

				if (file3.exists() && file3.isFile()) {
					file3.renameTo(file2);
				}
			}

			statisticsfile = new StatisticsFile(this.mcServer, file2);
			statisticsfile.readStatFile();
			this.playerStatFiles.put(uuid, statisticsfile);
		}

		return statisticsfile;
	}

	public void setViewDistance(int distance) {
		this.viewDistance = distance;

		if (this.mcServer.worldServers != null) {
			for (WorldServer worldserver : this.mcServer.worldServers) {
				if (worldserver != null) {
					worldserver.getPlayerManager().setPlayerViewRadius(distance);
				}
			}
		}
	}

	public List<EntityPlayerMP> func_181057_v() {
		return this.playerEntityList;
	}

	/**
	 * Get's the EntityPlayerMP object representing the player with the UUID.
	 */
	public EntityPlayerMP getPlayerByUUID(UUID playerUUID) {
		return (EntityPlayerMP) this.uuidToPlayerMap.get(playerUUID);
	}

	public boolean func_183023_f(GameProfile p_183023_1_) {
		return false;
	}
}
