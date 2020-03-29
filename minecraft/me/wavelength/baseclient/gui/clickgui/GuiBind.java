package me.wavelength.baseclient.gui.clickgui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.ModuleManager;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBind extends GuiScreen {

	private Module module;
	private ClickGui clickGui;

	public GuiBind(Module module, ClickGui clickGui) {
		this.module = module;
		this.clickGui = clickGui;
	}

	/** Credits for the Blur: MrTheShy */
	@Override
	public void initGui() {
		if (!(mc.gameSettings.ofFastRender) && OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (this.mc.entityRenderer.theShaderGroup != null)
				this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();

			this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
		}
	}

	@Override
	public void onGuiClosed() {
		if (!(mc.gameSettings.ofFastRender) && this.mc.entityRenderer.theShaderGroup != null) {
			this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			this.mc.entityRenderer.theShaderGroup = null;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution scaledResolution = RenderUtils.getScaledResolution();
		RenderUtils.drawModalRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0, 0, 0, 100).getRGB());

		String bindText = String.format("Bind the module %1$s", Strings.capitalizeFirstLetter(module.getName()));
		String escapeText = "Press Escape to cancel and Back or Delete to unbind";

		int escapeTextFontSize = BaseClient.instance.getFontRenderer().getFontSize() - 3;

		int bindTextHeight = Strings.getStringHeightCFR(bindText);
		int escapeTextHeight = Strings.getStringHeightCFR(escapeText);

		RenderUtils.drawString(bindText, scaledResolution.getScaledWidth() / 2 - Strings.getStringWidthCFR(bindText) / 2, bindTextHeight, Color.WHITE.getRGB());

		RenderUtils.drawString(escapeText, scaledResolution.getScaledWidth() / 2 - Strings.getStringWidthCFR(escapeText, escapeTextFontSize) / 2, bindTextHeight + escapeTextHeight, Color.GRAY.getRGB(), escapeTextFontSize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE)
			mc.displayGuiScreen(clickGui); // TODO: Change with back();
		else if (keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE) {
			module.setKey(Keyboard.KEY_NONE);
			mc.displayGuiScreen(clickGui); // TODO: Change with back();
			return;
		}

		ModuleManager moduleManager = BaseClient.instance.getModuleManager();

		Module boundModule = moduleManager.getModule(keyCode);
		if (boundModule != null) {
			System.out.println("Already bound."); // TODO: Confirm
			return;
		}

		module.setKey(keyCode);
		mc.displayGuiScreen(clickGui); // TODO: Change with back();
	}

}