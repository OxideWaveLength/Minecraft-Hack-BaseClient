package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld
{
    private static final Logger logger = LogManager.getLogger();

    public AnvilSaveConverter(File p_i2144_1_)
    {
        super(p_i2144_1_);
    }

    /**
     * Returns the name of the save format.
     */
    public String getName()
    {
        return "Anvil";
    }

    public List<SaveFormatComparator> getSaveList() throws AnvilConverterException
    {
        if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory())
        {
            List<SaveFormatComparator> list = Lists.<SaveFormatComparator>newArrayList();
            File[] afile = this.savesDirectory.listFiles();

            for (File file1 : afile)
            {
                if (file1.isDirectory())
                {
                    String s = file1.getName();
                    WorldInfo worldinfo = this.getWorldInfo(s);

                    if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133))
                    {
                        boolean flag = worldinfo.getSaveVersion() != this.getSaveVersion();
                        String s1 = worldinfo.getWorldName();

                        if (StringUtils.isEmpty(s1))
                        {
                            s1 = s;
                        }

                        long i = 0L;
                        list.add(new SaveFormatComparator(s, s1, worldinfo.getLastTimePlayed(), i, worldinfo.getGameType(), flag, worldinfo.isHardcoreModeEnabled(), worldinfo.areCommandsAllowed()));
                    }
                }
            }

            return list;
        }
        else
        {
            throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
        }
    }

    protected int getSaveVersion()
    {
        return 19133;
    }

    public void flushCache()
    {
        RegionFileCache.clearRegionFileReferences();
    }

    /**
     * Returns back a loader for the specified save directory
     */
    public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata)
    {
        return new AnvilSaveHandler(this.savesDirectory, saveName, storePlayerdata);
    }

    public boolean func_154334_a(String saveName)
    {
        WorldInfo worldinfo = this.getWorldInfo(saveName);
        return worldinfo != null && worldinfo.getSaveVersion() == 19132;
    }

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    public boolean isOldMapFormat(String saveName)
    {
        WorldInfo worldinfo = this.getWorldInfo(saveName);
        return worldinfo != null && worldinfo.getSaveVersion() != this.getSaveVersion();
    }

    /**
     * converts the map to mcRegion
     */
    public boolean convertMapFormat(String filename, IProgressUpdate progressCallback)
    {
        progressCallback.setLoadingProgress(0);
        List<File> list = Lists.<File>newArrayList();
        List<File> list1 = Lists.<File>newArrayList();
        List<File> list2 = Lists.<File>newArrayList();
        File file1 = new File(this.savesDirectory, filename);
        File file2 = new File(file1, "DIM-1");
        File file3 = new File(file1, "DIM1");
        logger.info("Scanning folders...");
        this.addRegionFilesToCollection(file1, list);

        if (file2.exists())
        {
            this.addRegionFilesToCollection(file2, list1);
        }

        if (file3.exists())
        {
            this.addRegionFilesToCollection(file3, list2);
        }

        int i = list.size() + list1.size() + list2.size();
        logger.info("Total conversion count is " + i);
        WorldInfo worldinfo = this.getWorldInfo(filename);
        WorldChunkManager worldchunkmanager = null;

        if (worldinfo.getTerrainType() == WorldType.FLAT)
        {
            worldchunkmanager = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
        }
        else
        {
            worldchunkmanager = new WorldChunkManager(worldinfo.getSeed(), worldinfo.getTerrainType(), worldinfo.getGeneratorOptions());
        }

        this.convertFile(new File(file1, "region"), list, worldchunkmanager, 0, i, progressCallback);
        this.convertFile(new File(file2, "region"), list1, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), list.size(), i, progressCallback);
        this.convertFile(new File(file3, "region"), list2, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), list.size() + list1.size(), i, progressCallback);
        worldinfo.setSaveVersion(19133);

        if (worldinfo.getTerrainType() == WorldType.DEFAULT_1_1)
        {
            worldinfo.setTerrainType(WorldType.DEFAULT);
        }

        this.createFile(filename);
        ISaveHandler isavehandler = this.getSaveLoader(filename, false);
        isavehandler.saveWorldInfo(worldinfo);
        return true;
    }

    /**
     * par: filename for the level.dat_mcr backup
     */
    private void createFile(String filename)
    {
        File file1 = new File(this.savesDirectory, filename);

        if (!file1.exists())
        {
            logger.warn("Unable to create level.dat_mcr backup");
        }
        else
        {
            File file2 = new File(file1, "level.dat");

            if (!file2.exists())
            {
                logger.warn("Unable to create level.dat_mcr backup");
            }
            else
            {
                File file3 = new File(file1, "level.dat_mcr");

                if (!file2.renameTo(file3))
                {
                    logger.warn("Unable to create level.dat_mcr backup");
                }
            }
        }
    }

    private void convertFile(File p_75813_1_, Iterable<File> p_75813_2_, WorldChunkManager p_75813_3_, int p_75813_4_, int p_75813_5_, IProgressUpdate p_75813_6_)
    {
        for (File file1 : p_75813_2_)
        {
            this.convertChunks(p_75813_1_, file1, p_75813_3_, p_75813_4_, p_75813_5_, p_75813_6_);
            ++p_75813_4_;
            int i = (int)Math.round(100.0D * (double)p_75813_4_ / (double)p_75813_5_);
            p_75813_6_.setLoadingProgress(i);
        }
    }

    /**
     * copies a 32x32 chunk set from par2File to par1File, via AnvilConverterData
     */
    private void convertChunks(File p_75811_1_, File p_75811_2_, WorldChunkManager p_75811_3_, int p_75811_4_, int p_75811_5_, IProgressUpdate progressCallback)
    {
        try
        {
            String s = p_75811_2_.getName();
            RegionFile regionfile = new RegionFile(p_75811_2_);
            RegionFile regionfile1 = new RegionFile(new File(p_75811_1_, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

            for (int i = 0; i < 32; ++i)
            {
                for (int j = 0; j < 32; ++j)
                {
                    if (regionfile.isChunkSaved(i, j) && !regionfile1.isChunkSaved(i, j))
                    {
                        DataInputStream datainputstream = regionfile.getChunkDataInputStream(i, j);

                        if (datainputstream == null)
                        {
                            logger.warn("Failed to fetch input stream");
                        }
                        else
                        {
                            NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                            datainputstream.close();
                            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Level");
                            ChunkLoader.AnvilConverterData chunkloader$anvilconverterdata = ChunkLoader.load(nbttagcompound1);
                            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                            nbttagcompound2.setTag("Level", nbttagcompound3);
                            ChunkLoader.convertToAnvilFormat(chunkloader$anvilconverterdata, nbttagcompound3, p_75811_3_);
                            DataOutputStream dataoutputstream = regionfile1.getChunkDataOutputStream(i, j);
                            CompressedStreamTools.write(nbttagcompound2, dataoutputstream);
                            dataoutputstream.close();
                        }
                    }
                }

                int k = (int)Math.round(100.0D * (double)(p_75811_4_ * 1024) / (double)(p_75811_5_ * 1024));
                int l = (int)Math.round(100.0D * (double)((i + 1) * 32 + p_75811_4_ * 1024) / (double)(p_75811_5_ * 1024));

                if (l > k)
                {
                    progressCallback.setLoadingProgress(l);
                }
            }

            regionfile.close();
            regionfile1.close();
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    /**
     * filters the files in the par1 directory, and adds them to the par2 collections
     */
    private void addRegionFilesToCollection(File worldDir, Collection<File> collection)
    {
        File file1 = new File(worldDir, "region");
        File[] afile = file1.listFiles(new FilenameFilter()
        {
            public boolean accept(File p_accept_1_, String p_accept_2_)
            {
                return p_accept_2_.endsWith(".mcr");
            }
        });

        if (afile != null)
        {
            Collections.addAll(collection, afile);
        }
    }
}
