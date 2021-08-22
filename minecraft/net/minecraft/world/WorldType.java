package net.minecraft.world;

public class WorldType {
	/** List of world types. */
	public static final WorldType[] worldTypes = new WorldType[16];

	/** Default world type. */
	public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();

	/** Flat world type. */
	public static final WorldType FLAT = new WorldType(1, "flat");

	/** Large Biome world Type. */
	public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");

	/** amplified world type */
	public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).setNotificationData();
	public static final WorldType CUSTOMIZED = new WorldType(4, "customized");
	public static final WorldType DEBUG_WORLD = new WorldType(5, "debug_all_block_states");

	/** Default (1.1) world type. */
	public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);

	/** ID for this world type. */
	private final int worldTypeId;
	private final String worldType;

	/** The int version of the ChunkProvider that generated this world. */
	private final int generatorVersion;

	/**
	 * Whether this world type can be generated. Normally true; set to false for
	 * out-of-date generator versions.
	 */
	private boolean canBeCreated;

	/** Whether this WorldType has a version or not. */
	private boolean isWorldTypeVersioned;
	private boolean hasNotificationData;

	private WorldType(int id, String name) {
		this(id, name, 0);
	}

	private WorldType(int id, String name, int version) {
		this.worldType = name;
		this.generatorVersion = version;
		this.canBeCreated = true;
		this.worldTypeId = id;
		worldTypes[id] = this;
	}

	public String getWorldTypeName() {
		return this.worldType;
	}

	/**
	 * Gets the translation key for the name of this world type.
	 */
	public String getTranslateName() {
		return "generator." + this.worldType;
	}

	public String func_151359_c() {
		return this.getTranslateName() + ".info";
	}

	/**
	 * Returns generatorVersion.
	 */
	public int getGeneratorVersion() {
		return this.generatorVersion;
	}

	public WorldType getWorldTypeForGeneratorVersion(int version) {
		return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
	}

	/**
	 * Sets canBeCreated to the provided value, and returns this.
	 */
	private WorldType setCanBeCreated(boolean enable) {
		this.canBeCreated = enable;
		return this;
	}

	/**
	 * Gets whether this WorldType can be used to generate a new world.
	 */
	public boolean getCanBeCreated() {
		return this.canBeCreated;
	}

	/**
	 * Flags this world type as having an associated version.
	 */
	private WorldType setVersioned() {
		this.isWorldTypeVersioned = true;
		return this;
	}

	/**
	 * Returns true if this world Type has a version associated with it.
	 */
	public boolean isVersioned() {
		return this.isWorldTypeVersioned;
	}

	public static WorldType parseWorldType(String type) {
		for (int i = 0; i < worldTypes.length; ++i) {
			if (worldTypes[i] != null && worldTypes[i].worldType.equalsIgnoreCase(type)) {
				return worldTypes[i];
			}
		}

		return null;
	}

	public int getWorldTypeID() {
		return this.worldTypeId;
	}

	/**
	 * returns true if selecting this worldtype from the customize menu should
	 * display the generator.[worldtype].info message
	 */
	public boolean showWorldInfoNotice() {
		return this.hasNotificationData;
	}

	/**
	 * enables the display of generator.[worldtype].info message on the customize
	 * world menu
	 */
	private WorldType setNotificationData() {
		this.hasNotificationData = true;
		return this;
	}
}
