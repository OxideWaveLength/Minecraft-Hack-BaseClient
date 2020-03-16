package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class FluidRenderEvent extends CancellableEvent {

	private BlockPos blockPos;
	private IBlockState state;

	public FluidRenderEvent(BlockPos blockPos, IBlockState state) {
		this.blockPos = blockPos;
		this.state = state;
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

}