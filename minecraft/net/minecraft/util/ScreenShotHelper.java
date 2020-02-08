package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;

public class ScreenShotHelper {

	private static final Logger logger = LogManager.getLogger();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	/** A buffer to hold pixel values returned by OpenGL. */
	private static IntBuffer pixelBuffer;

	/**
	 * The built-up array that contains all the pixel values returned by OpenGL.
	 */
	private static int[] pixelValues;

	/**
	 * Saves a screenshot in the game directory with a time-stamped filename. Args:
	 * gameDirectory, requestedWidthInPixels, requestedHeightInPixels, frameBuffer
	 */
	public static ChatComponentTranslation saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
		return saveScreenshot(gameDirectory, (String) null, width, height, buffer);
	}

	/**
	 * Saves a screenshot in the game directory with the given file name (or null to
	 * generate a time-stamped name). Args: gameDirectory, fileName,
	 * requestedWidthInPixels, requestedHeightInPixels, frameBuffer
	 */
	public static ChatComponentTranslation saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
		try {
			File file1 = new File(gameDirectory, "screenshots");
			file1.mkdir();

			if (OpenGlHelper.isFramebufferEnabled()) {
				width = buffer.framebufferTextureWidth;
				height = buffer.framebufferTextureHeight;
			}

			int i = width * height;

			if (pixelBuffer == null || pixelBuffer.capacity() < i) {
				pixelBuffer = BufferUtils.createIntBuffer(i);
				pixelValues = new int[i];
			}

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			if (OpenGlHelper.isFramebufferEnabled()) {
				GlStateManager.bindTexture(buffer.framebufferTexture);
				GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) pixelBuffer);
			} else {
				GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) pixelBuffer);
			}

			pixelBuffer.get(pixelValues);
			TextureUtil.processPixelValues(pixelValues, width, height);
			BufferedImage bufferedimage = null;

			if (OpenGlHelper.isFramebufferEnabled()) {
				bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
				int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

				for (int k = j; k < buffer.framebufferTextureHeight; ++k) {
					for (int l = 0; l < buffer.framebufferWidth; ++l) {
						bufferedimage.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
					}
				}
			} else {
				bufferedimage = new BufferedImage(width, height, 1);
				bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
			}

			File file2;

			if (screenshotName == null) {
				file2 = getTimestampedPNGFileForDirectory(file1);
			} else {
				file2 = new File(file1, screenshotName);
			}

			ImageIO.write(bufferedimage, "png", (File) file2);
			IChatComponent ichatcomponent = new ChatComponentText(file2.getName());
			ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
			ichatcomponent.getChatStyle().setUnderlined(Boolean.valueOf(true));
			return new ChatComponentTranslation("screenshot.success", new Object[] { ichatcomponent });
		} catch (Exception exception) {
			logger.warn((String) "Couldn\'t save screenshot", (Throwable) exception);
			return new ChatComponentTranslation("screenshot.failure", new Object[] { exception.getMessage() });
		}
	}

	/**
	 * Creates a unique PNG file in the given directory named by a timestamp.
	 * Handles cases where the timestamp alone is not enough to create a uniquely
	 * named file, though it still might suffer from an unlikely race condition
	 * where the filename was unique when this method was called, but another
	 * process or thread created a file at the same path immediately after this
	 * method returned.
	 */
	private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
		String s = dateFormat.format(new Date()).toString();
		int i = 1;

		while (true) {
			File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");

			if (!file1.exists()) {
				return file1;
			}

			++i;
		}
	}

}