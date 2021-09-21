package me.wavelength.baseclient.utils.animation.logic;

import me.wavelength.baseclient.utils.animation.logic.base.LogicalAnimation;
import me.wavelength.baseclient.utils.animation.logic.base.LogicalDirection;

public class LinearAnimation extends LogicalAnimation {

	public LinearAnimation(int ms, double endPoint, Enum<LogicalDirection> direction) {
		super(ms, endPoint, direction);
	}

	public LinearAnimation(int ms, double endPoint) {
		super(ms, endPoint);
	}

	protected double getEquation(double x) {
		return x / duration; // TODO Entirely broken even though it's the easiest animation to make. How? I
								// have no idea
	}

}
