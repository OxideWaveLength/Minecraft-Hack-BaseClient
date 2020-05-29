package me.wavelength.baseclient.gui.clickgui.components;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.gui.clickgui.ClickGui;
import me.wavelength.baseclient.gui.clickgui.components.ModuleButton.UpdateAction;
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
	public Dropdown(ClickGui clickGui, Category category, int x, int y, boolean extended) {
		this.clickGui = clickGui;

		this.category = category;

		this.x = x;
		this.y = y;

		this.extended = extended;

		this.modules = BaseClient.instance.getModuleManager().getModules(category);
		this.moduleButtons = new ArrayList<ModuleButton>();

		modules.sort((module1, module2) -> Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module2.getName())) - Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module1.getName())));

		this.fontSize = BaseClient.instance.getFontRenderer().getFontSize() / 2;

		this.width = Strings.getStringWidthCFR(category.name()) + 5;

		updateHeight();

		this.width = width + 12;

		updateButtons(UpdateAction.REPOPULATE);
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
		updateHeight();
		updateButtons(UpdateAction.UPDATE_POSITION);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		updateHeight();
		updateButtons(UpdateAction.UPDATE_POSITION);
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

		updateButtons();
	}

	private void updateHeight() {
		this.height = fontSize * (extended ? (modules.size()) : 0) + 6;

		this.headerHeight = fontSize + 6;
	}

	private void updateButtons() {
		updateButtons(UpdateAction.NONE);
	}

	/**
	 * 
	 * @param action
	 */
	private void updateButtons(UpdateAction action) {
		if (action.equals(UpdateAction.REPOPULATE)) {
			moduleButtons.clear();
			for (int i = 0; i < modules.size(); i++) {
				Module module = modules.get(i);
				int moduleWidth = Strings.getStringWidthCFR(Strings.capitalizeFirstLetter(module.getName()));

				moduleWidth += 6 + 3; // 3 is the y offset, should be parametized

				if (moduleWidth > this.width)
					this.width = moduleWidth;

				int[] position = ModuleButton.getPosition(this, i);

				this.moduleButtons.add(new ModuleButton(i, position[0], position[1], moduleWidth, fontSize, module, clickGui));
			}
		} else if (action.equals(UpdateAction.UPDATE_POSITION)) {
			for (int i = 0; i < moduleButtons.size(); i++) {
				ModuleButton button = moduleButtons.get(i);

				int[] position = ModuleButton.getPosition(this, i);
				button.xPosition = position[0];
				button.yPosition = position[1];
			}
		}

		List<GuiButton> buttonList = new ArrayList<GuiButton>(clickGui.getButtonList());
		buttonList.removeAll(moduleButtons);
		if (extended)
			buttonList.addAll(moduleButtons);
		clickGui.setButtonList(buttonList);
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
			if (modules.size() > 0)
				toggleExtend();
			return true;
		}

		return false;
	}

	public boolean mouseReleased(int mouseX, int mouseY, int state) {
		if (dragging)
			return !(this.dragging = false);

		return false;
	}

	public boolean mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
		if (mouseButton == 0 && dragging) {
//			this.x = (mouseX + width - x);
//			this.x = (x - width - (x - width / 2 - mouseX));

			this.x = mouseX - width / 2;
			this.y = mouseY - headerHeight / 2;

			updateButtons(UpdateAction.UPDATE_POSITION);
			return true;
		}

		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY <= y + fontSize + 5);
	}

}