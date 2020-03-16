package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.block.Block;

public class BlockRenderEvent extends CancellableEvent {

	private Block block;

	/**
	 * @formatter:off
	 * This event is fired by the Block class: {@link net.minecraft.block.Block#getRenderType}
	 * @formatter:on
	 * @param block
	 */
	public BlockRenderEvent(Block block) {
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}
}