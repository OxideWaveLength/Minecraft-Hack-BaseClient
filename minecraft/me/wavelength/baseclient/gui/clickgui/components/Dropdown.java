package me.wavelength.baseclient.gui.clickgui.components;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.gui.clickgui.ClickGui;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.gui.GuiButton;

public class Dropdown {

	private ClickGui clickGui;

	private Category category;

	private int x;
	private int y;

	private int width;
	private int height;

	private int headerHeight;

	private boolean dragging;
	private boolean extended;

	private int fontSize;

	private List<Module> modules;
	private List<ModuleButton> moduleButtons;

	/** TODO: Replace this to feed directly the title and the content as string and string list, this way this class can be used for the module settings as well */
	public Dropdown(ClickGui clickGui, Category category, int x, int y) {
		this.clickGui = clickGui;

		this.category = category;

		this.x = x;
		this.y = y;

		this.modules = BaseClient.instance.getModuleManager().getModules(category);
		this.moduleButtons = new ArrayList<ModuleButton>();

		modules.sort((module1, module2) -> Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module2.getName())) - Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module1.getName())));

		this.fontSize = BaseClient.instance.getFontRenderer().getFontSize() / 2;

		this.width = Strings.getStringWidthCFR(category.name()) + 5;

		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			int moduleWidth = Strings.getStringWidthCFR(module.getName());
			if (moduleWidth > this.width)
				this.width = moduleWidth;

			this.moduleButtons.add(new ModuleButton(i, x + 3, y + ((i + 1) * fontSize) + (6 + i), moduleWidth + 6, fontSize, module));
		}

		this.width = width + 12;

		this.headerHeight = y + fontSize + 5;

		updateHeight();
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public int getHeaderHeight() {
		return headerHeight;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isDragging() {
		return dragging;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
		updateHeight();
	}

	public void toggleExtend() {
		this.extended = !(extended);
		updateHeight();

		List<GuiButton> buttonList = new ArrayList<GuiButton>(clickGui.getButtonList());
		if (!(extended)) {
			buttonList.removeAll(moduleButtons);
		} else {
			buttonList.addAll(moduleButtons);
		}
		clickGui.setButtonList(buttonList);
	}

	private void updateHeight() {
		this.height = fontSize * (extended ? (modules.size() + 2) : 1);
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<ModuleButton> getModuleButtons() {
		return moduleButtons;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			this.dragging = true;
			return true;
		} else if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
			toggleExtend();
			return true;
		}
//		else if (mouseButton == 1 && extended) {
//			for (ModuleButton moduleButton : moduleButtons) {
//				if (moduleButton.mouseClicked(mouseX, mouseY, mouseButton))
//					return true;
//			}
//		}

		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY <= y + fontSize + 5);
	}

}