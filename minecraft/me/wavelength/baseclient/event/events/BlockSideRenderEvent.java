package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.block.Block;

public class BlockSideRenderEvent extends CancellableEvent {

	private Block block;

	private boolean render;

	/**
	 * @formatter:off
	 * This event is fired by the Block class: {@link net.minecraft.block.Block#shouldSideBeRendered}
	 * 
	 * This event when cancelled does NOT prevent the block from being rendered, that only means that an action has been taken
	 * To prevent the block from being rendered cancel the event and then {@link #setRender(boolean)} to false
	 * 
	 * @formatter:on
	 * @param block
	 */
	public BlockSideRenderEvent(Block block) {
		this.block = block;
		this.render = true;
	}

	public Block getBlock() {
		return block;
	}

	public boolean shouldRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

}