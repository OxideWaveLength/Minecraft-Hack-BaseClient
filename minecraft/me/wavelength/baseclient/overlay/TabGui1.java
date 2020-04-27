package me.wavelength.baseclient.overlay;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.MouseClickEvent;
import me.wavelength.baseclient.event.events.MouseScrollEvent;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.gui.clickgui.ClickGui;
import me.wavelength.baseclient.gui.clickgui.GuiBind;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.ModuleManager;
import me.wavelength.baseclient.module.ModuleSettings;
import me.wavelength.baseclient.module.modules.semi_hidden.AdvancedTabGui;
import me.wavelength.baseclient.module.modules.semi_hidden.TabGui;
import me.wavelength.baseclient.utils.Config;
import me.wavelength.baseclient.utils.Integers;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.utils.Strings;
import net.minecraft.client.gui.GuiScreen;

public class TabGui1 extends EventListener {

	private int currentCategory;
	private int currentModule;
	private int currentSetting;

	private String[] moduleSettingsExceptions;

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

		this.moduleSettingsExceptions = new String[] { "toggled", "key" };
	}

	/**
	 * @return The current mode, 0 = default mode (ARROW KEYS), 1 = "Advanced" mode (MOUSE WHEEL (CAN BE BOUND) AND MOUSE CLICKS)
	 */
	private int getMode() {
		return (BaseClient.instance.getModuleManager().getModule(AdvancedTabGui.class).isToggled() ? 1 : 0);
	}

	private List<Module> getModules() {
		return moduleManager.getModules(Category.values()[currentCategory]);
	}

	private Module getCurrentModule() {
		int modulesSize = getModules().size();
		if (modulesSize <= currentModule)
			currentModule = modulesSize;

		return getModules().get(currentModule);
	}

	private List<String> getCurrentSettingsList() {
		return getCurrentModule().getModuleSettings().getConfig().readLines();
	}

	private List<String> getFilteredSettingsList() {
		List<String> settings = new ArrayList<String>(getCurrentSettingsList());
		for (int i = 0; i < moduleSettingsExceptions.length; i++) {
			for (int j = 0; j < settings.size(); j++) {
				String setting = settings.get(j);
				setting = setting.substring(0, setting.indexOf(":"));

				if (moduleSettingsExceptions[i].equalsIgnoreCase(setting))
					settings.remove(j);
			}
		}

		return settings;
	}

	private String getCurrentSettings() {
		return getCurrentSettingsList().get(currentSetting);
	}

	private String getCurrentSettingsFiltered() {
		return getFilteredSettingsList().get(currentSetting);
	}

	private ModuleSettings getCurrentModuleSettings() {
		return getCurrentModule().getModuleSettings();
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		if (!(BaseClient.instance.getModuleManager().getModule(TabGui.class)).isToggled())
			return;

		GuiScreen currentScreen = mc.currentScreen;

		if (currentScreen != null && (currentScreen instanceof ClickGui || currentScreen instanceof GuiBind))
			return;

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
		case 3:
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
		if (!(BaseClient.instance.getModuleManager().getModule(TabGui.class).isToggled()))
			return;

		if (indentation == 3) {
			String[] currentSetting = getCurrentSettingsFiltered().split(": ");

			String key = currentSetting[0];
			String value = currentSetting[1];

			if (Integers.isInteger(value)) {
				int v = Integers.getInteger(value);
				v += (direction == 0 ? -1 : 1);
				getCurrentModule().getModuleSettings().set(key, v);
			} else if (Integers.isDouble(value)) {
				double v = Double.parseDouble(value);
				double incr = mc.gameSettings.keyBindSneak.isKeyDown() ? 1 : 0.1D;

				v += (direction == 0 ? -incr : incr);

				v = Double.valueOf(new DecimalFormat("#.#").format(v).replace(",", "."));

				if (v < 0)
					v = 0.0D;

				if (!(Double.toString(v).contains(".")))
					v = Double.parseDouble(v + ".0");

				getCurrentModule().getModuleSettings().set(key, v);
			} else if (Strings.isBoolean(value)) {
				boolean v = Strings.getBooleanValue(value);
				v = !v;
				getCurrentModule().getModuleSettings().set(key, v);
			} else {
				if (key.equalsIgnoreCase("anticheat")) {
					AntiCheat[] allowedAntiCheats = getCurrentModule().getAllowedAntiCheats();

					int currentAntiCheat = Arrays.asList(allowedAntiCheats).indexOf(AntiCheat.valueOf(value.toUpperCase()));

					int newAntiCheat = (direction == 0 ? (currentAntiCheat == 0 ? allowedAntiCheats.length - 1 : currentAntiCheat - 1) : (currentAntiCheat + 1 >= allowedAntiCheats.length ? 0 : currentAntiCheat + 1));

					getCurrentModule().setAntiCheat(allowedAntiCheats[newAntiCheat]);
				}
			}

			maxItemWidth = 0;
			return;
		}

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
		case 2:
		case 3: {
			currentSetting = (direction == 0 ? (currentSetting == getFilteredSettingsList().size() - 1 ? 0 : currentSetting + 1) : (currentSetting == 0 ? getFilteredSettingsList().size() - 1 : currentSetting - 1));
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
		if (!(BaseClient.instance.getModuleManager().getModule(TabGui.class).isToggled()))
			return;

		if (indentation == 3) {
			indentation -= 2;
			menuInteract(1);
			return;
		}

		boolean next = (indentation == 0 && getModules().size() == 0 ? false : (indentation == 1 && getCurrentSettingsList().size() == 0 ? false : true));

		if (indentation == 0 && getModules().size() == 0)
			next = false;

		if (indentation == 1) {
			if (getFilteredSettingsList().size() == 0)
				next = false;
			else {
				List<String> moduleSettings = getFilteredSettingsList();
				next = false;
				for (int i = 0; i < moduleSettings.size(); i++) {
					String moduleSetting = moduleSettings.get(i);
					moduleSetting = moduleSetting.substring(0, moduleSetting.indexOf(":"));
					if (moduleSetting.equals("anticheat")) {
						if (getCurrentModule().getAllowedAntiCheats().length == 1)
							continue;
					}
					next = true;
					break;
				}
			}
		}

		int difference = (direction == 0 ? (indentation == 0 ? 0 : (indentation == 3 ? 0 : -1)) : (next ? 1 : 0));

		indentation += difference;

		if (indentation < 2)
			currentSetting = 0;
		if (indentation < 1)
			currentModule = 0;

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
		case 240: {
			menuScroll(1);
			menuScroll(1);
		}
		case -120: {
			menuScroll(0);
			break;
		}
		case -240: {
			menuScroll(0);
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
		renderMenu(getFilteredSettingsList(), currentSetting);
	}

	private void renderMenu(List<String> items, int currentItem) {
		int height = BaseClient.instance.getFontRenderer().getFontSize() / 2 + 4;

		RenderUtils.drawString(String.format("&f%1$s &8-&b %2$s", BaseClient.instance.getClientName(), BaseClient.instance.getClientVersion()), 5, 12, -1);

		RenderUtils.drawRect(5, height * 2 - 5, maxItemWidth + 15 + 5, height * (items.size() + 2) - 5, new Color(0, 0, 0, 130).getRGB());

		for (int i = 0; i < items.size(); i++) {
			String item = items.get(i);

			int itemWidth = Strings.getStringWidthCFR(item);

			if (itemWidth > maxItemWidth)
				maxItemWidth = itemWidth;

			boolean isCurrentItem = (i == currentItem);

			Color backgroundColor = new Color(255, 255, 255);

			if (isCurrentItem) {
				Config genericConfig = BaseClient.instance.getGenericConfig();
				String tabGuiColor = genericConfig.getString("tabguicolor");
				backgroundColor = (Integers.isInteger(tabGuiColor) ? new Color(Integers.getInteger(tabGuiColor)) : Strings.getColor(tabGuiColor));

				RenderUtils.drawRect(5, height * (i + 2) - 5, maxItemWidth + 15 + 5, height * (i + 3) - 5, backgroundColor.getRGB());
				if (indentation == 3)
					item = "&a" + item;
			}

			RenderUtils.drawString(item, 10, height * (i + 2) - 5, -1);
		}

		if (indentation != 1 || getCurrentModule() == null)
			return;

		String description = getCurrentModule().getDescription();
		RenderUtils.drawRect(5, 9 + height * (items.size() + 3) - height + 2, Strings.getStringWidthCFR(description) + 12, height * (items.size() + 4) - 3, new Color(0, 0, 0, 100).getRGB());
		RenderUtils.drawString(description, 8, height * (items.size() + 3), -1);
	}

}