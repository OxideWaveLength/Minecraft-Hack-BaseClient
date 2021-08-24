package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;

public class ServerConnectingEvent extends CancellableEvent {

	private final String address;
	private final int port;

	private String cancelReason;

	public ServerConnectingEvent(final String address, final int port) {
		this.address = address;
		this.port = port;
		this.cancelReason = "Connection cancelled.";
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

}