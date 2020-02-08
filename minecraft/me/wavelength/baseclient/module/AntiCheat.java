package me.wavelength.baseclient.module;

public enum AntiCheat {

	VANILLA(false), NCP(true), SPARTAN(false), VERUS(false), AAC(true), HYPIXEL(false);

	private boolean capital;

	AntiCheat(boolean capital) {
		this.capital = capital;
	}

	public boolean isCapital() {
		return capital;
	}

}