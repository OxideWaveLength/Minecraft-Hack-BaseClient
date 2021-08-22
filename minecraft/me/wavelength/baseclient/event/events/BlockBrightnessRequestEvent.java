package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.Event;
import net.minecraft.block.Block;

public class BlockBrightnessRequestEvent extends Event {

	private Block block;

	private int blockBrightness;

	/**
	 * @formatter:off
	 * This event is fired by the Block class: {@link net.minecraft.block.Block#getMixedBrightnessForBlock}
	 * 
	 * The block's brightness is defaulted to -1
	 * 
	 * @formatter:on
	 */
	public BlockBrightnessRequestEvent(Block block) {
		this.block = block;

		this.blockBrightness = -1;
	}

	public Block getBlock() {
		return block;
	}

	public int getBlockBrightness() {
		return blockBrightness;
	}

	/**
	 * @param blockBrightness the new block's brightness (blocks brightness reference: https://minecraft.gamepedia.com/Light)
	 * If the blockBrightness is more than 15 the light levels won't be used and the block's brightness is gonna be set to the number itself
	 */
	public void setBlockBrightness(int blockBrightness) {
		this.blockBrightness = blockBrightness;
	}

}