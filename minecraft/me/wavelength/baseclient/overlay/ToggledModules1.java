package me.wavelength.baseclient.overlay;

import java.awt.Color;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.modules.client.ArrayList;
import me.wavelength.baseclient.utils.Colors;
import me.wavelength.baseclient.utils.Integers;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;

public class ToggledModules1 extends EventListener {

	public ToggledModules1() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		Module arrayList = BaseClient.instance.getModuleManager().getModule(ArrayList.class);

		if (arrayList.isToggled()) {
			List<Module> modules = BaseClient.instance.getModuleManager().getToggledModules();

			modules.sort((module1,
					module2) -> Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module2.getNameWithAntiCheat()))
							- Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module1.getNameWithAntiCheat())));
			int y = 1;

			int relativeYOffset = 3;
			int relativeXOffset = -2;

			int offset = BaseClient.instance.getFontRenderer().getFontSize() + relativeYOffset - 15;

			for (int i = 0; i < modules.size(); i++) {
				Module module = modules.get(i);
				String s = Strings.capitalizeFirstLetter(module.getNameWithAntiCheat());
				int mWidth = Strings.getStringWidthCFR(s);

				int tabWidth = arrayList.getModuleSettings().getInt("tab size");

				int moduleColor = arrayList.getModuleSettings().getBoolean("rainbow")
						? Colors.getRGBWave(arrayList.getModuleSettings().getInt("speed"), 1, 0.7f,
								Math.round(((i * y) * arrayList.getModuleSettings().getInt("offset"))))
						: module.getColor().getRGB();
				int tabColor = arrayList.getModuleSettings().getBoolean("rainbow")
						? arrayList.getModuleSettings().getBoolean("match module color") ? moduleColor
								: Colors.getRGBWave(arrayList.getModuleSettings().getInt("speed"), 1, 0.7f,
										Math.round(Integers
												.flipPositive(((i * y) * arrayList.getModuleSettings().getInt("offset"))
														- ((event.getWidth() - (mWidth + tabWidth)) * offset))))
						: module.getColor().getRGB();
				boolean showGradient = arrayList.getModuleSettings().getBoolean("gradient");
				int opacity = arrayList.getModuleSettings().getInt("opacity") > 255 ? 255
						: arrayList.getModuleSettings().getInt("opacity");

				if (module.getCategory().equals(Category.HIDDEN) || !(module.isShownInModuleArrayList()))
					continue;

				RenderUtils.drawGradientRect(event.getWidth() - mWidth + relativeXOffset - 5, y + 1, event.getWidth(),
						y + offset - 1, new Color(0, 0, 0, opacity).getRGB(),
						showGradient ? new Color(100, 100, 100, opacity > 255 ? 255 : opacity).getRGB()
								: new Color(0, 0, 0, opacity > 255 ? 255 : opacity).getRGB());
				RenderUtils.drawRect(event.getWidth() - mWidth + relativeXOffset - 5, y + 1,
						event.getWidth() - mWidth - tabWidth, y + offset - 1, tabColor);
				RenderUtils.drawString(s, event.getWidth() - mWidth + relativeXOffset, y + 1, moduleColor,
						BaseClient.instance.getFontRenderer().fontSizeNormal, true);
				y += offset - 2;
			}
		}
	}

}