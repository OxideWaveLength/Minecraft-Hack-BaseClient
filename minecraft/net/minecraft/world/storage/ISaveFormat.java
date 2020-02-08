package net.minecraft.world.storage;

import java.util.List;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat {
	/**
	 * Returns the name of the save format.
	 */
	String getName();

	/**
	 * Returns back a loader for the specified save directory
	 */
	ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata);

	List<SaveFormatComparator> getSaveList() throws AnvilConverterException;

	void flushCache();

	/**
	 * Returns the world's WorldInfo object
	 */
	WorldInfo getWorldInfo(String saveName);

	boolean func_154335_d(String p_154335_1_);

	/**
	 * @args: Takes one argument - the name of the directory of the world to
	 *        delete. @desc: Delete the world by deleting the associated directory
	 *        recursively.
	 */
	boolean deleteWorldDirectory(String p_75802_1_);

	/**
	 * Renames the world by storing the new name in level.dat. It does *not* rename
	 * the directory containing the world data.
	 */
	void renameWorld(String dirName, String newName);

	boolean func_154334_a(String saveName);

	/**
	 * gets if the map is old chunk saving (true) or McRegion (false)
	 */
	boolean isOldMapFormat(String saveName);

	/**
	 * converts the map to mcRegion
	 */
	boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

	/**
	 * Return whether the given world can be loaded.
	 */
	boolean canLoadWorld(String p_90033_1_);
}
