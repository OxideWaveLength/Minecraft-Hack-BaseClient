package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    private int field_146581_h;
    private List<String> field_146582_i;
    private int field_146579_r;
    private float field_146578_s = 0.5F;

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        MusicTicker musicticker = this.mc.func_181535_r();
        SoundHandler soundhandler = this.mc.getSoundHandler();

        if (this.field_146581_h == 0)
        {
            musicticker.func_181557_a();
            musicticker.func_181558_a(MusicTicker.MusicType.CREDITS);
            soundhandler.resumeSounds();
        }

        soundhandler.update();
        ++this.field_146581_h;
        float f = (float)(this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;

        if ((float)this.field_146581_h > f)
        {
            this.sendRespawnPacket();
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.sendRespawnPacket();
        }
    }

    private void sendRespawnPacket()
    {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        this.mc.displayGuiScreen((GuiScreen)null);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        if (this.field_146582_i == null)
        {
            this.field_146582_i = Lists.<String>newArrayList();

            try
            {
                String s = "";
                String s1 = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
                int i = 274;
                InputStream inputstream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, Charsets.UTF_8));
                Random random = new Random(8124371L);

                while ((s = bufferedreader.readLine()) != null)
                {
                    String s2;
                    String s3;

                    for (s = s.replaceAll("PLAYERNAME", this.mc.getSession().getUsername()); s.contains(s1); s = s2 + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + s3)
                    {
                        int j = s.indexOf(s1);
                        s2 = s.substring(0, j);
                        s3 = s.substring(j + s1.length());
                    }

                    this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(s, i));
                    this.field_146582_i.add("");
                }

                inputstream.close();

                for (int k = 0; k < 8; ++k)
                {
                    this.field_146582_i.add("");
                }

                inputstream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                bufferedreader = new BufferedReader(new InputStreamReader(inputstream, Charsets.UTF_8));

                while ((s = bufferedreader.readLine()) != null)
                {
                    s = s.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    s = s.replaceAll("\t", "    ");
                    this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(s, i));
                    this.field_146582_i.add("");
                }

                inputstream.close();
                this.field_146579_r = this.field_146582_i.size() * 12;
            }
            catch (Exception exception)
            {
                logger.error((String)"Couldn\'t load credits", (Throwable)exception);
            }
        }
    }

    private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int i = this.width;
        float f = 0.0F - ((float)this.field_146581_h + p_146575_3_) * 0.5F * this.field_146578_s;
        float f1 = (float)this.height - ((float)this.field_146581_h + p_146575_3_) * 0.5F * this.field_146578_s;
        float f2 = 0.015625F;
        float f3 = ((float)this.field_146581_h + p_146575_3_ - 0.0F) * 0.02F;
        float f4 = (float)(this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;
        float f5 = (f4 - 20.0F - ((float)this.field_146581_h + p_146575_3_)) * 0.005F;

        if (f5 < f3)
        {
            f3 = f5;
        }

        if (f3 > 1.0F)
        {
            f3 = 1.0F;
        }

        f3 = f3 * f3;
        f3 = f3 * 96.0F / 255.0F;
        worldrenderer.pos(0.0D, (double)this.height, (double)this.zLevel).tex(0.0D, (double)(f * f2)).color(f3, f3, f3, 1.0F).endVertex();
        worldrenderer.pos((double)i, (double)this.height, (double)this.zLevel).tex((double)((float)i * f2), (double)(f * f2)).color(f3, f3, f3, 1.0F).endVertex();
        worldrenderer.pos((double)i, 0.0D, (double)this.zLevel).tex((double)((float)i * f2), (double)(f1 * f2)).color(f3, f3, f3, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, (double)(f1 * f2)).color(f3, f3, f3, 1.0F).endVertex();
        tessellator.draw();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 274;
        int j = this.width / 2 - i / 2;
        int k = this.height + 50;
        float f = -((float)this.field_146581_h + partialTicks) * this.field_146578_s;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, f, 0.0F);
        this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(j, k, 0, 0, 155, 44);
        this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);
        int l = k + 200;

        for (int i1 = 0; i1 < this.field_146582_i.size(); ++i1)
        {
            if (i1 == this.field_146582_i.size() - 1)
            {
                float f1 = (float)l + f - (float)(this.height / 2 - 6);

                if (f1 < 0.0F)
                {
                    GlStateManager.translate(0.0F, -f1, 0.0F);
                }
            }

            if ((float)l + f + 12.0F + 8.0F > 0.0F && (float)l + f < (float)this.height)
            {
                String s = (String)this.field_146582_i.get(i1);

                if (s.startsWith("[C]"))
                {
                    this.fontRendererObj.drawStringWithShadow(s.substring(3), (float)(j + (i - this.fontRendererObj.getStringWidth(s.substring(3))) / 2), (float)l, 16777215);
                }
                else
                {
                    this.fontRendererObj.fontRandom.setSeed((long)i1 * 4238972211L + (long)(this.field_146581_h / 4));
                    this.fontRendererObj.drawStringWithShadow(s, (float)j, (float)l, 16777215);
                }
            }

            l += 12;
        }

        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0, 769);
        int j1 = this.width;
        int k1 = this.height;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)k1, (double)this.zLevel).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos((double)j1, (double)k1, (double)this.zLevel).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos((double)j1, 0.0D, (double)this.zLevel).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
