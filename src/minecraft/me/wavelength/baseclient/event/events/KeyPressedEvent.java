package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;

public class KeyPressedEvent extends CancellableEvent {

	private int key;

	public KeyPressedEvent(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

}