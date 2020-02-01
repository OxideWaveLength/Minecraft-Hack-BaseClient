package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo
{
    /** Instance of WorldInfo. */
    private final WorldInfo theWorldInfo;

    public DerivedWorldInfo(WorldInfo p_i2145_1_)
    {
        this.theWorldInfo = p_i2145_1_;
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound getNBTTagCompound()
    {
        return this.theWorldInfo.getNBTTagCompound();
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
     */
    public NBTTagCompound cloneNBTCompound(NBTTagCompound nbt)
    {
        return this.theWorldInfo.cloneNBTCompound(nbt);
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed()
    {
        return this.theWorldInfo.getSeed();
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX()
    {
        return this.theWorldInfo.getSpawnX();
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY()
    {
        return this.theWorldInfo.getSpawnY();
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ()
    {
        return this.theWorldInfo.getSpawnZ();
    }

    public long getWorldTotalTime()
    {
        return this.theWorldInfo.getWorldTotalTime();
    }

    /**
     * Get current world time
     */
    public long getWorldTime()
    {
        return this.theWorldInfo.getWorldTime();
    }

    public long getSizeOnDisk()
    {
        return this.theWorldInfo.getSizeOnDisk();
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.theWorldInfo.getPlayerNBTTagCompound();
    }

    /**
     * Get current world name
     */
    public String getWorldName()
    {
        return this.theWorldInfo.getWorldName();
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion()
    {
        return this.theWorldInfo.getSaveVersion();
    }

    /**
     * Return the last time the player was in this world.
     */
    public long getLastTimePlayed()
    {
        return this.theWorldInfo.getLastTimePlayed();
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering()
    {
        return this.theWorldInfo.isThundering();
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime()
    {
        return this.theWorldInfo.getThunderTime();
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining()
    {
        return this.theWorldInfo.isRaining();
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime()
    {
        return this.theWorldInfo.getRainTime();
    }

    /**
     * Gets the GameType.
     */
    public WorldSettings.GameType getGameType()
    {
        return this.theWorldInfo.getGameType();
    }

    /**
     * Set the x spawn position to the passed in value
     */
    public void setSpawnX(int x)
    {
    }

    /**
     * Sets the y spawn position
     */
    public void setSpawnY(int y)
    {
    }

    /**
     * Set the z spawn position to the passed in value
     */
    public void setSpawnZ(int z)
    {
    }

    public void setWorldTotalTime(long time)
    {
    }

    /**
     * Set current world time
     */
    public void setWorldTime(long time)
    {
    }

    public void setSpawn(BlockPos spawnPoint)
    {
    }

    public void setWorldName(String worldName)
    {
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(int version)
    {
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean thunderingIn)
    {
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int time)
    {
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean isRaining)
    {
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int time)
    {
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or disabled.
     */
    public boolean isMapFeaturesEnabled()
    {
        return this.theWorldInfo.isMapFeaturesEnabled();
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcoreModeEnabled()
    {
        return this.theWorldInfo.isHardcoreModeEnabled();
    }

    public WorldType getTerrainType()
    {
        return this.theWorldInfo.getTerrainType();
    }

    public void setTerrainType(WorldType type)
    {
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed()
    {
        return this.theWorldInfo.areCommandsAllowed();
    }

    public void setAllowCommands(boolean allow)
    {
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized()
    {
        return this.theWorldInfo.isInitialized();
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setServerInitialized(boolean initializedIn)
    {
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance()
    {
        return this.theWorldInfo.getGameRulesInstance();
    }

    public EnumDifficulty getDifficulty()
    {
        return this.theWorldInfo.getDifficulty();
    }

    public void setDifficulty(EnumDifficulty newDifficulty)
    {
    }

    public boolean isDifficultyLocked()
    {
        return this.theWorldInfo.isDifficultyLocked();
    }

    public void setDifficultyLocked(boolean locked)
    {
    }
}
