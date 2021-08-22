package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class PlayerSpawnEvent extends CancellableEvent {

	private EntityOtherPlayerMP entity;

	public PlayerSpawnEvent(EntityOtherPlayerMP entity) {
		this.entity = entity;
	}

	public EntityOtherPlayerMP getEntity() {
		return entity;
	}

	public void setEntity(EntityOtherPlayerMP entity) {
		this.entity = entity;
	}

}