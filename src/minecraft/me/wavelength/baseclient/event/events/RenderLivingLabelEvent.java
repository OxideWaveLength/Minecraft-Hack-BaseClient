package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class RenderLivingLabelEvent extends CancellableEvent {

	private Entity entity;
	private String label;

	/**
	 * This event is fired by the Render class: {@link net.minecraft.client.renderer.entity.Render#renderLivingLabel}
	 */
	public RenderLivingLabelEvent(Entity entity, String label) {
		this.entity = entity;
		this.label = label;
	}

	public Entity getEntity() {
		return entity;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}