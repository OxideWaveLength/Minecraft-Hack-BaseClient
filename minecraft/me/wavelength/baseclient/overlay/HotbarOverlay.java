package me.wavelength.baseclient.overlay;

import java.awt.Color;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.gui.clickgui.ClickGui;
import me.wavelength.baseclient.gui.clickgui.GuiBind;
import me.wavelength.baseclient.utils.Colors;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class HotbarOverlay extends EventListener {

	public HotbarOverlay() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	/** The rest of the code is in GuiIngame#renderTooltip() */
	@Override
	public void onRender2D(Render2DEvent event) {
		me.wavelength.baseclient.module.Module mod = BaseClient.instance.getModuleManager()
				.getModule(me.wavelength.baseclient.module.modules.client.HotbarOverlay.class);

		if (mod.isToggled()) {
			if (BaseClient.instance.isDefaultHotbar())
				return;

			RenderUtils.drawModalRectFromRight(5, 0, 5, 21,
					Colors.getRGBWave(10, 1, 0.7f, System.currentTimeMillis() / 1000));
			if (mod.getModuleSettings().getBoolean("time")) {
				RenderUtils.drawStringFromBottomRight(Time.getTime(System.currentTimeMillis(), "h:mm"), 9, 10,
						mod.getModuleSettings().getBoolean("rainbow")
								? Colors.getRGBWave(10, 1, 0.7f, System.currentTimeMillis() / 5000)
								: Color.YELLOW.getRGB());
			}

			GuiScreen currentScreen = mc.currentScreen;

			if (currentScreen != null && !(currentScreen instanceof ClickGui) && !(currentScreen instanceof GuiBind))
				return;

			if (mod.getModuleSettings().getBoolean("fps")) {
				RenderUtils.drawStringFromBottomLeft("[FPS] " + Minecraft.getDebugFPS(), 2, 10,
						mod.getModuleSettings().getBoolean("rainbow")
								? Colors.getRGBWave(10, 1, 0.7f, System.currentTimeMillis() / 5000)
								: Color.magenta.getRGB());
			}
		}
	}

}