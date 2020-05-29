package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.block.Block;

public class BlockRenderEvent extends CancellableEvent {

	private Block block;

	private boolean forceDraw;

	/**
	 * @formatter:off
	 * This event is fired by the BlockRendererDispatcher class: {@link net.minecraft.client.renderer.BlockRendererDispatcher#renderBlock}
	 * @formatter:on
	 * @param block
	 */
	public BlockRenderEvent(Block block) {
		this.block = block;
		this.forceDraw = false;
	}

	public Block getBlock() {
		return block;
	}

	public void setForceDraw(boolean forceDraw) {
		this.forceDraw = forceDraw;
	}

	public boolean shouldForceDraw() {
		return forceDraw;
	}

}