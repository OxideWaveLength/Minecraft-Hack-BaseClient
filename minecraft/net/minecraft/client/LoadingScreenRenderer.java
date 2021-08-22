package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;

public class LoadingScreenRenderer implements IProgressUpdate {
	private String message = "";

	/** A reference to the Minecraft object. */
	private Minecraft mc;

	/**
	 * The text currently displayed (i.e. the argument to the last call to printText
	 * or displayString)
	 */
	private String currentlyDisplayedText = "";

	/** The system's time represented in milliseconds. */
	private long systemTime = Minecraft.getSystemTime();
	private boolean field_73724_e;
	private ScaledResolution scaledResolution;
	private Framebuffer framebuffer;

	public LoadingScreenRenderer(Minecraft mcIn) {
		this.mc = mcIn;
		this.scaledResolution = new ScaledResolution(mcIn);
		this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
		this.framebuffer.setFramebufferFilter(9728);
	}

	/**
	 * this string, followed by "working..." and then the "% complete" are the 3
	 * lines shown. This resets progress to 0, and the WorkingString to
	 * "working...".
	 */
	public void resetProgressAndMessage(String message) {
		this.field_73724_e = false;
		this.displayString(message);
	}

	/**
	 * Shows the 'Saving level' string.
	 */
	public void displaySavingString(String message) {
		this.field_73724_e = true;
		this.displayString(message);
	}

	private void displayString(String message) {
		this.currentlyDisplayedText = message;

		if (!this.mc.running) {
			if (!this.field_73724_e) {
				throw new MinecraftError();
			}
		} else {
			GlStateManager.clear(256);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();

			if (OpenGlHelper.isFramebufferEnabled()) {
				int i = this.scaledResolution.getScaleFactor();
				GlStateManager.ortho(0.0D, (double) (this.scaledResolution.getScaledWidth() * i), (double) (this.scaledResolution.getScaledHeight() * i), 0.0D, 100.0D, 300.0D);
			} else {
				ScaledResolution scaledresolution = new ScaledResolution(this.mc);
				GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
			}

			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, 0.0F, -200.0F);
		}
	}

	/**
	 * Displays a string on the loading screen supposed to indicate what is being
	 * done currently.
	 */
	public void displayLoadingString(String message) {
		if (!this.mc.running) {
			if (!this.field_73724_e) {
				throw new MinecraftError();
			}
		} else {
			this.systemTime = 0L;
			this.message = message;
			this.setLoadingProgress(-1);
			this.systemTime = 0L;
		}
	}

	/**
	 * Updates the progress bar on the loading screen to the specified amount. Args:
	 * loadProgress
	 */
	public void setLoadingProgress(int progress) {
		if (!this.mc.running) {
			if (!this.field_73724_e) {
				throw new MinecraftError();
			}
		} else {
			long i = Minecraft.getSystemTime();

			if (i - this.systemTime >= 100L) {
				this.systemTime = i;
				ScaledResolution scaledresolution = new ScaledResolution(this.mc);
				int j = scaledresolution.getScaleFactor();
				int k = scaledresolution.getScaledWidth();
				int l = scaledresolution.getScaledHeight();

				if (OpenGlHelper.isFramebufferEnabled()) {
					this.framebuffer.framebufferClear();
				} else {
					GlStateManager.clear(256);
				}

				this.framebuffer.bindFramebuffer(false);
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, 0.0F, -200.0F);

				if (!OpenGlHelper.isFramebufferEnabled()) {
					GlStateManager.clear(16640);
				}

				Tessellator tessellator = Tessellator.getInstance();
				WorldRenderer worldrenderer = tessellator.getWorldRenderer();
				this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
				float f = 32.0F;
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				worldrenderer.pos(0.0D, (double) l, 0.0D).tex(0.0D, (double) ((float) l / f)).color(64, 64, 64, 255).endVertex();
				worldrenderer.pos((double) k, (double) l, 0.0D).tex((double) ((float) k / f), (double) ((float) l / f)).color(64, 64, 64, 255).endVertex();
				worldrenderer.pos((double) k, 0.0D, 0.0D).tex((double) ((float) k / f), 0.0D).color(64, 64, 64, 255).endVertex();
				worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
				tessellator.draw();

				if (progress >= 0) {
					int i1 = 100;
					int j1 = 2;
					int k1 = k / 2 - i1 / 2;
					int l1 = l / 2 + 16;
					GlStateManager.disableTexture2D();
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
					worldrenderer.pos((double) k1, (double) l1, 0.0D).color(128, 128, 128, 255).endVertex();
					worldrenderer.pos((double) k1, (double) (l1 + j1), 0.0D).color(128, 128, 128, 255).endVertex();
					worldrenderer.pos((double) (k1 + i1), (double) (l1 + j1), 0.0D).color(128, 128, 128, 255).endVertex();
					worldrenderer.pos((double) (k1 + i1), (double) l1, 0.0D).color(128, 128, 128, 255).endVertex();
					worldrenderer.pos((double) k1, (double) l1, 0.0D).color(128, 255, 128, 255).endVertex();
					worldrenderer.pos((double) k1, (double) (l1 + j1), 0.0D).color(128, 255, 128, 255).endVertex();
					worldrenderer.pos((double) (k1 + progress), (double) (l1 + j1), 0.0D).color(128, 255, 128, 255).endVertex();
					worldrenderer.pos((double) (k1 + progress), (double) l1, 0.0D).color(128, 255, 128, 255).endVertex();
					tessellator.draw();
					GlStateManager.enableTexture2D();
				}

				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				this.mc.fontRendererObj.drawStringWithShadow(this.currentlyDisplayedText, (float) ((k - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2), (float) (l / 2 - 4 - 16), 16777215);
				this.mc.fontRendererObj.drawStringWithShadow(this.message, (float) ((k - this.mc.fontRendererObj.getStringWidth(this.message)) / 2), (float) (l / 2 - 4 + 8), 16777215);
				this.framebuffer.unbindFramebuffer();

				if (OpenGlHelper.isFramebufferEnabled()) {
					this.framebuffer.framebufferRender(k * j, l * j);
				}

				this.mc.updateDisplay();

				try {
					Thread.yield();
				} catch (Exception var15) {
					;
				}
			}
		}
	}

	public void setDoneWorking() {
	}
}
