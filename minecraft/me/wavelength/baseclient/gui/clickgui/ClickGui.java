package me.wavelength.baseclient.gui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.gui.clickgui.components.Dropdown;
import me.wavelength.baseclient.gui.clickgui.components.ModuleButton;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.utils.Colors;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings.Options;

public class ClickGui extends GuiScreen {

	private List<Dropdown> dropdowns;

	public me.wavelength.baseclient.module.Module clickGuiMod;

	private boolean fastRender;
	private boolean showDebugInfo;

	private int scroll = 5;

	public ClickGui() {
	}

	@Override
	public void initGui() {
		this.clickGuiMod = BaseClient.instance.getModuleManager()
				.getModule(me.wavelength.baseclient.module.modules.client.ClickGui.class);
		int clickGuiSpacing = clickGuiMod.getModuleSettings().getInt("spacing");

		if (fastRender = mc.gameSettings.ofFastRender)
			mc.gameSettings.setOptionValue(Options.FAST_RENDER, 1);

		this.showDebugInfo = mc.gameSettings.showDebugInfo;
		mc.gameSettings.showDebugInfo = false;

		this.dropdowns = new ArrayList<Dropdown>();

		Dropdown previousDropdown = null;
		for (Category category : Category.values()) {
			if (category.equals(Category.SEMI_HIDDEN) || category.equals(Category.HIDDEN))
				continue;

			Dropdown dropdown = new Dropdown(this, category, 0, 0, true);

			int x = (previousDropdown == null ? 5
					: clickGuiSpacing + (previousDropdown.getX() + previousDropdown.getWidth()));
			int y = this.scroll + (previousDropdown == null ? 5 : previousDropdown.getY());

			dropdown.setX(x);
			dropdown.setY(y);

			if (x + dropdown.getWidth() > RenderUtils.getScaledResolution().getScaledWidth()
					&& previousDropdown != null) {
				dropdown.setX(5);
				dropdown.setY(previousDropdown.getY() + previousDropdown.getHeight() + 30);
			}
			dropdowns.add(dropdown);
			previousDropdown = dropdown;
		}
	}

	@Override
	public void onGuiClosed() {
		if (fastRender)
			mc.gameSettings.setOptionValue(Options.FAST_RENDER, 1);

		mc.gameSettings.showDebugInfo = showDebugInfo;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		ScaledResolution scaledResolution = RenderUtils.getScaledResolution();
		boolean isRainbow = clickGuiMod.getModuleSettings().getBoolean("rainbow");
		boolean isGradient = clickGuiMod.getModuleSettings().getBoolean("gradient");
		int rainbowOffset = clickGuiMod.getModuleSettings().getInt("offset");
		int rainbowSpeed = clickGuiMod.getModuleSettings().getInt("speed");

		handleScrolling();

		int textColor = new Color(255, 255, 255).getRGB();

		for (int i = 0; i < dropdowns.size(); i++) {
			Dropdown dropdown = dropdowns.get(i);
			int contentColor = isRainbow ? Colors.getRGBWave(rainbowSpeed, 1, 0.5f, dropdown.getX() * rainbowOffset)
					: me.wavelength.baseclient.module.Color.getColor(dropdown.getCategory()).getRGB();

			dropdown.setY(this.scroll + dropdown.getMouseLastY());

			Category category = dropdown.getCategory();
			int x = dropdown.getX();
			int y = dropdown.getY();
			int width = dropdown.getWidth();
			int height = dropdown.getHeight();
			List<ModuleButton> moduleButtons = dropdown.getModuleButtons();

			if (dropdown.isExtended()) {
				RenderUtils.drawModalGradientRectFromTopLeft(x, y + dropdown.getHeaderHeight(), width, height,
						contentColor, isGradient ? new Color(0, 0, 0).getRGB() : contentColor);
				moduleButtons.forEach(button -> button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY));
			}

			RenderUtils.drawModalGradientRectFromTopLeft(x, y, width, dropdown.getHeaderHeight(), contentColor,
					isGradient ? new Color(0, 0, 0).getRGB() : contentColor);
			RenderUtils.drawString(Strings.capitalizeOnlyFirstLetter(category.name()), x + 2,
					y + ((dropdown.getHeaderHeight() / 2)
							- (Strings.getStringHeightCFR(Strings.capitalizeOnlyFirstLetter(category.name()),
									BaseClient.instance.getFontRenderer().fontSizeNormal) / 2)),
					textColor, BaseClient.instance.getFontRenderer().fontSizeNormal, true);

			RenderUtils.drawModalRectFromTopLeft(x, (y + dropdown.getHeaderHeight()) - 1,
					Strings.getStringWidthCFR(Strings.capitalizeOnlyFirstLetter(category.name())) / 2, 1,
					new Color(122, 122, 122).getRGB());

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
			if (dropdowns.get(i).mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
				dropdowns.get(i).setMouseLastX(mouseX);
				dropdowns.get(i).setMouseLastY(mouseY - this.scroll);
				return;
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		List<Dropdown> dropdowns = new ArrayList<Dropdown>(this.dropdowns);
		for (int i = dropdowns.size() - 1; i >= 0; i--) {
			if (dropdowns.get(i).mouseReleased(mouseX, mouseY, state)) {
				return;
			}
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

	public void handleScrolling() {
		if (Mouse.hasWheel()) {
			int mouseDelta = Mouse.getDWheel() / clickGuiMod.getModuleSettings().getInt("scroll speed");

			this.scroll += -(mouseDelta);

			if (this.scroll < 5)
				this.scroll = 5;
		}
	}
}