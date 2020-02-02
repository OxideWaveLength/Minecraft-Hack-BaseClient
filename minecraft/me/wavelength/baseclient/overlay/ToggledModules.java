package me.wavelength.baseclient.overlay;

import java.awt.Color;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ToggledModules extends EventListener {

	public ToggledModules() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		List<Module> modules = BaseClient.instance.getModuleManager().getToggledModules();

		modules.sort((o1, o2) -> fr.getStringWidth(o2.getName()) - fr.getStringWidth(o1.getName()));
		int y = 1;

		final int[] counter = { 1 };

		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			if (module.getCategory() == Category.HIDDEN)
				continue;

			Module nextModule = null;
			Module previousModule = null;

			if (i < modules.size() - 2)
				nextModule = modules.get(i + 1);

			if ((i) > 0)
				previousModule = modules.get(i - 1);

			String s = Strings.translateColors(module.getName() + (module.getAntiCheat() == AntiCheat.VANILLA ? "" : Strings.translateColors(" &7") + module.getAntiCheat().name()));
			int mWidth = fr.getStringWidth(s);

			int nextModuleWidth = (nextModule == null ? 0 : fr.getStringWidth((nextModule.getName() + (nextModule.getAntiCheat() == AntiCheat.VANILLA ? "" : Strings.translateColors(" &7") + nextModule.getAntiCheat().name()))) + 5);
			int previousModuleWidth = (previousModule == null ? 0 : fr.getStringWidth((previousModule.getName() + (previousModule.getAntiCheat() == AntiCheat.VANILLA ? "" : Strings.translateColors(" &7") + previousModule.getAntiCheat().name()))) + 5);

//			RenderHelper.drawRect(e.getWidth() - 1, y - 2, e.getWidth(), y + 10, rainbowColor);

//			RenderHelper.drawRect(e.getWidth() - mWidth - 6, y - 1, e.getWidth(), y - 2, rainbowColor);

//			System.out.println(i < modules.size()-2);

			/** Draws The Black Background */
			RenderUtils.drawRect(event.getWidth() - mWidth - 6, y - 1, event.getWidth(), y + 10, new Color(0, 0, 0, 100).getRGB());

			int rainbowColor = rainbow(counter[0] * 200);

			/** Draws The Rainbow Borders On This Current Module */
			RenderUtils.drawRect(event.getWidth() - mWidth - (mWidth < nextModuleWidth ? 5 : 6), y + 10, event.getWidth() - (module != null ? nextModuleWidth : 0), y + 11, rainbowColor);
			RenderUtils.drawRect(event.getWidth() - mWidth - 6, y - (mWidth < nextModuleWidth ? 1 : 2), event.getWidth() - mWidth - 5, y + 10, rainbowColor);

//			/**
//			 * If the previous module is NOT null, draw the line between the previous one
//			 * and this one
//			 */
//			if (previousModule != null)
//				RenderHelper.drawRect(e.getWidth() - mWidth - 5, y - 2, e.getWidth(), y - 1, new Color(0, 0, 0, 100).getRGB());

			/**
			 * If the next module is NOT null, draw the line between the next one and this
			 * one
			 */
			if (nextModule != null)
				RenderUtils.drawRect(event.getWidth() - nextModuleWidth, y + 11, event.getWidth(), y + 10, new Color(0, 0, 0, 100).getRGB());

			fr.drawStringWithShadow(s, event.getWidth() - mWidth - 3, y + 1, new Color(255, 255, 255, 0).getRGB());
			y += 9 + 3;
			counter[0]++;
		}
	}

	public static int rainbow(int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 15.0);
		rainbowState %= 360;
		return Color.getHSBColor((float) (rainbowState / 360.0f), 0.7f, 0.8f).getRGB();
	}

}