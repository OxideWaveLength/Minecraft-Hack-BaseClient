package me.wavelength.baseclient.utils.animation.logic;

import me.wavelength.baseclient.utils.animation.logic.base.LogicalAnimation;
import me.wavelength.baseclient.utils.animation.logic.base.LogicalDirection;

public class DecelerateAnimation extends LogicalAnimation {

	public DecelerateAnimation(int ms, double endPoint) {
		super(ms, endPoint);
	}

	public DecelerateAnimation(int ms, double endPoint, LogicalDirection direction) {
		super(ms, endPoint, direction);
	}

	protected double getEquation(double x) {
		double x1 = x / duration;
		return 1 - ((x1 - 1) * (x1 - 1));
	}

}
