package me.wavelength.baseclient.utils;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.font.Font;
import me.wavelength.baseclient.font.UnicodeFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public class RenderUtils {

	private static Font getFontRenderer() {
		return BaseClient.instance.getFontRenderer();
	}

	public static void renderItem(int xPos, int yPos, ItemStack itemStack) {
		GuiIngame guiInGame = Minecraft.getMinecraft().ingameGUI;

		if (guiInGame == null)
			return;

		if (itemStack == null)
			return;

		RenderItem itemRenderer = guiInGame.itemRenderer;

		Minecraft.getMinecraft().getTextureManager().bindTexture(guiInGame.getWidgetsTexPath());

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();

		RenderHelper.enableGUIStandardItemLighting();
		itemRenderer.renderItemAndEffectIntoGUI(itemStack, xPos, yPos);

		itemRenderer.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, itemStack, xPos, yPos, "");
		RenderHelper.disableStandardItemLighting();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	public static void drawString(String text, int x, int y, int color, boolean shadow, UnicodeFontRenderer font) {
		if (color == -1)
			color = Color.WHITE.getRGB();

		text = Strings.simpleTranslateColors(text);
		if (shadow)
			font.drawStringWithShadow(text, x, y, color);
		else
			font.drawString(text, x, y, color);
	}

	public static void drawString(String text, int x, int y, int color, int fontSize, boolean shadow) {
		drawString(text, x, y, color, shadow, getFontRenderer().getFont(fontSize));
	}

	public static void drawString(String text, int x, int y, int color, boolean shadow) {
		drawString(text, x, y, color, getFontRenderer().getFontSize(), shadow);
	}

	public static void drawString(String text, int x, int y, int color, int fontSize) {
		drawString(text, x, y, color, fontSize, true);
	}

	public static void drawString(String text, int x, int y, int color) {
		if (color == -1)
			color = Color.WHITE.getRGB();
		drawString(text, x, y, color, true);
	}

	public static void drawStringFromTopRight(String text, int x, int y, int color, int fontSize, boolean shadow) {
		drawString(text, getScaledResolution().getScaledWidth() - Strings.getStringWidthCFR(text) - x, y, color, shadow);
	}

	public static void drawStringFromTopRight(String text, int x, int y, int color, boolean shadow) {
		drawStringFromTopRight(text, x, y, color, getFontRenderer().getFontSize(), shadow);
	}

	public static void drawStringFromTopRight(String text, int x, int y, int color, int fontSize) {
		drawStringFromTopRight(text, x, y, color, fontSize, true);
	}

	public static void drawStringFromTopRight(String text, int x, int y, int color) {
		drawStringFromTopRight(text, x, y, color, getFontRenderer().getFontSize(), true);
	}

	public static void drawStringFromBottomRight(String text, int x, int y, int color, int fontSize, boolean shadow) {
		drawStringFromTopRight(text, x, getScaledResolution().getScaledHeight() - y * 2, color, fontSize, shadow);
	}

	public static void drawStringFromBottomRight(String text, int x, int y, int color, boolean shadow) {
		drawStringFromBottomRight(text, x, y, color, getFontRenderer().getFontSize(), true);
	}

	public static void drawStringFromBottomRight(String text, int x, int y, int color, int fontSize) {
		drawStringFromBottomRight(text, x, y, color, fontSize, true);
	}

	public static void drawStringFromBottomRight(String text, int x, int y, int color) {
		drawStringFromBottomRight(text, x, y, color, getFontRenderer().getFontSize());
	}

	public static void drawStringFromBottomLeft(String text, int x, int y, int color) {
		drawStringFromBottomLeft(text, x, y, color, getFontRenderer().getFontSize(), true);
	}

	public static void drawStringFromBottomLeft(String text, int x, int y, int color, int fontSize) {
		drawStringFromBottomLeft(text, x, y, color, fontSize, true);
	}

	public static void drawStringFromBottomLeft(String text, int x, int y, int color, int fontSize, boolean shadow) {
		drawString(text, x, getScaledResolution().getScaledHeight() - y * 2, color, fontSize, shadow);
	}

	public static void drawRect(int left, int top, int right, int bottom, int color) {
		Gui.drawRect(left, top, right, bottom, color);
	}

	public static void drawModalRect(int xCord, int yCord, int width, int height, int color) {
		Gui.drawRect(xCord, getScaledResolution().getScaledHeight() - yCord, xCord + width, getScaledResolution().getScaledHeight() - yCord - height, color);
	}

	public static void drawModalRectFromRight(int xCord, int yCord, int width, int height, int color) {
		drawModalRect(getScaledResolution().getScaledWidth() - xCord, yCord, width, height, color);
	}

	public static void drawModalRectFromTopRight(int xCord, int yCord, int width, int height, int color) {
		Gui.drawRect(getScaledResolution().getScaledWidth() - xCord, yCord, getScaledResolution().getScaledWidth() - xCord + width, yCord + height, color);
	}

	public static void drawModalRectFromTopLeft(int xCord, int yCord, int width, int height, int color) {
		Gui.drawRect(xCord, yCord, xCord + width, yCord + height, color);
	}

	public static ScaledResolution getScaledResolution() {
		return new ScaledResolution(Minecraft.getMinecraft());
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
		int[] colors = new int[] { 10, 0, 30, 15 };
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();

//		worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
//		worldRenderer.pos(-259, 102, 19).endVertex();
//		worldRenderer.pos(-259, 104, 17).endVertex();
//		worldRenderer.pos(-257, 102, 19).endVertex();
//		worldRenderer.pos(-257, 104, 19).endVertex();
//		tessellator.draw();

		worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		tessellator.draw();
		worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
		tessellator.draw();
		worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
	}

	public static void drawBoundingBox(AxisAlignedBB aa) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldRenderer = tessellator.getWorldRenderer();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ);
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		tessellator.draw();
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.minZ);
		worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
		worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
		tessellator.draw();
	}

	public static void drawOutlinedBlockESP(double x, double y, double z, float red, float green, float blue, float alpha, float lineWidth) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glLineWidth(lineWidth);
		GL11.glColor4f(red, green, blue, alpha);
		drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawBlockESP(double x, double y, double z, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWidth) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
		GL11.glLineWidth(lineWidth);
		GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
		drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawSolidBlockESP(double x, double y, double z, float red, float green, float blue, float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawOutlinedEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawSolidEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		// GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4f(red, green, blue, alpha);
		drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glLineWidth(lineWdith);
		GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
		drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public static void drawTracerLine(double x, double y, double z, float red, float green, float blue, float alpha, float lineWdith) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
//		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(lineWdith);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(2);
		GL11.glVertex3d(0.0D, 0.0D + Minecraft.getMinecraft().thePlayer.getEyeHeight() - 0.2, 0.0D);
		GL11.glVertex3d(x, y, z);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_BLEND);
//		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	public static void renderBoundingBox(Entity entityIn, double x, double y, double z, float partialTicks, int red, int green, int blue, double xAdd, double yAdd, double zAdd) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableDepth();
		AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX + xAdd - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ + zAdd - entityIn.posZ + z, axisalignedbb.maxX - xAdd - entityIn.posX + x, axisalignedbb.maxY + yAdd - entityIn.posY + y, axisalignedbb.maxZ - zAdd - entityIn.posZ + z);

		RenderGlobal.func_181563_a(axisalignedbb1, red, green, blue, 255);

//		Tessellator tessellator = Tessellator.getInstance();
//		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
	}

	public static void renderPlayerESP(Entity entityIn, double x, double y, double z, int red, int green, int blue, float partialTicks) {
		renderBoundingBox(entityIn, x, y, z, partialTicks, red, green, blue, 0.75, 0.15, 0.75);
	}

	public static void renderItemsESP(Entity entityIn, double x, double y, double z, float partialTicks) {
		int red = 150;
		int green = 20;
		int blue = 150;

		renderBoundingBox(entityIn, x, y, z, partialTicks, red, green, blue, 0.35, 0.30, 0.35);
	}

}