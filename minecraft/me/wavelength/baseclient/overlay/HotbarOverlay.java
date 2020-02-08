package me.wavelength.baseclient.overlay;

import java.awt.Color;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.font.NahrFont.FontType;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Time;

public class HotbarOverlay extends EventListener {

	public HotbarOverlay() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	/** The rest of the code is in GuiIngame#renderTooltip() */
	@Override
	public void onRender2D(Render2DEvent event) {
		if (BaseClient.instance.isDefaultHotbar())
			return;

		RenderUtils.drawModalRectFromRight(5, 0, 5, 21, Color.RED.getRGB());

		RenderUtils.drawStringFromBottomRight(Time.getTime(System.currentTimeMillis(), "HH:mm:ss"), 9, 7, FontType.SHADOW_THIN, Color.YELLOW.getRGB());

		if (mc.currentScreen != null)
			return;

		renderText(event, "&7[&dFPS&7]&5 " + mc.getDebugFPS(), 2);
	}

	private void renderText(Render2DEvent event, String text, int x) {
		String fpsText = text;
		RenderUtils.drawString(fpsText, x, event.getHeight() - 15, FontType.SHADOW_THIN, -1);
	}

}