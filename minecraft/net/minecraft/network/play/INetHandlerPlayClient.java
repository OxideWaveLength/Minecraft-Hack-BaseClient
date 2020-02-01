package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;

public interface INetHandlerPlayClient extends INetHandler
{
    /**
     * Spawns an instance of the objecttype indicated by the packet and sets its position and momentum
     */
    void handleSpawnObject(S0EPacketSpawnObject packetIn);

    /**
     * Spawns an experience orb and sets its value (amount of XP)
     */
    void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn);

    /**
     * Handles globally visible entities. Used in vanilla for lightning bolts
     */
    void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn);

    /**
     * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
     * entities Datawatchers with the entity metadata specified in the packet
     */
    void handleSpawnMob(S0FPacketSpawnMob packetIn);

    /**
     * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
     */
    void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn);

    /**
     * Handles the spawning of a painting object
     */
    void handleSpawnPainting(S10PacketSpawnPainting packetIn);

    /**
     * Handles the creation of a nearby player entity, sets the position and held item
     */
    void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn);

    /**
     * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
     * or receiving a critical hit by normal or magical means
     */
    void handleAnimation(S0BPacketAnimation packetIn);

    /**
     * Updates the players statistics or achievements
     */
    void handleStatistics(S37PacketStatistics packetIn);

    /**
     * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
     */
    void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn);

    /**
     * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
     */
    void handleSignEditorOpen(S36PacketSignEditorOpen packetIn);

    /**
     * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
     * beacons, skulls, flowerpot
     */
    void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn);

    /**
     * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
     * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
     * accessing a (Ender)Chest
     */
    void handleBlockAction(S24PacketBlockAction packetIn);

    /**
     * Updates the block and metadata and generates a blockupdate (and notify the clients)
     */
    void handleBlockChange(S23PacketBlockChange packetIn);

    /**
     * Prints a chatmessage in the chat GUI
     */
    void handleChat(S02PacketChat packetIn);

    /**
     * Displays the available command-completion options the server knows of
     */
    void handleTabComplete(S3APacketTabComplete packetIn);

    /**
     * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
     * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
     * S21PacketChunkData
     */
    void handleMultiBlockChange(S22PacketMultiBlockChange packetIn);

    /**
     * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
     * MapItemRenderer for it
     */
    void handleMaps(S34PacketMaps packetIn);

    /**
     * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
     * and confirms if it is the case.
     */
    void handleConfirmTransaction(S32PacketConfirmTransaction packetIn);

    /**
     * Resets the ItemStack held in hand and closes the window that is opened
     */
    void handleCloseWindow(S2EPacketCloseWindow packetIn);

    /**
     * Handles the placement of a specified ItemStack in a specified container/inventory slot
     */
    void handleWindowItems(S30PacketWindowItems packetIn);

    /**
     * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
     * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
     */
    void handleOpenWindow(S2DPacketOpenWindow packetIn);

    /**
     * Sets the progressbar of the opened window to the specified value
     */
    void handleWindowProperty(S31PacketWindowProperty packetIn);

    /**
     * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
     */
    void handleSetSlot(S2FPacketSetSlot packetIn);

    /**
     * Handles packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
     * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
     * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
     * resourcepack for the client to load.
     */
    void handleCustomPayload(S3FPacketCustomPayload packetIn);

    /**
     * Closes the network channel
     */
    void handleDisconnect(S40PacketDisconnect packetIn);

    /**
     * Retrieves the player identified by the packet, puts him to sleep if possible (and flags whether all players are
     * asleep)
     */
    void handleUseBed(S0APacketUseBed packetIn);

    /**
     * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
     * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
     * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
     * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
     */
    void handleEntityStatus(S19PacketEntityStatus packetIn);

    void handleEntityAttach(S1BPacketEntityAttach packetIn);

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
     */
    void handleExplosion(S27PacketExplosion packetIn);

    void handleChangeGameState(S2BPacketChangeGameState packetIn);

    void handleKeepAlive(S00PacketKeepAlive packetIn);

    /**
     * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
     */
    void handleChunkData(S21PacketChunkData packetIn);

    void handleMapChunkBulk(S26PacketMapChunkBulk packetIn);

    void handleEffect(S28PacketEffect packetIn);

    /**
     * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
     * WorldClient and sets the player initial dimension
     */
    void handleJoinGame(S01PacketJoinGame packetIn);

    /**
     * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
     * subclassing of the packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
     * rotation or both).
     */
    void handleEntityMovement(S14PacketEntity packetIn);

    /**
     * Handles changes in player positioning and rotation such as when travelling to a new dimension, (re)spawning,
     * mounting horses etc. Seems to immediately reply to the server with the clients post-processing perspective on the
     * player positioning
     */
    void handlePlayerPosLook(S08PacketPlayerPosLook packetIn);

    /**
     * Spawns a specified number of particles at the specified location with a randomized displacement according to
     * specified bounds
     */
    void handleParticles(S2APacketParticles packetIn);

    void handlePlayerAbilities(S39PacketPlayerAbilities packetIn);

    void handlePlayerListItem(S38PacketPlayerListItem packetIn);

    /**
     * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
     * longer registered as required to monitor them. The latter  happens when distance between the player and item
     * increases beyond a certain treshold (typically the viewing distance)
     */
    void handleDestroyEntities(S13PacketDestroyEntities packetIn);

    void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn);

    void handleRespawn(S07PacketRespawn packetIn);

    /**
     * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
     * rotation of the entity itself
     */
    void handleEntityHeadLook(S19PacketEntityHeadLook packetIn);

    /**
     * Updates which hotbar slot of the player is currently selected
     */
    void handleHeldItemChange(S09PacketHeldItemChange packetIn);

    /**
     * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
     * name)
     */
    void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn);

    /**
     * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
     * changed -> Registers any changes locally
     */
    void handleEntityMetadata(S1CPacketEntityMetadata packetIn);

    /**
     * Sets the velocity of the specified entity to the specified value
     */
    void handleEntityVelocity(S12PacketEntityVelocity packetIn);

    void handleEntityEquipment(S04PacketEntityEquipment packetIn);

    void handleSetExperience(S1FPacketSetExperience packetIn);

    void handleUpdateHealth(S06PacketUpdateHealth packetIn);

    /**
     * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
     * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
     */
    void handleTeams(S3EPacketTeams packetIn);

    /**
     * Either updates the score with a specified value or removes the score for an objective
     */
    void handleUpdateScore(S3CPacketUpdateScore packetIn);

    void handleSpawnPosition(S05PacketSpawnPosition packetIn);

    void handleTimeUpdate(S03PacketTimeUpdate packetIn);

    /**
     * Updates a specified sign with the specified text lines
     */
    void handleUpdateSign(S33PacketUpdateSign packetIn);

    void handleSoundEffect(S29PacketSoundEffect packetIn);

    void handleCollectItem(S0DPacketCollectItem packetIn);

    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    void handleEntityTeleport(S18PacketEntityTeleport packetIn);

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    void handleEntityProperties(S20PacketEntityProperties packetIn);

    void handleEntityEffect(S1DPacketEntityEffect packetIn);

    void handleCombatEvent(S42PacketCombatEvent packetIn);

    void handleServerDifficulty(S41PacketServerDifficulty packetIn);

    void handleCamera(S43PacketCamera packetIn);

    void handleWorldBorder(S44PacketWorldBorder packetIn);

    void handleTitle(S45PacketTitle packetIn);

    void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn);

    void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn);

    void handleResourcePack(S48PacketResourcePackSend packetIn);

    void handleEntityNBT(S49PacketUpdateEntityNBT packetIn);
}
