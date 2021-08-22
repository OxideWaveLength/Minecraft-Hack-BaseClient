package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.Event;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.IChatComponent;

public class ServerLeaveEvent extends Event {

	private final ServerData serverData;
	private final String reason;
	private final IChatComponent message;

	public ServerLeaveEvent(final ServerData serverData, final String reason, final IChatComponent message) {
		this.serverData = serverData;
		this.reason = reason;
		this.message = message;
	}

	public ServerData getServerData() {
		return serverData;
	}

	public String getReason() {
		return reason;
	}

	public IChatComponent getMessage() {
		return message;
	}

}