package net.minecraft.world.storage;

import net.minecraft.world.WorldSettings;

public class SaveFormatComparator implements Comparable<SaveFormatComparator> {
	/** the file name of this save */
	private final String fileName;

	/** the displayed name of this save file */
	private final String displayName;
	private final long lastTimePlayed;
	private final long sizeOnDisk;
	private final boolean requiresConversion;

	/** Instance of EnumGameType. */
	private final WorldSettings.GameType theEnumGameType;
	private final boolean hardcore;
	private final boolean cheatsEnabled;

	public SaveFormatComparator(String fileNameIn, String displayNameIn, long lastTimePlayedIn, long sizeOnDiskIn, WorldSettings.GameType theEnumGameTypeIn, boolean requiresConversionIn, boolean hardcoreIn, boolean cheatsEnabledIn) {
		this.fileName = fileNameIn;
		this.displayName = displayNameIn;
		this.lastTimePlayed = lastTimePlayedIn;
		this.sizeOnDisk = sizeOnDiskIn;
		this.theEnumGameType = theEnumGameTypeIn;
		this.requiresConversion = requiresConversionIn;
		this.hardcore = hardcoreIn;
		this.cheatsEnabled = cheatsEnabledIn;
	}

	/**
	 * return the file name
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * return the display name of the save
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	public long getSizeOnDisk() {
		return this.sizeOnDisk;
	}

	public boolean requiresConversion() {
		return this.requiresConversion;
	}

	public long getLastTimePlayed() {
		return this.lastTimePlayed;
	}

	public int compareTo(SaveFormatComparator p_compareTo_1_) {
		return this.lastTimePlayed < p_compareTo_1_.lastTimePlayed ? 1 : (this.lastTimePlayed > p_compareTo_1_.lastTimePlayed ? -1 : this.fileName.compareTo(p_compareTo_1_.fileName));
	}

	/**
	 * Gets the EnumGameType.
	 */
	public WorldSettings.GameType getEnumGameType() {
		return this.theEnumGameType;
	}

	public boolean isHardcoreModeEnabled() {
		return this.hardcore;
	}

	/**
	 * @return {@code true} if cheats are enabled for this world
	 */
	public boolean getCheatsEnabled() {
		return this.cheatsEnabled;
	}
}
