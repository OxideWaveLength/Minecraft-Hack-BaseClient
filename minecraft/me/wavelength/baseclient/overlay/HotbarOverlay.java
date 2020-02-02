package me.wavelength.baseclient.overlay;

import java.awt.Color;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.font.NahrFont.FontType;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;

public class HotbarOverlay extends EventListener {

	public HotbarOverlay() {
		BaseClient.instance.getEventManager().registerListener(this);
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		if (mc.currentScreen != null)
			return;

		RenderUtils.drawRect(event.getWidth(), event.getHeight(), -event.getWidth(), event.getHeight() - 23, new Color(0, 0, 0, 180).getRGB());

		renderText(event, "&7[&dFPS&7]&5 " + mc.getDebugFPS(), 5);
	}

	private void renderText(Render2DEvent event, String text, int x) {
		String fpsText = text;
		RenderUtils.drawString(fpsText, x, event.getHeight() - 18, FontType.NORMAL, -1, -16777216);
	}

}