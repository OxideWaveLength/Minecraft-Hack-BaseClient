package me.wavelength.baseclient.utils.animation.logic;

import me.wavelength.baseclient.utils.animation.logic.base.LogicalAnimation;
import me.wavelength.baseclient.utils.animation.logic.base.LogicalDirection;

public class SmoothStepAnimation extends LogicalAnimation {

	public SmoothStepAnimation(int ms, double endPoint) {
		super(ms, endPoint);
	}

	public SmoothStepAnimation(int ms, double endPoint, Enum<LogicalDirection> direction) {
		super(ms, endPoint, direction);
	}

	protected double getEquation(double x) {
		double x1 = x / (double) duration; // Used to force input to range from 0 - 1
		return -2 * Math.pow(x1, 3) + (3 * Math.pow(x1, 2));
	}

}
