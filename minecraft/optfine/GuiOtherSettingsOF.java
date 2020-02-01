package optfine;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiOtherSettingsOF extends GuiScreen implements GuiYesNoCallback
{
    private GuiScreen prevScreen;
    protected String title = "Other Settings";
    private GameSettings settings;
    private static GameSettings.Options[] enumOptions = new GameSettings.Options[] {GameSettings.Options.LAGOMETER, GameSettings.Options.PROFILER, GameSettings.Options.WEATHER, GameSettings.Options.TIME, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.FULLSCREEN_MODE, GameSettings.Options.SHOW_FPS, GameSettings.Options.AUTOSAVE_TICKS};
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public GuiOtherSettingsOF(GuiScreen p_i36_1_, GameSettings p_i36_2_)
    {
        this.prevScreen = p_i36_1_;
        this.settings = p_i36_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        int i = 0;

        for (GameSettings.Options gamesettings$options : enumOptions)
        {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 10;

            if (!gamesettings$options.getEnumFloat())
            {
                this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
            }
            else
            {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
            }

            ++i;
        }

        this.buttonList.add(new GuiButton(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, "Reset Video Settings..."));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id < 200 && button instanceof GuiOptionButton)
            {
                this.settings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
                button.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
            }

            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.prevScreen);
            }

            if (button.id == 210)
            {
                this.mc.gameSettings.saveOptions();
                GuiYesNo guiyesno = new GuiYesNo(this, "Reset all video settings to their default values?", "", 9999);
                this.mc.displayGuiScreen(guiyesno);
            }

            if (button.id != GameSettings.Options.CLOUD_HEIGHT.ordinal())
            {
                ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                this.setWorldAndResolution(this.mc, i, j);
            }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (result)
        {
            this.mc.gameSettings.resetSettings();
        }

        this.mc.displayGuiScreen(this);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (Math.abs(mouseX - this.lastMouseX) <= 5 && Math.abs(mouseY - this.lastMouseY) <= 5)
        {
            int i = 700;

            if (System.currentTimeMillis() >= this.mouseStillTime + (long)i)
            {
                int j = this.width / 2 - 150;
                int k = this.height / 6 - 5;

                if (mouseY <= k + 98)
                {
                    k += 105;
                }

                int l = j + 150 + 150;
                int i1 = k + 84 + 10;
                GuiButton guibutton = this.getSelectedButton(mouseX, mouseY);

                if (guibutton != null)
                {
                    String s = this.getButtonName(guibutton.displayString);
                    String[] astring = this.getTooltipLines(s);

                    if (astring == null)
                    {
                        return;
                    }

                    this.drawGradientRect(j, k, l, i1, -536870912, -536870912);

                    for (int j1 = 0; j1 < astring.length; ++j1)
                    {
                        String s1 = astring[j1];
                        this.fontRendererObj.drawStringWithShadow(s1, (float)(j + 5), (float)(k + 5 + j1 * 11), 14540253);
                    }
                }
            }
        }
        else
        {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private String[] getTooltipLines(String p_getTooltipLines_1_)
    {
        return p_getTooltipLines_1_.equals("Autosave") ? new String[] {"Autosave interval", "Default autosave interval (2s) is NOT RECOMMENDED.", "Autosave causes the famous Lag Spike of Death."}: (p_getTooltipLines_1_.equals("Lagometer") ? new String[] {"Shows the lagometer on the debug screen (F3).", "* Orange - Memory garbage collection", "* Cyan - Tick", "* Blue - Scheduled executables", "* Purple - Chunk upload", "* Red - Chunk updates", "* Yellow - Visibility check", "* Green - Render terrain"}: (p_getTooltipLines_1_.equals("Debug Profiler") ? new String[] {"Debug Profiler", "  ON - debug profiler is active, slower", "  OFF - debug profiler is not active, faster", "The debug profiler collects and shows debug information", "when the debug screen is open (F3)"}: (p_getTooltipLines_1_.equals("Time") ? new String[] {"Time", " Default - normal day/night cycles", " Day Only - day only", " Night Only - night only", "The time setting is only effective in CREATIVE mode", "and for local worlds."}: (p_getTooltipLines_1_.equals("Weather") ? new String[] {"Weather", "  ON - weather is active, slower", "  OFF - weather is not active, faster", "The weather controls rain, snow and thunderstorms.", "Weather control is only possible for local worlds."}: (p_getTooltipLines_1_.equals("Fullscreen") ? new String[] {"Fullscreen", "  ON - use fullscreen mode", "  OFF - use window mode", "Fullscreen mode may be faster or slower than", "window mode, depending on the graphics card."}: (p_getTooltipLines_1_.equals("Fullscreen Mode") ? new String[] {"Fullscreen mode", "  Default - use desktop screen resolution, slower", "  WxH - use custom screen resolution, may be faster", "The selected resolution is used in fullscreen mode (F11).", "Lower resolutions should generally be faster."}: (p_getTooltipLines_1_.equals("3D Anaglyph") ? new String[] {"3D mode used with red-cyan 3D glasses."}: (p_getTooltipLines_1_.equals("Show FPS") ? new String[] {"Shows compact FPS and render information", "  C: - chunk renderers", "  E: - rendered entities + block entities", "  U: - chunk updates", "The compact FPS information is only shown when the", "debug screen is not visible."}: null))))))));
    }

    private String getButtonName(String p_getButtonName_1_)
    {
        int i = p_getButtonName_1_.indexOf(58);
        return i < 0 ? p_getButtonName_1_ : p_getButtonName_1_.substring(0, i);
    }

    private GuiButton getSelectedButton(int p_getSelectedButton_1_, int p_getSelectedButton_2_)
    {
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            GuiButton guibutton = (GuiButton)this.buttonList.get(i);
            int j = GuiVideoSettings.getButtonWidth(guibutton);
            int k = GuiVideoSettings.getButtonHeight(guibutton);
            boolean flag = p_getSelectedButton_1_ >= guibutton.xPosition && p_getSelectedButton_2_ >= guibutton.yPosition && p_getSelectedButton_1_ < guibutton.xPosition + j && p_getSelectedButton_2_ < guibutton.yPosition + k;

            if (flag)
            {
                return guibutton;
            }
        }

        return null;
    }
}
