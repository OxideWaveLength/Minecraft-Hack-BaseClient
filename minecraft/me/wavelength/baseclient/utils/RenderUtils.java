package me.wavelength.baseclient.utils;

import org.lwjgl.opengl.GL11;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.font.NahrFont;
import me.wavelength.baseclient.font.NahrFont.FontType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class RenderUtils {

	public static void drawString(String text, int x, int y, FontType fontType, int color, int color2) {
		BaseClient.instance.getFontRenderer().drawString(text, x, y, fontType, color, color2);
	}

	public static void drawRect(int left, int top, int right, int bottom, int color) {
		Gui.drawRect(left, top, right, bottom, color);
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
		/** Called from the RenderManager */
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableDepth();
		float f = entityIn.width / 2.0F;
		AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX + xAdd - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ + zAdd - entityIn.posZ + z, axisalignedbb.maxX - xAdd - entityIn.posX + x, axisalignedbb.maxY + yAdd - entityIn.posY + y, axisalignedbb.maxZ - zAdd - entityIn.posZ + z);

		RenderGlobal.func_181563_a(axisalignedbb1, red, green, blue, 255);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
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