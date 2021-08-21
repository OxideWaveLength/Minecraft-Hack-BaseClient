package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.Event;

public class PreMotionEvent extends Event {

	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private boolean ground;

	public PreMotionEvent(double x, double y, double z, float yaw, float pitch, boolean ground) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.ground = ground;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public boolean onGround() {
		return ground;
	}

	public void setGround(boolean ground) {
		this.ground = ground;
	}

}