package optfine;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiPerformanceSettingsOF extends GuiScreen
{
    private GuiScreen prevScreen;
    protected String title = "Performance Settings";
    private GameSettings settings;
    private static GameSettings.Options[] enumOptions = new GameSettings.Options[] {GameSettings.Options.SMOOTH_FPS, GameSettings.Options.SMOOTH_WORLD, GameSettings.Options.FAST_RENDER, GameSettings.Options.FAST_MATH, GameSettings.Options.CHUNK_UPDATES, GameSettings.Options.CHUNK_UPDATES_DYNAMIC, GameSettings.Options.LAZY_CHUNK_LOADING};
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public GuiPerformanceSettingsOF(GuiScreen p_i37_1_, GameSettings p_i37_2_)
    {
        this.prevScreen = p_i37_1_;
        this.settings = p_i37_2_;
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

            if (button.id != GameSettings.Options.CLOUD_HEIGHT.ordinal())
            {
                ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                this.setWorldAndResolution(this.mc, i, j);
            }
        }
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
        return p_getTooltipLines_1_.equals("Smooth FPS") ? new String[] {"Stabilizes FPS by flushing the graphic driver buffers", "  OFF - no stabilization, FPS may fluctuate", "  ON - FPS stabilization", "This option is graphics driver dependant and its effect", "is not always visible"}: (p_getTooltipLines_1_.equals("Smooth World") ? new String[] {"Removes lag spikes caused by the internal server.", "  OFF - no stabilization, FPS may fluctuate", "  ON - FPS stabilization", "Stabilizes FPS by distributing the internal server load.", "Effective only for local worlds (single player)."}: (p_getTooltipLines_1_.equals("Load Far") ? new String[] {"Loads the world chunks at distance Far.", "Switching the render distance does not cause all chunks ", "to be loaded again.", "  OFF - world chunks loaded up to render distance", "  ON - world chunks loaded at distance Far, allows", "       fast render distance switching"}: (p_getTooltipLines_1_.equals("Preloaded Chunks") ? new String[] {"Defines an area in which no chunks will be loaded", "  OFF - after 5m new chunks will be loaded", "  2 - after 32m  new chunks will be loaded", "  8 - after 128m new chunks will be loaded", "Higher values need more time to load all the chunks", "and may decrease the FPS."}: (p_getTooltipLines_1_.equals("Chunk Updates") ? new String[] {"Chunk updates", " 1 - slower world loading, higher FPS (default)", " 3 - faster world loading, lower FPS", " 5 - fastest world loading, lowest FPS", "Number of chunk updates per rendered frame,", "higher values may destabilize the framerate."}: (p_getTooltipLines_1_.equals("Dynamic Updates") ? new String[] {"Dynamic chunk updates", " OFF - (default) standard chunk updates per frame", " ON - more updates while the player is standing still", "Dynamic updates force more chunk updates while", "the player is standing still to load the world faster."}: (p_getTooltipLines_1_.equals("Lazy Chunk Loading") ? new String[] {"Lazy Chunk Loading", " OFF - default server chunk loading", " ON - lazy server chunk loading (smoother)", "Smooths the integrated server chunk loading by", "distributing the chunks over several ticks.", "Turn it OFF if parts of the world do not load correctly.", "Effective only for local worlds and single-core CPU."}: (p_getTooltipLines_1_.equals("Fast Math") ? new String[] {"Fast Math", " OFF - standard math (default)", " ON - faster math", "Uses optimized sin() and cos() functions which can", "better utilize the CPU cache and increase the FPS."}: (p_getTooltipLines_1_.equals("Fast Render") ? new String[] {"Fast Render", " OFF - standard rendering (default)", " ON - optimized rendering (faster)", "Uses optimized rendering algorithm which decreases", "the GPU load and may substantionally increase the FPS."}: null))))))));
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
