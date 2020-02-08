package net.minecraft.world;

public class WorldProviderSurface extends WorldProvider {
	/**
	 * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
	 */
	public String getDimensionName() {
		return "Overworld";
	}

	public String getInternalNameSuffix() {
		return "";
	}
}
