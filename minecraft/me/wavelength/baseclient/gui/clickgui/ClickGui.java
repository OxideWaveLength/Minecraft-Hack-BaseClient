package me.wavelength.baseclient.gui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.gui.clickgui.components.Dropdown;
import me.wavelength.baseclient.gui.clickgui.components.ModuleButton;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ClickGui extends GuiScreen {

	private List<Dropdown> dropdowns;

	private boolean fastRender;
	private boolean showDebugInfo;

	public ClickGui() {
	}

	/** Credits for the Blur: MrTheShy */
	@Override
	public void initGui() {
		if (fastRender = mc.gameSettings.ofFastRender)
			mc.gameSettings.setOptionValue(Options.FAST_RENDER, 1);

		this.showDebugInfo = mc.gameSettings.showDebugInfo;
		mc.gameSettings.showDebugInfo = false;

		this.dropdowns = new ArrayList<Dropdown>();

		Dropdown previousDropdown = null;
		for (Category category : Category.values()) {
			if (category.equals(Category.SEMI_HIDDEN) || category.equals(Category.HIDDEN))
				continue;

			int x = 5 + (previousDropdown == null ? 0 : 10 + previousDropdown.getX() + previousDropdown.getWidth());
			int y = (previousDropdown == null ? 5 : previousDropdown.getY());
			Dropdown dropdown = new Dropdown(this, category, x, y, false);
			if (x + dropdown.getWidth() > RenderUtils.getScaledResolution().getScaledWidth() && previousDropdown != null) {
				dropdown.setX(5);
				dropdown.setY(previousDropdown.getY() + previousDropdown.getHeight() + 30);
			}
			dropdowns.add(dropdown);
			previousDropdown = dropdown;
		}

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

		if (fastRender)
			mc.gameSettings.setOptionValue(Options.FAST_RENDER, 1);

		mc.gameSettings.showDebugInfo = showDebugInfo;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int contentColor = new Color(0, 0, 0, 200).getRGB();
		int headerColor = new Color(30, 150, 10, 200).getRGB();
		int textColor = Color.WHITE.getRGB();

		for (int i = 0; i < dropdowns.size(); i++) {
			Dropdown dropdown = dropdowns.get(i);

			Category category = dropdown.getCategory();
			int x = dropdown.getX();
			int y = dropdown.getY();
			int width = dropdown.getWidth();
			int height = dropdown.getHeight();
			List<ModuleButton> moduleButtons = dropdown.getModuleButtons();

			if (dropdown.isExtended())
				RenderUtils.drawModalRectFromTopLeft(x, y + dropdown.getHeaderHeight(), width, height, contentColor);

			if (dropdown.isExtended())
				moduleButtons.forEach(button -> button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY));

			RenderUtils.drawModalRectFromTopLeft(x, y, width, dropdown.getHeaderHeight(), headerColor);
			RenderUtils.drawString(Strings.capitalizeOnlyFirstLetter(category.name()), x + 3, y + 1, textColor);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		List<Dropdown> dropdowns = new ArrayList<Dropdown>(this.dropdowns);
		for (int i = dropdowns.size() - 1; i >= 0; i--) {
			Dropdown dropdown = dropdowns.get(i);
			if (dropdown.mouseClicked(mouseX, mouseY, mouseButton)) {
				this.dropdowns.remove(dropdown);
				this.dropdowns.add(dropdown);
				return;
			}
		}

		for (int i = 0; i < this.buttonList.size(); ++i) {
			ModuleButton guiButton = (ModuleButton) this.buttonList.get(i);
			if (guiButton.mousePressed(this.mc, mouseX, mouseY, mouseButton)) {
				selectedButton = guiButton;
				this.actionPerformed(guiButton);
			}
		}
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		List<Dropdown> dropdowns = new ArrayList<Dropdown>(this.dropdowns);
		for (int i = dropdowns.size() - 1; i >= 0; i--) {
			if (dropdowns.get(i).mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick))
				return;
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		List<Dropdown> dropdowns = new ArrayList<Dropdown>(this.dropdowns);
		for (int i = dropdowns.size() - 1; i >= 0; i--) {
			if (dropdowns.get(i).mouseReleased(mouseX, mouseY, state))
				return;
		}

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

	}

	public List<GuiButton> getButtonList() {
		return buttonList;
	}

	public void setButtonList(List<GuiButton> buttonList) {
		this.buttonList = buttonList;
	}

}