package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiSnooper extends GuiScreen {
	private final GuiScreen field_146608_a;

	/** Reference to the GameSettings object. */
	private final GameSettings game_settings_2;
	private final java.util.List<String> field_146604_g = Lists.<String>newArrayList();
	private final java.util.List<String> field_146609_h = Lists.<String>newArrayList();
	private String field_146610_i;
	private String[] field_146607_r;
	private GuiSnooper.List field_146606_s;
	private GuiButton field_146605_t;

	public GuiSnooper(GuiScreen p_i1061_1_, GameSettings p_i1061_2_) {
		this.field_146608_a = p_i1061_1_;
		this.game_settings_2 = p_i1061_2_;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.field_146610_i = I18n.format("options.snooper.title", new Object[0]);
		String s = I18n.format("options.snooper.desc", new Object[0]);
		java.util.List<String> list = Lists.<String>newArrayList();

		for (Object s1 : this.fontRendererObj.listFormattedStringToWidth(s, this.width - 30)) {
			list.add((String) s1);
		}

		this.field_146607_r = (String[]) list.toArray(new String[list.size()]);
		this.field_146604_g.clear();
		this.field_146609_h.clear();
		this.buttonList.add(this.field_146605_t = new GuiButton(1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done", new Object[0])));
		boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

		for (Entry<String, String> entry : (new TreeMap<String, String>(this.mc.getPlayerUsageSnooper().getCurrentStats())).entrySet()) {
			this.field_146604_g.add((flag ? "C " : "") + (String) entry.getKey());
			this.field_146609_h.add(this.fontRendererObj.trimStringToWidth((String) entry.getValue(), this.width - 220));
		}

		if (flag) {
			for (Entry<String, String> entry1 : (new TreeMap<String, String>(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats())).entrySet()) {
				this.field_146604_g.add("S " + (String) entry1.getKey());
				this.field_146609_h.add(this.fontRendererObj.trimStringToWidth((String) entry1.getValue(), this.width - 220));
			}
		}

		this.field_146606_s = new GuiSnooper.List();
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.field_146606_s.handleMouseInput();
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 2) {
				this.game_settings_2.saveOptions();
				this.game_settings_2.saveOptions();
				this.mc.displayGuiScreen(this.field_146608_a);
			}

			if (button.id == 1) {
				this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
				this.field_146605_t.displayString = this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
			}
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.field_146606_s.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRendererObj, this.field_146610_i, this.width / 2, 8, 16777215);
		int i = 22;

		for (String s : this.field_146607_r) {
			this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 8421504);
			i += this.fontRendererObj.FONT_HEIGHT;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	class List extends GuiSlot {
		public List() {
			super(GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRendererObj.FONT_HEIGHT + 1);
		}

		protected int getSize() {
			return GuiSnooper.this.field_146604_g.size();
		}

		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
		}

		protected boolean isSelected(int slotIndex) {
			return false;
		}

		protected void drawBackground() {
		}

		protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
			GuiSnooper.this.fontRendererObj.drawString((String) GuiSnooper.this.field_146604_g.get(entryID), 10, p_180791_3_, 16777215);
			GuiSnooper.this.fontRendererObj.drawString((String) GuiSnooper.this.field_146609_h.get(entryID), 230, p_180791_3_, 16777215);
		}

		protected int getScrollBarX() {
			return this.width - 10;
		}
	}
}
