package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkProviderSettings;
import org.lwjgl.input.Keyboard;

public class GuiScreenCustomizePresets extends GuiScreen
{
    private static final List<GuiScreenCustomizePresets.Info> field_175310_f = Lists.<GuiScreenCustomizePresets.Info>newArrayList();
    private GuiScreenCustomizePresets.ListPreset field_175311_g;
    private GuiButton field_175316_h;
    private GuiTextField field_175317_i;
    private GuiCustomizeWorldScreen field_175314_r;
    protected String field_175315_a = "Customize World Presets";
    private String field_175313_s;
    private String field_175312_t;

    public GuiScreenCustomizePresets(GuiCustomizeWorldScreen p_i45524_1_)
    {
        this.field_175314_r = p_i45524_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.field_175315_a = I18n.format("createWorld.customize.custom.presets.title", new Object[0]);
        this.field_175313_s = I18n.format("createWorld.customize.presets.share", new Object[0]);
        this.field_175312_t = I18n.format("createWorld.customize.presets.list", new Object[0]);
        this.field_175317_i = new GuiTextField(2, this.fontRendererObj, 50, 40, this.width - 100, 20);
        this.field_175311_g = new GuiScreenCustomizePresets.ListPreset();
        this.field_175317_i.setMaxStringLength(2000);
        this.field_175317_i.setText(this.field_175314_r.func_175323_a());
        this.buttonList.add(this.field_175316_h = new GuiButton(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.format("createWorld.customize.presets.select", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.format("gui.cancel", new Object[0])));
        this.func_175304_a();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.field_175311_g.handleMouseInput();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.field_175317_i.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!this.field_175317_i.textboxKeyTyped(typedChar, keyCode))
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 0:
                this.field_175314_r.func_175324_a(this.field_175317_i.getText());
                this.mc.displayGuiScreen(this.field_175314_r);
                break;

            case 1:
                this.mc.displayGuiScreen(this.field_175314_r);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.field_175311_g.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.field_175315_a, this.width / 2, 8, 16777215);
        this.drawString(this.fontRendererObj, this.field_175313_s, 50, 30, 10526880);
        this.drawString(this.fontRendererObj, this.field_175312_t, 50, 70, 10526880);
        this.field_175317_i.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.field_175317_i.updateCursorCounter();
        super.updateScreen();
    }

    public void func_175304_a()
    {
        this.field_175316_h.enabled = this.func_175305_g();
    }

    private boolean func_175305_g()
    {
        return this.field_175311_g.field_178053_u > -1 && this.field_175311_g.field_178053_u < field_175310_f.size() || this.field_175317_i.getText().length() > 1;
    }

    static
    {
        ChunkProviderSettings.Factory chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{ \"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":8.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":0.5, \"biomeScaleWeight\":2.0, \"biomeScaleOffset\":0.375, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":255 }");
        ResourceLocation resourcelocation = new ResourceLocation("textures/gui/presets/water.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.waterWorld", new Object[0]), resourcelocation, chunkprovidersettings$factory));
        chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{\"coordinateScale\":3000.0, \"heightScale\":6000.0, \"upperLimitScale\":250.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }");
        resourcelocation = new ResourceLocation("textures/gui/presets/isles.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.isleLand", new Object[0]), resourcelocation, chunkprovidersettings$factory));
        chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":5.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":1.0, \"biomeScaleWeight\":4.0, \"biomeScaleOffset\":1.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }");
        resourcelocation = new ResourceLocation("textures/gui/presets/delight.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.caveDelight", new Object[0]), resourcelocation, chunkprovidersettings$factory));
        chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{\"coordinateScale\":738.41864, \"heightScale\":157.69133, \"upperLimitScale\":801.4267, \"lowerLimitScale\":1254.1643, \"depthNoiseScaleX\":374.93652, \"depthNoiseScaleZ\":288.65228, \"depthNoiseScaleExponent\":1.2092624, \"mainNoiseScaleX\":1355.9908, \"mainNoiseScaleY\":745.5343, \"mainNoiseScaleZ\":1183.464, \"baseSize\":1.8758626, \"stretchY\":1.7137525, \"biomeDepthWeight\":1.7553768, \"biomeDepthOffset\":3.4701107, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":2.535211, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }");
        resourcelocation = new ResourceLocation("textures/gui/presets/madness.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.mountains", new Object[0]), resourcelocation, chunkprovidersettings$factory));
        chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":1000.0, \"mainNoiseScaleY\":3000.0, \"mainNoiseScaleZ\":1000.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":20 }");
        resourcelocation = new ResourceLocation("textures/gui/presets/drought.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.drought", new Object[0]), resourcelocation, chunkprovidersettings$factory));
        chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":2.0, \"lowerLimitScale\":64.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":6 }");
        resourcelocation = new ResourceLocation("textures/gui/presets/chaos.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.caveChaos", new Object[0]), resourcelocation, chunkprovidersettings$factory));
        chunkprovidersettings$factory = ChunkProviderSettings.Factory.jsonToFactory("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":true, \"seaLevel\":40 }");
        resourcelocation = new ResourceLocation("textures/gui/presets/luck.png");
        field_175310_f.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.goodLuck", new Object[0]), resourcelocation, chunkprovidersettings$factory));
    }

    static class Info
    {
        public String field_178955_a;
        public ResourceLocation field_178953_b;
        public ChunkProviderSettings.Factory field_178954_c;

        public Info(String p_i45523_1_, ResourceLocation p_i45523_2_, ChunkProviderSettings.Factory p_i45523_3_)
        {
            this.field_178955_a = p_i45523_1_;
            this.field_178953_b = p_i45523_2_;
            this.field_178954_c = p_i45523_3_;
        }
    }

    class ListPreset extends GuiSlot
    {
        public int field_178053_u = -1;

        public ListPreset()
        {
            super(GuiScreenCustomizePresets.this.mc, GuiScreenCustomizePresets.this.width, GuiScreenCustomizePresets.this.height, 80, GuiScreenCustomizePresets.this.height - 32, 38);
        }

        protected int getSize()
        {
            return GuiScreenCustomizePresets.field_175310_f.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            this.field_178053_u = slotIndex;
            GuiScreenCustomizePresets.this.func_175304_a();
            GuiScreenCustomizePresets.this.field_175317_i.setText(((GuiScreenCustomizePresets.Info)GuiScreenCustomizePresets.field_175310_f.get(GuiScreenCustomizePresets.this.field_175311_g.field_178053_u)).field_178954_c.toString());
        }

        protected boolean isSelected(int slotIndex)
        {
            return slotIndex == this.field_178053_u;
        }

        protected void drawBackground()
        {
        }

        private void func_178051_a(int p_178051_1_, int p_178051_2_, ResourceLocation p_178051_3_)
        {
            int i = p_178051_1_ + 5;
            GuiScreenCustomizePresets.this.drawHorizontalLine(i - 1, i + 32, p_178051_2_ - 1, -2039584);
            GuiScreenCustomizePresets.this.drawHorizontalLine(i - 1, i + 32, p_178051_2_ + 32, -6250336);
            GuiScreenCustomizePresets.this.drawVerticalLine(i - 1, p_178051_2_ - 1, p_178051_2_ + 32, -2039584);
            GuiScreenCustomizePresets.this.drawVerticalLine(i + 32, p_178051_2_ - 1, p_178051_2_ + 32, -6250336);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(p_178051_3_);
            int j = 32;
            int k = 32;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos((double)(i + 0), (double)(p_178051_2_ + 32), 0.0D).tex(0.0D, 1.0D).endVertex();
            worldrenderer.pos((double)(i + 32), (double)(p_178051_2_ + 32), 0.0D).tex(1.0D, 1.0D).endVertex();
            worldrenderer.pos((double)(i + 32), (double)(p_178051_2_ + 0), 0.0D).tex(1.0D, 0.0D).endVertex();
            worldrenderer.pos((double)(i + 0), (double)(p_178051_2_ + 0), 0.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
        }

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
        {
            GuiScreenCustomizePresets.Info guiscreencustomizepresets$info = (GuiScreenCustomizePresets.Info)GuiScreenCustomizePresets.field_175310_f.get(entryID);
            this.func_178051_a(p_180791_2_, p_180791_3_, guiscreencustomizepresets$info.field_178953_b);
            GuiScreenCustomizePresets.this.fontRendererObj.drawString(guiscreencustomizepresets$info.field_178955_a, p_180791_2_ + 32 + 10, p_180791_3_ + 14, 16777215);
        }
    }
}
