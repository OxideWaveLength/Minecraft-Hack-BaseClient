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

public class ClickGui extends GuiScreen {

	private List<Dropdown> dropdowns;

	public ClickGui() {
	}

	@Override
	public void initGui() {
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

			/** Rendering this of height 0 instead of not rendering because else there would be problems with the colors.. Will be looked into */
			RenderUtils.drawModalRectFromTopLeft(x, y + dropdown.getHeaderHeight(), width, dropdown.isExtended() ? height : 0, contentColor);

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

		if (mouseButton == 0) {
			for (int i = 0; i < this.buttonList.size(); ++i) {
				GuiButton guibutton = (GuiButton) this.buttonList.get(i);
				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					selectedButton = guibutton;
					this.actionPerformed(guibutton);
				}
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