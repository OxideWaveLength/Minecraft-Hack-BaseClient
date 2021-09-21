package me.wavelength.baseclient.utils.animation.logic;

import me.wavelength.baseclient.utils.animation.logic.base.LogicalAnimation;
import me.wavelength.baseclient.utils.animation.logic.base.LogicalDirection;

public class SmootherStepAnimation extends LogicalAnimation {

	private double x;

	public SmootherStepAnimation(int ms, double maxOutput, Enum<LogicalDirection> direction) {
		super(ms, maxOutput, direction);
		// TODO Auto-generated constructor stub
	}

	public SmootherStepAnimation(int ms, double maxOutput) {
		super(ms, maxOutput);
	}

	@Override
	protected double getEquation(double x) {
		this.x = x / duration; // Used to force input to range from 0 - 1
		return 6 * Math.pow(this.x, 5) - (15 * Math.pow(this.x, 4)) + (10 * Math.pow(this.x, 3));
	}

}
