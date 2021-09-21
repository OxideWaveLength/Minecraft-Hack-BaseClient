package me.wavelength.baseclient.utils.animation.logic.base;

public enum LogicalDirection {
	FORWARDS(new int[] { 0, 0 }), BACKWARDS(new int[] { 0, 0 }), UP(new int[] { 0, -1 }), DOWN(new int[] { 0, 1 }),
	LEFT(new int[] { -1, 0 }), RIGHT(new int[] { 1, 0 });

	private final int[] xy;

	LogicalDirection(int[] xy) {
		this.xy = xy;
	}

	public int[] getXy() {
		return xy;
	}

}
