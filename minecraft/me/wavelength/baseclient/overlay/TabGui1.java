package me.wavelength.baseclient.overlay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.MouseClickEvent;
import me.wavelength.baseclient.event.events.MouseScrollEvent;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.font.NahrFont.FontType;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.ModuleManager;
import me.wavelength.baseclient.module.ModuleSettings;
import me.wavelength.baseclient.module.modules.hidden.AdvancedTabGui;
import me.wavelength.baseclient.utils.Random;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.Minecraft;

public class TabGui1 extends EventListener {

	private int currentCategory;
	private int currentModule;
	private int currentSetting;

	/**
	 * @formatter:off
	 * 
	 * 0 = Categories
	 * 1 = Modules
	 * 2 = Module Settings
	 * 
	 * @formatter:on
	 **/
	private int indentation;

	private int maxItemWidth;

	private ModuleManager moduleManager;

	public TabGui1() {
		BaseClient.instance.getEventManager().registerListener(this);

		this.moduleManager = BaseClient.instance.getModuleManager();
	}

	/**
	 * @return The current mode, 0 = default mode (ARROW KEYS), 1 = "Advanced" mode
	 *         (MOUSE WHEEL AND MOUSE CLICKS)
	 */
	private int getMode() {
		return (BaseClient.instance.getModuleManager().getModule(AdvancedTabGui.class).isToggled() ? 1 : 0);
	}

	private List<Module> getModules() {
		return moduleManager.getModules(Category.values()[currentCategory]);
	}

	private Module getCurrentModule() {
		return getModules().get(currentModule);
	}

	private List<String> getCurrentSettingsList() {
		return getCurrentModule().getModuleSettings().getConfig().readLines();
	}

	private ModuleSettings getCurrentModuleSettings() {
		return getCurrentModule().getModuleSettings();
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		// TODO: Render ICON

		switch (indentation) {
		/** If inside the Category indentation draw it */
		case 0:
		default: {
			renderCategories(event);
			break;
		}
		/** If inside the Module indentation draw it */
		case 1: {
			renderModules(event);
			break;
		}
		/** If inside the Module Settings indentation draw it */
		case 2: {
			renderSettings(event);
			break;
		}
		}
	}

	/**
	 * @formatter:off
	 * @param direction 0 means the direction means DOWN and 1 means the direction is UP
	 * @formatter:on
	 */
	private void menuScroll(int direction) {
		switch (indentation) {
		/** If inside the Category indentation scroll through it */
		case 0:
		default: {
			currentCategory = (direction == 0 ? (currentCategory == Category.values().length - 3 ? 0 : currentCategory + 1) : (currentCategory == 0 ? Category.values().length - 3 : currentCategory - 1));
			break;
		}
		/** If inside the Module indentation scroll through it */
		case 1: {
			currentModule = (direction == 0 ? (currentModule == getModules().size() - 1 ? 0 : currentModule + 1) : (currentModule == 0 ? getModules().size() - 1 : currentModule - 1));
			break;
		}
		case 2: {
			currentSetting = (direction == 0 ? (currentSetting == getCurrentSettingsList().size() - 1 ? 0 : currentSetting + 1) : (currentSetting == 0 ? getCurrentSettingsList().size() - 1 : currentSetting - 1));
			break;
		}
		}
	}

	/**
	 * @formatter:off
	 * @param direction 0 means LEFT and 1 means RIGHT
	 * @formatter:on
	 */
	private void menuInteract(int direction) {
		boolean next = (indentation == 2 ? false : (indentation == 0 && getModules().size() == 0 ? false : (indentation == 1 && getCurrentSettingsList().size() == 0 ? false : true)));

		int difference = (direction == 0 ? (indentation == 0 ? 0 : -1) : (next ? 1 : 0));

		indentation += difference;
		maxItemWidth = (difference == 0 ? maxItemWidth : 0);
	}

