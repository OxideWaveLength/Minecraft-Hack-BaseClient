package me.wavelength.baseclient.overlay;

import java.awt.Color;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.font.NahrFont;
import me.wavelength.baseclient.font.NahrFont.FontType;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;

public class ToggledModules1 extends EventListener {

	public ToggledModules1() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		List<Module> modules = BaseClient.instance.getModuleManager().getToggledModules();

		modules.sort((o1, o2) -> Strings.getStringWidthCFR(o2.getName()) - Strings.getStringWidthCFR(o1.getName()));
		int y = 1;

		int offset = 15;

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

			String s = Strings.translateColors(module.getName() + (module.getAntiCheat().equals(AntiCheat.VANILLA) ? "" : " &8-&f " + Strings.capitalizeOnlyFirstLetter(module.getAntiCheat().name())));
			int mWidth = Strings.getStringWidthCFR(s);

			/** Draws The Black Background */
			RenderUtils.drawRect(event.getWidth() - mWidth - 6, y - 1, event.getWidth(), y + offset, new Color(0, 0, 0, 100).getRGB());
			
			RenderUtils.drawRect(event.getWidth() - mWidth - 6, y - 1, event.getWidth() - mWidth - 4, y + offset, module.getColor().getRGB());

			RenderUtils.drawString(s, event.getWidth() - mWidth - 3, y + 3, FontType.OUTLINE_THIN, module.getColor().getRGB(), new Color(0, 0, 0, 255).getRGB());
			y += offset + 1;
		}
	}

	public static int rainbow(int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 15.0);
		rainbowState %= 360;
		return Color.getHSBColor((float) (rainbowState / 360.0f), 0.7f, 0.8f).getRGB();
	}

}