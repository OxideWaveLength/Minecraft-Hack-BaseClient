package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public interface IWorldAccess
{
    void markBlockForUpdate(BlockPos pos);

    void notifyLightSet(BlockPos pos);

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2);

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    void playSound(String soundName, double x, double y, double z, float volume, float pitch);

    /**
     * Plays sound to all near players except the player reference given
     */
    void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch);

    void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_180442_15_);

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    void onEntityAdded(Entity entityIn);

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    void onEntityRemoved(Entity entityIn);

    void playRecord(String recordName, BlockPos blockPosIn);

    void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_);

    void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_);

    void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress);
}
