package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class FluidRenderEvent extends CancellableEvent {

	private BlockPos blockPos;
	private IBlockState state;

	private boolean forceDraw;

	/**
	 * @formatter:off
	 * This event is fired by the BlockRendererDispatcher class: {@link net.minecraft.client.renderer.BlockRendererDispatcher#renderBlock}
	 * @formatter:on
	 * @param block
	 */
	public FluidRenderEvent(BlockPos blockPos, IBlockState state) {
		this.blockPos = blockPos;
		this.state = state;

		this.forceDraw = false;
	}

	public BlockPos getBlockPos() {
		return blockPos;
	}

	public IBlockState getState() {
		return state;
	}

	public Block getBlock() {
		return (state.getBlock() == null ? null : state.getBlock());
	}

	public void setForceDraw(boolean forceDraw) {
		this.forceDraw = forceDraw;
	}

	public boolean shouldForceDraw() {
		return forceDraw;
	}

}