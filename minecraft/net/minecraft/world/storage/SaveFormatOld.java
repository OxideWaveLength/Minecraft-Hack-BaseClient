package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reference to the File object representing the directory for the world saves
     */
    protected final File savesDirectory;

    public SaveFormatOld(File p_i2147_1_)
    {
        if (!p_i2147_1_.exists())
        {
            p_i2147_1_.mkdirs();
        }

        this.savesDirectory = p_i2147_1_;
    }

    /**
     * Returns the name of the save format.
     */
    public String getName()
    {
        return "Old Format";
    }

    public List<SaveFormatComparator> getSaveList() throws AnvilConverterException
    {
        List<SaveFormatComparator> list = Lists.<SaveFormatComparator>newArrayList();

        for (int i = 0; i < 5; ++i)
        {
            String s = "World" + (i + 1);
            WorldInfo worldinfo = this.getWorldInfo(s);

            if (worldinfo != null)
            {
                list.add(new SaveFormatComparator(s, "", worldinfo.getLastTimePlayed(), worldinfo.getSizeOnDisk(), worldinfo.getGameType(), false, worldinfo.isHardcoreModeEnabled(), worldinfo.areCommandsAllowed()));
            }
        }

        return list;
    }

    public void flushCache()
    {
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldInfo getWorldInfo(String saveName)
    {
        File file1 = new File(this.savesDirectory, saveName);

        if (!file1.exists())
        {
            return null;
        }
        else
        {
            File file2 = new File(file1, "level.dat");

            if (file2.exists())
            {
                try
                {
                    NBTTagCompound nbttagcompound2 = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompoundTag("Data");
                    return new WorldInfo(nbttagcompound3);
                }
                catch (Exception exception1)
                {
                    logger.error((String)("Exception reading " + file2), (Throwable)exception1);
                }
            }

            file2 = new File(file1, "level.dat_old");

            if (file2.exists())
            {
                try
                {
                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                    return new WorldInfo(nbttagcompound1);
                }
                catch (Exception exception)
                {
                    logger.error((String)("Exception reading " + file2), (Throwable)exception);
                }
            }

            return null;
        }
    }

    /**
     * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
     * data.
     */
    public void renameWorld(String dirName, String newName)
    {
        File file1 = new File(this.savesDirectory, dirName);

        if (file1.exists())
        {
            File file2 = new File(file1, "level.dat");

            if (file2.exists())
            {
                try
                {
                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                    nbttagcompound1.setString("LevelName", newName);
                    CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file2));
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    public boolean func_154335_d(String p_154335_1_)
    {
        File file1 = new File(this.savesDirectory, p_154335_1_);

        if (file1.exists())
        {
            return false;
        }
        else
        {
            try
            {
                file1.mkdir();
                file1.delete();
                return true;
            }
            catch (Throwable throwable)
            {
                logger.warn("Couldn\'t make new level", throwable);
                return false;
            }
        }
    }

    /**
     * @args: Takes one argument - the name of the directory of the world to delete. @desc: Delete the world by deleting
     * the associated directory recursively.
     */
    public boolean deleteWorldDirectory(String p_75802_1_)
    {
        File file1 = new File(this.savesDirectory, p_75802_1_);

        if (!file1.exists())
        {
            return true;
        }
        else
        {
            logger.info("Deleting level " + p_75802_1_);

            for (int i = 1; i <= 5; ++i)
            {
                logger.info("Attempt " + i + "...");

                if (deleteFiles(file1.listFiles()))
                {
                    break;
                }

                logger.warn("Unsuccessful in deleting contents.");

                if (i < 5)
                {
                    try
                    {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException var5)
                    {
                        ;
                    }
                }
            }

            return file1.delete();
        }
    }

    /**
     * @args: Takes one argument - the list of files and directories to delete. @desc: Deletes the files and directory
     * listed in the list recursively.
     */
    protected static boolean deleteFiles(File[] files)
    {
        for (int i = 0; i < files.length; ++i)
        {
            File file1 = files[i];
            logger.debug("Deleting " + file1);

            if (file1.isDirectory() && !deleteFiles(file1.listFiles()))
            {
                logger.warn("Couldn\'t delete directory " + file1);
                return false;
            }

            if (!file1.delete())
            {
                logger.warn("Couldn\'t delete file " + file1);
                return false;
            }
        }

        return true;
    }

    /**
     * Returns back a loader for the specified save directory
     */
    public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata)
    {
        return new SaveHandler(this.savesDirectory, saveName, storePlayerdata);
    }

    public boolean func_154334_a(String saveName)
    {
        return false;
    }

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    public boolean isOldMapFormat(String saveName)
    {
        return false;
    }

    /**
     * converts the map to mcRegion
     */
    public boolean convertMapFormat(String filename, IProgressUpdate progressCallback)
    {
        return false;
    }

    /**
     * Return whether the given world can be loaded.
     */
    public boolean canLoadWorld(String p_90033_1_)
    {
        File file1 = new File(this.savesDirectory, p_90033_1_);
        return file1.isDirectory();
    }
}
