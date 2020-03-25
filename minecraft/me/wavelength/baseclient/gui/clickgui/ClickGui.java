package me.wavelength.baseclient.gui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
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
			int x = 5 + (previousDropdown == null ? 0 : 10 + previousDropdown.getX() + previousDropdown.getWidth());
			int y = (previousDropdown == null ? 5 : previousDropdown.getY());
			Dropdown dropdown = new Dropdown(category, x, y);
			if (x + dropdown.getWidth() > RenderUtils.getScaledResolution().getScaledWidth() && previousDropdown != null) {
				dropdown.setX(5);
				dropdown.setY(previousDropdown.getY() + previousDropdown.getHeight() + 30);
			}
			dropdowns.add(dropdown);
			previousDropdown = dropdown;
		}

		for (Dropdown dropdown : dropdowns) {
			if (dropdown.isExtended())
				dropdown.getModuleButtons().forEach(button -> buttonList.add(button));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		final int fontSize = BaseClient.instance.getFontRenderer().getFontSize() / 2;
		for (int i = 0; i < dropdowns.size(); i++) {
			Dropdown dropdown = dropdowns.get(i);

			Category category = dropdown.getCategory();
			int x = dropdown.getX();
			int y = dropdown.getY();
			int width = dropdown.getWidth();
			int height = dropdown.getHeight();
			List<ModuleButton> moduleButtons = dropdown.getModuleButtons();

			RenderUtils.drawModalRectFromTopLeft(x, y, width, height, new Color(0, 0, 0, 100).getRGB());

			if (dropdown.isExtended())
				moduleButtons.forEach(button -> button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY));

			RenderUtils.drawModalRectFromTopLeft(x, y, width, fontSize + 4, new Color(255, 15, 50, 110).getRGB());
			RenderUtils.drawString(Strings.capitalizeOnlyFirstLetter(category.name()), x + 3, y + 1, Color.WHITE.getRGB());
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//		dropdowns.forEach(dropdown -> {
//			if (dropdown.mouseClicked(mouseX, mouseY, mouseButton))
//				return;
//		});

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
	protected void mouseReleased(int mouseX, int mouseY, int state) {
//		dropdowns.forEach(dropdown -> {
//			if (dropdown.mouseReleased(mouseX, mouseY, state))
//				return;
//		});

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {

	}

}