	@Override
	public void onKeyPressed(KeyPressedEvent event) {
		if (event.getKey() == Keyboard.KEY_RETURN && indentation == 1) {
			getCurrentModule().toggle();
			return;
		}

		if (getMode() != 0)
			return;

		switch (event.getKey()) {
		case Keyboard.KEY_UP: {
			menuScroll(1);
			break;
		}
		case Keyboard.KEY_DOWN: {
			menuScroll(0);
			break;
		}
		case Keyboard.KEY_BACK:
		case Keyboard.KEY_LEFT: {
			menuInteract(0);
			break;
		}
		case Keyboard.KEY_RIGHT: {
			menuInteract(1);
			break;
		}
		default: {
			return;
		}
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (getMode() != 1)
			return;

		event.setCancelled(true);

		switch (event.getDirection()) {
		case 120: {
			menuScroll(1);
			break;
		}
		case -120: {
			menuScroll(0);
			break;
		}
		default: {
			return;
		}
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		if (getMode() != 1)
			return;

		event.setCancelled(true);

		switch (event.getButton()) {
		case 1: {
			menuInteract(0);
			break;
		}
		case 0: {
			menuInteract(1);
			break;
		}
		case 4: {
			if (!(indentation == 1))
				break;

			getCurrentModule().toggle();
		}
		default:
			return;
		}

	}

	private void renderCategories(Render2DEvent event) {
		List<String> items = new ArrayList<String>();

		Category[] categories = Category.values();
		for (int i = 0; i < categories.length; i++) {
			Category category = categories[i];

			if (category.equals(Category.HIDDEN) || category.equals(Category.SEMI_HIDDEN))
				continue;

			items.add(Strings.capitalizeOnlyFirstLetter(category.name()));
		}
		renderMenu(items, currentCategory);
	}

	private void renderModules(Render2DEvent event) {
		List<Module> modules = new ArrayList<Module>(getModules());

		List<String> items = new ArrayList<String>();
		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			items.add((module.isToggled() ? "&a" : "") + Strings.capitalizeFirstLetter(module.getName()));
		}
		renderMenu(items, currentModule);
	}

	private void renderSettings(Render2DEvent event) {
		List<String> moduleSettingsList = new ArrayList<String>(getCurrentSettingsList());

		renderMenu(getCurrentSettingsList(), currentSetting);
	}

	private void renderMenu(List<String> items, int currentItem) {
		int height = 15;

		RenderUtils.drawString(String.format("&f%1$s &8-&b %2$s", BaseClient.instance.getClientName(), BaseClient.instance.getClientVersion()), 5, 12, FontType.SHADOW_THIN, -1);

		RenderUtils.drawRect(5, height * 2 - 3, maxItemWidth + 15 + 5, height * (items.size() + 2) - 3, new Color(0, 0, 0, 130).getRGB());
		for (int i = 0; i < items.size(); i++) {
			String item = items.get(i);

			int itemWidth = Strings.getStringWidthCFR(item);

			if (itemWidth > maxItemWidth)
				maxItemWidth = itemWidth;

			boolean isCurrentItem = (i == currentItem);

			Color backgroundColor = new Color(255, 255, 255);

			if (isCurrentItem) {
				backgroundColor = new Color(84, 199, 222);

				RenderUtils.drawRect(5, 10 + height * (i + 2) - height + 2, maxItemWidth + 15 + 5, height * (i + 3) - 3, backgroundColor.getRGB());
			}

			RenderUtils.drawString(item, 10, height * (i + 2), FontType.SHADOW_THIN, -1);
		}

		if (indentation != 1 || getCurrentModule() == null)
			return;

		String description = getCurrentModule().getDescription();
		RenderUtils.drawRect(5, 9 + height * (items.size() + 3) - height + 2, Strings.getStringWidthCFR(description) + 12, height * (items.size() + 4) - 3, new Color(0, 0, 0, 100).getRGB());
		RenderUtils.drawString(description, 8, height * (items.size() + 3), FontType.SHADOW_THIN, -1);
	}

}