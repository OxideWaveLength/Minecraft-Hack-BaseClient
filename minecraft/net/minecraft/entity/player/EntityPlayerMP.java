package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements ICrafting
{
    private static final Logger logger = LogManager.getLogger();
    private String translator = "en_US";

    /**
     * The NetServerHandler assigned to this player by the ServerConfigurationManager.
     */
    public NetHandlerPlayServer playerNetServerHandler;

    /** Reference to the MinecraftServer object. */
    public final MinecraftServer mcServer;

    /** The ItemInWorldManager belonging to this player */
    public final ItemInWorldManager theItemInWorldManager;

    /** player X position as seen by PlayerManager */
    public double managedPosX;

    /** player Z position as seen by PlayerManager */
    public double managedPosZ;
    public final List<ChunkCoordIntPair> loadedChunks = Lists.<ChunkCoordIntPair>newLinkedList();
    private final List<Integer> destroyedItemsNetCache = Lists.<Integer>newLinkedList();
    private final StatisticsFile statsFile;

    /**
     * the total health of the player, includes actual health and absorption health. Updated every tick.
     */
    private float combinedHealth = Float.MIN_VALUE;

    /** amount of health the client was last set to */
    private float lastHealth = -1.0E8F;

    /** set to foodStats.GetFoodLevel */
    private int lastFoodLevel = -99999999;

    /** set to foodStats.getSaturationLevel() == 0.0F each tick */
    private boolean wasHungry = true;

    /** Amount of experience the client was last set to */
    private int lastExperience = -99999999;
    private int respawnInvulnerabilityTicks = 60;
    private EntityPlayer.EnumChatVisibility chatVisibility;
    private boolean chatColours = true;
    private long playerLastActiveTime = System.currentTimeMillis();

    /** The entity the player is currently spectating through. */
    private Entity spectatingEntity = null;

    /**
     * The currently in use window ID. Incremented every time a window is opened.
     */
    private int currentWindowId;

    /**
     * set to true when player is moving quantity of items from one inventory to another(crafting) but item in either
     * slot is not changed
     */
    public boolean isChangingQuantityOnly;
    public int ping;

    /**
     * Set when a player beats the ender dragon, used to respawn the player at the spawn point while retaining inventory
     * and XP
     */
    public boolean playerConqueredTheEnd;

    public EntityPlayerMP(MinecraftServer server, WorldServer worldIn, GameProfile profile, ItemInWorldManager interactionManager)
    {
        super(worldIn, profile);
        interactionManager.thisPlayerMP = this;
        this.theItemInWorldManager = interactionManager;
        BlockPos blockpos = worldIn.getSpawnPoint();

        if (!worldIn.provider.getHasNoSky() && worldIn.getWorldInfo().getGameType() != WorldSettings.GameType.ADVENTURE)
        {
            int i = Math.max(5, server.getSpawnProtectionSize() - 6);
            int j = MathHelper.floor_double(worldIn.getWorldBorder().getClosestDistance((double)blockpos.getX(), (double)blockpos.getZ()));

            if (j < i)
            {
                i = j;
            }

            if (j <= 1)
            {
                i = 1;
            }

            blockpos = worldIn.getTopSolidOrLiquidBlock(blockpos.add(this.rand.nextInt(i * 2) - i, 0, this.rand.nextInt(i * 2) - i));
        }

        this.mcServer = server;
        this.statsFile = server.getConfigurationManager().getPlayerStatsFile(this);
        this.stepHeight = 0.0F;
        this.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);

        while (!worldIn.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.posY < 255.0D)
        {
            this.setPosition(this.posX, this.posY + 1.0D, this.posZ);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("playerGameType", 99))
        {
            if (MinecraftServer.getServer().getForceGamemode())
            {
                this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
            }
            else
            {
                this.theItemInWorldManager.setGameType(WorldSettings.GameType.getByID(tagCompund.getInteger("playerGameType")));
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(int levels)
    {
        super.addExperienceLevel(levels);
        this.lastExperience = -1;
    }

    public void removeExperienceLevel(int levels)
    {
        super.removeExperienceLevel(levels);
        this.lastExperience = -1;
    }

    public void addSelfToInternalCraftingInventory()
    {
        this.openContainer.onCraftGuiOpened(this);
    }

    /**
     * Sends an ENTER_COMBAT packet to the client
     */
    public void sendEnterCombat()
    {
        super.sendEnterCombat();
        this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), S42PacketCombatEvent.Event.ENTER_COMBAT));
    }

    /**
     * Sends an END_COMBAT packet to the client
     */
    public void sendEndCombat()
    {
        super.sendEndCombat();
        this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), S42PacketCombatEvent.Event.END_COMBAT));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.theItemInWorldManager.updateBlockRemoving();
        --this.respawnInvulnerabilityTicks;

        if (this.hurtResistantTime > 0)
        {
            --this.hurtResistantTime;
        }

        this.openContainer.detectAndSendChanges();

        if (!this.worldObj.isRemote && !this.openContainer.canInteractWith(this))
        {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }

        while (!this.destroyedItemsNetCache.isEmpty())
        {
            int i = Math.min(this.destroyedItemsNetCache.size(), Integer.MAX_VALUE);
            int[] aint = new int[i];
            Iterator<Integer> iterator = this.destroyedItemsNetCache.iterator();
            int j = 0;

            while (iterator.hasNext() && j < i)
            {
                aint[j++] = ((Integer)iterator.next()).intValue();
                iterator.remove();
            }

            this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(aint));
        }

        if (!this.loadedChunks.isEmpty())
        {
            List<Chunk> list = Lists.<Chunk>newArrayList();
            Iterator<ChunkCoordIntPair> iterator1 = this.loadedChunks.iterator();
            List<TileEntity> list1 = Lists.<TileEntity>newArrayList();

            while (iterator1.hasNext() && ((List)list).size() < 10)
            {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair)iterator1.next();

                if (chunkcoordintpair != null)
                {
                    if (this.worldObj.isBlockLoaded(new BlockPos(chunkcoordintpair.chunkXPos << 4, 0, chunkcoordintpair.chunkZPos << 4)))
                    {
                        Chunk chunk = this.worldObj.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);

                        if (chunk.isPopulated())
                        {
                            list.add(chunk);
                            list1.addAll(((WorldServer)this.worldObj).getTileEntitiesIn(chunkcoordintpair.chunkXPos * 16, 0, chunkcoordintpair.chunkZPos * 16, chunkcoordintpair.chunkXPos * 16 + 16, 256, chunkcoordintpair.chunkZPos * 16 + 16));
                            iterator1.remove();
                        }
                    }
                }
                else
                {
                    iterator1.remove();
                }
            }

            if (!list.isEmpty())
            {
                if (list.size() == 1)
                {
                    this.playerNetServerHandler.sendPacket(new S21PacketChunkData((Chunk)list.get(0), true, 65535));
                }
                else
                {
                    this.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(list));
                }

                for (TileEntity tileentity : list1)
                {
                    this.sendTileEntityUpdate(tileentity);
                }

                for (Chunk chunk1 : list)
                {
                    this.getServerForPlayer().getEntityTracker().func_85172_a(this, chunk1);
                }
            }
        }

        Entity entity = this.getSpectatingEntity();

        if (entity != this)
        {
            if (!entity.isEntityAlive())
            {
                this.setSpectatingEntity(this);
            }
            else
            {
                this.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this);

                if (this.isSneaking())
                {
                    this.setSpectatingEntity(this);
                }
            }
        }
    }

    public void onUpdateEntity()
    {
        try
        {
            super.onUpdate();

            for (int i = 0; i < this.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.inventory.getStackInSlot(i);

                if (itemstack != null && itemstack.getItem().isMap())
                {
                    Packet packet = ((ItemMapBase)itemstack.getItem()).createMapDataPacket(itemstack, this.worldObj, this);

                    if (packet != null)
                    {
                        this.playerNetServerHandler.sendPacket(packet);
                    }
                }
            }

            if (this.getHealth() != this.lastHealth || this.lastFoodLevel != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry)
            {
                this.playerNetServerHandler.sendPacket(new S06PacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
                this.lastHealth = this.getHealth();
                this.lastFoodLevel = this.foodStats.getFoodLevel();
                this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionAmount() != this.combinedHealth)
            {
                this.combinedHealth = this.getHealth() + this.getAbsorptionAmount();

                for (ScoreObjective scoreobjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.health))
                {
                    this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreobjective).func_96651_a(Arrays.<EntityPlayer>asList(new EntityPlayer[] {this}));
                }
            }

            if (this.experienceTotal != this.lastExperience)
            {
                this.lastExperience = this.experienceTotal;
                this.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
            }

            if (this.ticksExisted % 20 * 5 == 0 && !this.getStatFile().hasAchievementUnlocked(AchievementList.exploreAllBiomes))
            {
                this.updateBiomesExplored();
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Updates all biomes that have been explored by this player and triggers Adventuring Time if player qualifies.
     */
    protected void updateBiomesExplored()
    {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)));
        String s = biomegenbase.biomeName;
        JsonSerializableSet jsonserializableset = (JsonSerializableSet)this.getStatFile().func_150870_b(AchievementList.exploreAllBiomes);

        if (jsonserializableset == null)
        {
            jsonserializableset = (JsonSerializableSet)this.getStatFile().func_150872_a(AchievementList.exploreAllBiomes, new JsonSerializableSet());
        }

        jsonserializableset.add(s);

        if (this.getStatFile().canUnlockAchievement(AchievementList.exploreAllBiomes) && jsonserializableset.size() >= BiomeGenBase.explorationBiomesList.size())
        {
            Set<BiomeGenBase> set = Sets.newHashSet(BiomeGenBase.explorationBiomesList);

            for (String s1 : jsonserializableset)
            {
                Iterator<BiomeGenBase> iterator = set.iterator();

                while (iterator.hasNext())
                {
                    BiomeGenBase biomegenbase1 = (BiomeGenBase)iterator.next();

                    if (biomegenbase1.biomeName.equals(s1))
                    {
                        iterator.remove();
                    }
                }

                if (set.isEmpty())
                {
                    break;
                }
            }

            if (set.isEmpty())
            {
                this.triggerAchievement(AchievementList.exploreAllBiomes);
            }
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        if (this.worldObj.getGameRules().getBoolean("showDeathMessages"))
        {
            Team team = this.getTeam();

            if (team != null && team.getDeathMessageVisibility() != Team.EnumVisible.ALWAYS)
            {
                if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS)
                {
                    this.mcServer.getConfigurationManager().sendMessageToAllTeamMembers(this, this.getCombatTracker().getDeathMessage());
                }
                else if (team.getDeathMessageVisibility() == Team.EnumVisible.HIDE_FOR_OWN_TEAM)
                {
                    this.mcServer.getConfigurationManager().sendMessageToTeamOrEvryPlayer(this, this.getCombatTracker().getDeathMessage());
                }
            }
            else
            {
                this.mcServer.getConfigurationManager().sendChatMsg(this.getCombatTracker().getDeathMessage());
            }
        }

        if (!this.worldObj.getGameRules().getBoolean("keepInventory"))
        {
            this.inventory.dropAllItems();
        }

        for (ScoreObjective scoreobjective : this.worldObj.getScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.deathCount))
        {
            Score score = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreobjective);
            score.func_96648_a();
        }

        EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (entitylivingbase != null)
        {
            EntityList.EntityEggInfo entitylist$entityegginfo = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(EntityList.getEntityID(entitylivingbase)));

            if (entitylist$entityegginfo != null)
            {
                this.triggerAchievement(entitylist$entityegginfo.field_151513_e);
            }

            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }

        this.triggerAchievement(StatList.deathsStat);
        this.func_175145_a(StatList.timeSinceDeathStat);
        this.getCombatTracker().reset();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            boolean flag = this.mcServer.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(source.damageType);

            if (!flag && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.outOfWorld)
            {
                return false;
            }
            else
            {
                if (source instanceof EntityDamageSource)
                {
                    Entity entity = source.getEntity();

                    if (entity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity))
                    {
                        return false;
                    }

                    if (entity instanceof EntityArrow)
                    {
                        EntityArrow entityarrow = (EntityArrow)entity;

                        if (entityarrow.shootingEntity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entityarrow.shootingEntity))
                        {
                            return false;
                        }
                    }
                }

                return super.attackEntityFrom(source, amount);
            }
        }
    }

    public boolean canAttackPlayer(EntityPlayer other)
    {
        return !this.canPlayersAttack() ? false : super.canAttackPlayer(other);
    }

    /**
     * Returns if other players can attack this player
     */
    private boolean canPlayersAttack()
    {
        return this.mcServer.isPVPEnabled();
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(int dimensionId)
    {
        if (this.dimension == 1 && dimensionId == 1)
        {
            this.triggerAchievement(AchievementList.theEnd2);
            this.worldObj.removeEntity(this);
            this.playerConqueredTheEnd = true;
            this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0.0F));
        }
        else
        {
            if (this.dimension == 0 && dimensionId == 1)
            {
                this.triggerAchievement(AchievementList.theEnd);
                BlockPos blockpos = this.mcServer.worldServerForDimension(dimensionId).getSpawnCoordinate();

                if (blockpos != null)
                {
                    this.playerNetServerHandler.setPlayerLocation((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 0.0F, 0.0F);
                }

                dimensionId = 1;
            }
            else
            {
                this.triggerAchievement(AchievementList.portal);
            }

            this.mcServer.getConfigurationManager().transferPlayerToDimension(this, dimensionId);
            this.lastExperience = -1;
            this.lastHealth = -1.0F;
            this.lastFoodLevel = -1;
        }
    }

    public boolean isSpectatedByPlayer(EntityPlayerMP player)
    {
        return player.isSpectator() ? this.getSpectatingEntity() == this : (this.isSpectator() ? false : super.isSpectatedByPlayer(player));
    }

    private void sendTileEntityUpdate(TileEntity p_147097_1_)
    {
        if (p_147097_1_ != null)
        {
            Packet packet = p_147097_1_.getDescriptionPacket();

            if (packet != null)
            {
                this.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity p_71001_1_, int p_71001_2_)
    {
        super.onItemPickup(p_71001_1_, p_71001_2_);
        this.openContainer.detectAndSendChanges();
    }

    public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation)
    {
        EntityPlayer.EnumStatus entityplayer$enumstatus = super.trySleep(bedLocation);

        if (entityplayer$enumstatus == EntityPlayer.EnumStatus.OK)
        {
            Packet packet = new S0APacketUseBed(this, bedLocation);
            this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntity(this, packet);
            this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.playerNetServerHandler.sendPacket(packet);
        }

        return entityplayer$enumstatus;
    }

    /**
     * Wake up the player if they're sleeping.
     */
    public void wakeUpPlayer(boolean p_70999_1_, boolean updateWorldFlag, boolean setSpawn)
    {
        if (this.isPlayerSleeping())
        {
            this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 2));
        }

        super.wakeUpPlayer(p_70999_1_, updateWorldFlag, setSpawn);

        if (this.playerNetServerHandler != null)
        {
            this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity entityIn)
    {
        Entity entity = this.ridingEntity;
        super.mountEntity(entityIn);

        if (entityIn != entity)
        {
            this.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this, this.ridingEntity));
            this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos)
    {
    }

    /**
     * process player falling based on movement packet
     */
    public void handleFalling(double p_71122_1_, boolean p_71122_3_)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posY - 0.20000000298023224D);
        int k = MathHelper.floor_double(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        Block block = this.worldObj.getBlockState(blockpos).getBlock();

        if (block.getMaterial() == Material.air)
        {
            Block block1 = this.worldObj.getBlockState(blockpos.down()).getBlock();

            if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)
            {
                blockpos = blockpos.down();
                block = this.worldObj.getBlockState(blockpos).getBlock();
            }
        }

        super.updateFallState(p_71122_1_, p_71122_3_, block, blockpos);
    }

    public void openEditSign(TileEntitySign signTile)
    {
        signTile.setPlayer(this);
        this.playerNetServerHandler.sendPacket(new S36PacketSignEditorOpen(signTile.getPos()));
    }

    /**
     * get the next window id to use
     */
    private void getNextWindowId()
    {
        this.currentWindowId = this.currentWindowId % 100 + 1;
    }

    public void displayGui(IInteractionObject guiOwner)
    {
        this.getNextWindowId();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, guiOwner.getGuiID(), guiOwner.getDisplayName()));
        this.openContainer = guiOwner.createContainer(this.inventory, this);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest(IInventory chestInventory)
    {
        if (this.openContainer != this.inventoryContainer)
        {
            this.closeScreen();
        }

        if (chestInventory instanceof ILockableContainer)
        {
            ILockableContainer ilockablecontainer = (ILockableContainer)chestInventory;

            if (ilockablecontainer.isLocked() && !this.canOpen(ilockablecontainer.getLockCode()) && !this.isSpectator())
            {
                this.playerNetServerHandler.sendPacket(new S02PacketChat(new ChatComponentTranslation("container.isLocked", new Object[] {chestInventory.getDisplayName()}), (byte)2));
                this.playerNetServerHandler.sendPacket(new S29PacketSoundEffect("random.door_close", this.posX, this.posY, this.posZ, 1.0F, 1.0F));
                return;
            }
        }

        this.getNextWindowId();

        if (chestInventory instanceof IInteractionObject)
        {
            this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, ((IInteractionObject)chestInventory).getGuiID(), chestInventory.getDisplayName(), chestInventory.getSizeInventory()));
            this.openContainer = ((IInteractionObject)chestInventory).createContainer(this.inventory, this);
        }
        else
        {
            this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, "minecraft:container", chestInventory.getDisplayName(), chestInventory.getSizeInventory()));
            this.openContainer = new ContainerChest(this.inventory, chestInventory, this);
        }

        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    public void displayVillagerTradeGui(IMerchant villager)
    {
        this.getNextWindowId();
        this.openContainer = new ContainerMerchant(this.inventory, villager, this.worldObj);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
        IInventory iinventory = ((ContainerMerchant)this.openContainer).getMerchantInventory();
        IChatComponent ichatcomponent = villager.getDisplayName();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, "minecraft:villager", ichatcomponent, iinventory.getSizeInventory()));
        MerchantRecipeList merchantrecipelist = villager.getRecipes(this);

        if (merchantrecipelist != null)
        {
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeInt(this.currentWindowId);
            merchantrecipelist.writeToBuf(packetbuffer);
            this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetbuffer));
        }
    }

    public void displayGUIHorse(EntityHorse horse, IInventory horseInventory)
    {
        if (this.openContainer != this.inventoryContainer)
        {
            this.closeScreen();
        }

        this.getNextWindowId();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, "EntityHorse", horseInventory.getDisplayName(), horseInventory.getSizeInventory(), horse.getEntityId()));
        this.openContainer = new ContainerHorseInventory(this.inventory, horseInventory, horse, this);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    /**
     * Displays the GUI for interacting with a book.
     */
    public void displayGUIBook(ItemStack bookStack)
    {
        Item item = bookStack.getItem();

        if (item == Items.written_book)
        {
            this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|BOpen", new PacketBuffer(Unpooled.buffer())));
        }
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot. Args: Container, slot number, slot contents
     */
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
    {
        if (!(containerToSend.getSlot(slotInd) instanceof SlotCrafting))
        {
            if (!this.isChangingQuantityOnly)
            {
                this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(containerToSend.windowId, slotInd, stack));
            }
        }
    }

    public void sendContainerToPlayer(Container p_71120_1_)
    {
        this.updateCraftingInventory(p_71120_1_, p_71120_1_.getInventory());
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList)
    {
        this.playerNetServerHandler.sendPacket(new S30PacketWindowItems(containerToSend.windowId, itemsList));
        this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue)
    {
        this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(containerIn.windowId, varToUpdate, newValue));
    }

    public void func_175173_a(Container p_175173_1_, IInventory p_175173_2_)
    {
        for (int i = 0; i < p_175173_2_.getFieldCount(); ++i)
        {
            this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(p_175173_1_.windowId, i, p_175173_2_.getField(i)));
        }
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeScreen()
    {
        this.playerNetServerHandler.sendPacket(new S2EPacketCloseWindow(this.openContainer.windowId));
        this.closeContainer();
    }

    /**
     * updates item held by mouse
     */
    public void updateHeldItem()
    {
        if (!this.isChangingQuantityOnly)
        {
            this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
        }
    }

    /**
     * Closes the container the player currently has open.
     */
    public void closeContainer()
    {
        this.openContainer.onContainerClosed(this);
        this.openContainer = this.inventoryContainer;
    }

    public void setEntityActionState(float p_110430_1_, float p_110430_2_, boolean p_110430_3_, boolean sneaking)
    {
        if (this.ridingEntity != null)
        {
            if (p_110430_1_ >= -1.0F && p_110430_1_ <= 1.0F)
            {
                this.moveStrafing = p_110430_1_;
            }

            if (p_110430_2_ >= -1.0F && p_110430_2_ <= 1.0F)
            {
                this.moveForward = p_110430_2_;
            }

            this.isJumping = p_110430_3_;
            this.setSneaking(sneaking);
        }
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase stat, int amount)
    {
        if (stat != null)
        {
            this.statsFile.increaseStat(this, stat, amount);

            for (ScoreObjective scoreobjective : this.getWorldScoreboard().getObjectivesFromCriteria(stat.func_150952_k()))
            {
                this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreobjective).increseScore(amount);
            }

            if (this.statsFile.func_150879_e())
            {
                this.statsFile.func_150876_a(this);
            }
        }
    }

    public void func_175145_a(StatBase p_175145_1_)
    {
        if (p_175145_1_ != null)
        {
            this.statsFile.unlockAchievement(this, p_175145_1_, 0);

            for (ScoreObjective scoreobjective : this.getWorldScoreboard().getObjectivesFromCriteria(p_175145_1_.func_150952_k()))
            {
                this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreobjective).setScorePoints(0);
            }

            if (this.statsFile.func_150879_e())
            {
                this.statsFile.func_150876_a(this);
            }
        }
    }

    public void mountEntityAndWakeUp()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.mountEntity(this);
        }

        if (this.sleeping)
        {
            this.wakeUpPlayer(true, false, false);
        }
    }

    /**
     * this function is called when a players inventory is sent to him, lastHealth is updated on any dimension
     * transitions, then reset.
     */
    public void setPlayerHealthUpdated()
    {
        this.lastHealth = -1.0E8F;
    }

    public void addChatComponentMessage(IChatComponent chatComponent)
    {
        this.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponent));
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish()
    {
        this.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(this, (byte)9));
        super.onItemUseFinish();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void setItemInUse(ItemStack stack, int duration)
    {
        super.setItemInUse(stack, duration);

        if (stack != null && stack.getItem() != null && stack.getItem().getItemUseAction(stack) == EnumAction.EAT)
        {
            this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(this, 3));
        }
    }

    /**
     * Copies the values from the given player into this player if boolean par2 is true. Always clones Ender Chest
     * Inventory.
     */
    public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd)
    {
        super.clonePlayer(oldPlayer, respawnFromEnd);
        this.lastExperience = -1;
        this.lastHealth = -1.0F;
        this.lastFoodLevel = -1;
        this.destroyedItemsNetCache.addAll(((EntityPlayerMP)oldPlayer).destroyedItemsNetCache);
    }

    protected void onNewPotionEffect(PotionEffect id)
    {
        super.onNewPotionEffect(id);
        this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
    }

    protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_)
    {
        super.onChangedPotionEffect(id, p_70695_2_);
        this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
    }

    protected void onFinishedPotionEffect(PotionEffect p_70688_1_)
    {
        super.onFinishedPotionEffect(p_70688_1_);
        this.playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(this.getEntityId(), p_70688_1_));
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void setPositionAndUpdate(double x, double y, double z)
    {
        this.playerNetServerHandler.setPlayerLocation(x, y, z, this.rotationYaw, this.rotationPitch);
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(Entity entityHit)
    {
        this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(entityHit, 4));
    }

    public void onEnchantmentCritical(Entity entityHit)
    {
        this.getServerForPlayer().getEntityTracker().func_151248_b(this, new S0BPacketAnimation(entityHit, 5));
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities()
    {
        if (this.playerNetServerHandler != null)
        {
            this.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(this.capabilities));
            this.updatePotionMetadata();
        }
    }

    public WorldServer getServerForPlayer()
    {
        return (WorldServer)this.worldObj;
    }

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(WorldSettings.GameType gameType)
    {
        this.theItemInWorldManager.setGameType(gameType);
        this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(3, (float)gameType.getID()));

        if (gameType == WorldSettings.GameType.SPECTATOR)
        {
            this.mountEntity((Entity)null);
        }
        else
        {
            this.setSpectatingEntity(this);
        }

        this.sendPlayerAbilities();
        this.markPotionsDirty();
    }

    /**
     * Returns true if the player is in spectator mode.
     */
    public boolean isSpectator()
    {
        return this.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR;
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage(IChatComponent component)
    {
        this.playerNetServerHandler.sendPacket(new S02PacketChat(component));
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        if ("seed".equals(commandName) && !this.mcServer.isDedicatedServer())
        {
            return true;
        }
        else if (!"tell".equals(commandName) && !"help".equals(commandName) && !"me".equals(commandName) && !"trigger".equals(commandName))
        {
            if (this.mcServer.getConfigurationManager().canSendCommands(this.getGameProfile()))
            {
                UserListOpsEntry userlistopsentry = (UserListOpsEntry)this.mcServer.getConfigurationManager().getOppedPlayers().getEntry(this.getGameProfile());
                return userlistopsentry != null ? userlistopsentry.getPermissionLevel() >= permLevel : this.mcServer.getOpPermissionLevel() >= permLevel;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Gets the player's IP address. Used in /banip.
     */
    public String getPlayerIP()
    {
        String s = this.playerNetServerHandler.netManager.getRemoteAddress().toString();
        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void handleClientSettings(C15PacketClientSettings packetIn)
    {
        this.translator = packetIn.getLang();
        this.chatVisibility = packetIn.getChatVisibility();
        this.chatColours = packetIn.isColorsEnabled();
        this.getDataWatcher().updateObject(10, Byte.valueOf((byte)packetIn.getModelPartFlags()));
    }

    public EntityPlayer.EnumChatVisibility getChatVisibility()
    {
        return this.chatVisibility;
    }

    public void loadResourcePack(String url, String hash)
    {
        this.playerNetServerHandler.sendPacket(new S48PacketResourcePackSend(url, hash));
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    public BlockPos getPosition()
    {
        return new BlockPos(this.posX, this.posY + 0.5D, this.posZ);
    }

    public void markPlayerActive()
    {
        this.playerLastActiveTime = MinecraftServer.getCurrentTimeMillis();
    }

    /**
     * Gets the stats file for reading achievements
     */
    public StatisticsFile getStatFile()
    {
        return this.statsFile;
    }

    /**
     * Sends a packet to the player to remove an entity.
     */
    public void removeEntity(Entity p_152339_1_)
    {
        if (p_152339_1_ instanceof EntityPlayer)
        {
            this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(new int[] {p_152339_1_.getEntityId()}));
        }
        else
        {
            this.destroyedItemsNetCache.add(Integer.valueOf(p_152339_1_.getEntityId()));
        }
    }

    /**
     * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
     * ambience, and invisibility metadata values
     */
    protected void updatePotionMetadata()
    {
        if (this.isSpectator())
        {
            this.resetPotionEffectMetadata();
            this.setInvisible(true);
        }
        else
        {
            super.updatePotionMetadata();
        }

        this.getServerForPlayer().getEntityTracker().func_180245_a(this);
    }

    public Entity getSpectatingEntity()
    {
        return (Entity)(this.spectatingEntity == null ? this : this.spectatingEntity);
    }

    public void setSpectatingEntity(Entity entityToSpectate)
    {
        Entity entity = this.getSpectatingEntity();
        this.spectatingEntity = (Entity)(entityToSpectate == null ? this : entityToSpectate);

        if (entity != this.spectatingEntity)
        {
            this.playerNetServerHandler.sendPacket(new S43PacketCamera(this.spectatingEntity));
            this.setPositionAndUpdate(this.spectatingEntity.posX, this.spectatingEntity.posY, this.spectatingEntity.posZ);
        }
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.  The equipped item has hitEntity
     * called on it. Args: targetEntity
     */
    public void attackTargetEntityWithCurrentItem(Entity targetEntity)
    {
        if (this.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR)
        {
            this.setSpectatingEntity(targetEntity);
        }
        else
        {
            super.attackTargetEntityWithCurrentItem(targetEntity);
        }
    }

    public long getLastActiveTime()
    {
        return this.playerLastActiveTime;
    }

    /**
     * Returns null which indicates the tab list should just display the player's name, return a different value to
     * display the specified text instead of the player's name
     */
    public IChatComponent getTabListDisplayName()
    {
        return null;
    }
}
