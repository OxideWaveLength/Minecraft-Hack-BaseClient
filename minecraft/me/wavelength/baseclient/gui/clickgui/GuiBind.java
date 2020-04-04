package me.wavelength.baseclient.gui.clickgui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.ModuleManager;
import me.wavelength.baseclient.utils.KeyUtils;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBind extends GuiScreen {

	private Module module;
	private ClickGui clickGui;

	private boolean fastRender;

	public GuiBind(Module module, ClickGui clickGui) {
		this.module = module;
		this.clickGui = clickGui;
	}

	/** Credits for the blur effect: MrTheShy */
	@Override
	public void initGui() {
		if (fastRender = mc.gameSettings.ofFastRender)
			mc.gameSettings.setOptionValue(Options.FAST_RENDER, 1);

		if (!(mc.gameSettings.ofFastRender) && OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (this.mc.entityRenderer.theShaderGroup != null)
				this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();

			this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
		}

		if (fastRender)
			mc.gameSettings.setOptionValue(Options.FAST_RENDER, 1);
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
		RenderUtils.drawModalRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0, 0, 0, 110).getRGB());

		String bindText = String.format("Bind the module %s", Strings.capitalizeFirstLetter(module.getName()));
		String currentBoundText = (module.getKey() == 0 ? "Currently not bound" : String.format("Currently bound to %s", KeyUtils.getKeyName(module.getKey())));
		String escapeText = "Press Escape to cancel and Back or Delete to unbind";

		int currentBoundTextFontSize = BaseClient.instance.getFontRenderer().getFontSize() - 3;
		int escapeTextFontSize = BaseClient.instance.getFontRenderer().getFontSize() - 8;

		int bindTextHeight = Strings.getStringHeightCFR(bindText);
		int currentBoundTextHeight = Strings.getStringHeightCFR(currentBoundText, currentBoundTextFontSize);
		int escapeTextHeight = Strings.getStringHeightCFR(escapeText, escapeTextFontSize);

		int bindTextWidth = Strings.getStringWidthCFR(bindText);
		int currentBoundTextWidth = Strings.getStringWidthCFR(currentBoundText, currentBoundTextFontSize);
		int escapeTextWidth = Strings.getStringWidthCFR(escapeText, escapeTextFontSize);

		int yOffset = 3;

		RenderUtils.drawString(bindText, scaledResolution.getScaledWidth() / 2 - bindTextWidth / 2, bindTextHeight, Color.WHITE.getRGB());

		RenderUtils.drawString(currentBoundText, scaledResolution.getScaledWidth() / 2 - currentBoundTextWidth / 2, bindTextHeight + yOffset + currentBoundTextHeight, Color.LIGHT_GRAY.getRGB(), currentBoundTextFontSize);
		RenderUtils.drawString(escapeText, scaledResolution.getScaledWidth() / 2 - escapeTextWidth / 2, bindTextHeight + yOffset + currentBoundTextHeight + yOffset * 2 + escapeTextHeight, Color.GRAY.getRGB(), escapeTextFontSize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			back();
			return;
		} else if (keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE) {
			module.setKey(Keyboard.KEY_NONE);
			back();
			return;
		}

		ModuleManager moduleManager = BaseClient.instance.getModuleManager();

		Module boundModule = moduleManager.getModule(keyCode);
		if (boundModule != null) {
			System.out.println("Already bound."); // TODO: Confirm
			return;
		}

		module.setKey(keyCode);
		back();
	}

	private void back() {
		mc.displayGuiScreen(clickGui);
	}

}