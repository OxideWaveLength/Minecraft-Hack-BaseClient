package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.Event;

public class Render3DEvent extends Event {

	private float partialTicks;

	public Render3DEvent(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

}