package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;

public class LadderClimbEvent extends CancellableEvent {

	private double motionY;

	public LadderClimbEvent(double motionY) {
		this.motionY = motionY;
	}

	public double getMotionY() {
		return motionY;
	}

	public void setMotionY(double motionY) {
		this.motionY = motionY;
	}

}