package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class CollideEvent extends CancellableEvent {

	private Entity entity;

	private AxisAlignedBB axisAlignedBB;
	private Block block;

	/**
	 * This event is fired by the Block class: {@link net.minecraft.block.Block#addCollisionBoxesToList}
	 */
	public CollideEvent(Entity entity, AxisAlignedBB axisAlignedBB, Block block) {
		this.entity = entity;
		this.axisAlignedBB = axisAlignedBB;
		this.block = block;
	}

	public Entity getEntity() {
		return entity;
	}

	public AxisAlignedBB getAxisAlignedBB() {
		return axisAlignedBB;
	}

	public Block getBlock() {
		return block;
	}

}