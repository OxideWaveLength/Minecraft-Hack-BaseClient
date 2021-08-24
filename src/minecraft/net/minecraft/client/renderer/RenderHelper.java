package net.minecraft.client.renderer;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.Vec3;

public class RenderHelper {
	/** Float buffer used to set OpenGL material colors */
	private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
	private static final Vec3 LIGHT0_POS = (new Vec3(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
	private static final Vec3 LIGHT1_POS = (new Vec3(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

	/**
	 * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
	 */
	public static void disableStandardItemLighting() {
		GlStateManager.disableLighting();
		GlStateManager.disableLight(0);
		GlStateManager.disableLight(1);
		GlStateManager.disableColorMaterial();
	}

	/**
	 * Sets the OpenGL lighting properties to the values used when rendering blocks
	 * as items
	 */
	public static void enableStandardItemLighting() {
		GlStateManager.enableLighting();
		GlStateManager.enableLight(0);
		GlStateManager.enableLight(1);
		GlStateManager.enableColorMaterial();
		GlStateManager.colorMaterial(1032, 5634);
		float f = 0.4F;
		float f1 = 0.6F;
		float f2 = 0.0F;
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer) setColorBuffer(LIGHT0_POS.xCoord, LIGHT0_POS.yCoord, LIGHT0_POS.zCoord, 0.0D));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer) setColorBuffer(f1, f1, f1, 1.0F));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, (FloatBuffer) setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, (FloatBuffer) setColorBuffer(f2, f2, f2, 1.0F));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) setColorBuffer(LIGHT1_POS.xCoord, LIGHT1_POS.yCoord, LIGHT1_POS.zCoord, 0.0D));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer) setColorBuffer(f1, f1, f1, 1.0F));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer) setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, (FloatBuffer) setColorBuffer(f2, f2, f2, 1.0F));
		GlStateManager.shadeModel(7424);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, (FloatBuffer) setColorBuffer(f, f, f, 1.0F));
	}

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_) {
		return setColorBuffer((float) p_74517_0_, (float) p_74517_2_, (float) p_74517_4_, (float) p_74517_6_);
	}

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_) {
		colorBuffer.clear();
		colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
		colorBuffer.flip();
		return colorBuffer;
	}

	/**
	 * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such
	 * as containers).
	 */
	public static void enableGUIStandardItemLighting() {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
		enableStandardItemLighting();
		GlStateManager.popMatrix();
	}
}
