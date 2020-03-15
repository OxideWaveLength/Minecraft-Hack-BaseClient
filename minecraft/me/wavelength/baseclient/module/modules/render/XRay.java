package me.wavelength.baseclient.module.modules.render;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.event.events.BlockBrightnessRequestEvent;
import me.wavelength.baseclient.event.events.BlockSideRenderEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class XRay extends Module {

	public XRay() {
		super("XRay", "See only specific blocks", Keyboard.KEY_X, Category.RENDER);
	}

	private List<String> exceptions;

	@Override
	public void setup() {
		moduleSettings.addDefault("blocks", Arrays.asList(Blocks.iron_ore.getLocalizedName().toUpperCase().replace(" ", "_")));

		Function<String, String> consumer = line -> line = line.toUpperCase().replace(" ", "_"); // Replaces every line with an uppercase version that has spaces replaced with underscores
		this.exceptions = moduleSettings.getStringList("blocks", consumer);
	}

	@Override
	public void onEnable() {
		mc.renderGlobal.loadRenderers();
	}

	@Override
	public void onDisable() {
		mc.renderGlobal.loadRenderers();
	}

	@Override
	public void onBlockBrightnessRequest(BlockBrightnessRequestEvent event) {
		if (isInExceptions(event.getBlock()))
			event.setBlockBrightness(15);
	}

	@Override
	public void onBlockSideRender(BlockSideRenderEvent event) {
		event.setCancelled(true);

		if (!(isInExceptions(event.getBlock()))) {
			event.setRender(false);
		}
	}

	private boolean isInExceptions(Block block) {
		if (exceptions == null)
			return false;

		return exceptions.contains(block.getLocalizedName().replace(" ", "_").toUpperCase()) || exceptions.contains(Integer.toString(Block.getIdFromBlock(block)));
	}

}