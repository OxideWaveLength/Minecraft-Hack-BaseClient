package me.wavelength.baseclient.font;

import java.awt.Font;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class UnicodeFontRenderer extends FontRenderer {

	private final UnicodeFont font;

	@SuppressWarnings("unchecked")
	public UnicodeFontRenderer(Font awtFont) {
		super((Minecraft.getMinecraft()).gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
		this.font = new UnicodeFont(awtFont);
		this.font.addAsciiGlyphs();
		this.font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		try {
			this.font.loadGlyphs();
		} catch (SlickException exception) {
			throw new RuntimeException(exception);
		}
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
		this.FONT_HEIGHT = this.font.getHeight(alphabet) / 2;
	}

	@SuppressWarnings("unchecked")
	public UnicodeFontRenderer(InputStream streamFont, int size) {
		super((Minecraft.getMinecraft()).gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
		this.font = new UnicodeFont(streamFont, (int) size);
		this.font.addAsciiGlyphs();
		this.font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		try {
			this.font.loadGlyphs();
		} catch (SlickException exception) {
			throw new RuntimeException(exception);
		}
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
		this.FONT_HEIGHT = this.font.getHeight(alphabet) / 2;
	}

	public int drawString(String string, int x, int y, int color) {
		return drawString(string, x, y, color, false);
	}

	public int drawString(String string, int x, int y, int color, boolean hasShadow) {
		if (string == null)
			return -1;
		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		boolean blend = GL11.glIsEnabled(3042);
		boolean lighting = GL11.glIsEnabled(2896);
		boolean texture = GL11.glIsEnabled(3553);
		if (!blend)
			GL11.glEnable(3042);
		if (lighting)
			GL11.glDisable(2896);
		if (texture)
			GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		x *= 2;
		y *= 2;

		this.font.drawString((float) x, (float) y, string, new Color(color), hasShadow);
		if (texture)
			GL11.glEnable(3553);
		if (lighting)
			GL11.glEnable(2896);
		if (!blend)
			GL11.glDisable(3042);
		GL11.glPopMatrix();
		return getStringWidth(string);
	}

	public void drawStringWithShadow(String string, int x, int y, int color) {
		drawString(StringUtils.stripControlCodes(string), x + 1, y + 1, -16777216); // Shadow
		drawString(string, x, y, color, true);
	}

	public int getStringWidth(String string) {
		return this.font.getWidth(string) / 2;
	}

	public int getStringHeight(String string) {
		return this.font.getHeight(string) / 2;
	}

	public UnicodeFont getFont() {
		return this.font;
	}

}