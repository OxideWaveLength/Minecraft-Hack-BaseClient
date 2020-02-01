package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry
{
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final GuiMultiplayer field_148303_c;
    private final Minecraft mc;
    private final ServerData field_148301_e;
    private final ResourceLocation field_148306_i;
    private String field_148299_g;
    private DynamicTexture field_148305_h;
    private long field_148298_f;

    protected ServerListEntryNormal(GuiMultiplayer p_i45048_1_, ServerData p_i45048_2_)
    {
        this.field_148303_c = p_i45048_1_;
        this.field_148301_e = p_i45048_2_;
        this.mc = Minecraft.getMinecraft();
        this.field_148306_i = new ResourceLocation("servers/" + p_i45048_2_.serverIP + "/icon");
        this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.field_148306_i);
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        if (!this.field_148301_e.field_78841_f)
        {
            this.field_148301_e.field_78841_f = true;
            this.field_148301_e.pingToServer = -2L;
            this.field_148301_e.serverMOTD = "";
            this.field_148301_e.populationInfo = "";
            field_148302_b.submit(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        ServerListEntryNormal.this.field_148303_c.getOldServerPinger().ping(ServerListEntryNormal.this.field_148301_e);
                    }
                    catch (UnknownHostException var2)
                    {
                        ServerListEntryNormal.this.field_148301_e.pingToServer = -1L;
                        ServerListEntryNormal.this.field_148301_e.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
                    }
                    catch (Exception var3)
                    {
                        ServerListEntryNormal.this.field_148301_e.pingToServer = -1L;
                        ServerListEntryNormal.this.field_148301_e.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
                    }
                }
            });
        }

        boolean flag = this.field_148301_e.version > 47;
        boolean flag1 = this.field_148301_e.version < 47;
        boolean flag2 = flag || flag1;
        this.mc.fontRendererObj.drawString(this.field_148301_e.serverName, x + 32 + 3, y + 1, 16777215);
        List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.field_148301_e.serverMOTD, listWidth - 32 - 2);

        for (int i = 0; i < Math.min(list.size(), 2); ++i)
        {
            this.mc.fontRendererObj.drawString((String)list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504);
        }

        String s2 = flag2 ? EnumChatFormatting.DARK_RED + this.field_148301_e.gameVersion : this.field_148301_e.populationInfo;
        int j = this.mc.fontRendererObj.getStringWidth(s2);
        this.mc.fontRendererObj.drawString(s2, x + listWidth - j - 15 - 2, y + 1, 8421504);
        int k = 0;
        String s = null;
        int l;
        String s1;

        if (flag2)
        {
            l = 5;
            s1 = flag ? "Client out of date!" : "Server out of date!";
            s = this.field_148301_e.playerList;
        }
        else if (this.field_148301_e.field_78841_f && this.field_148301_e.pingToServer != -2L)
        {
            if (this.field_148301_e.pingToServer < 0L)
            {
                l = 5;
            }
            else if (this.field_148301_e.pingToServer < 150L)
            {
                l = 0;
            }
            else if (this.field_148301_e.pingToServer < 300L)
            {
                l = 1;
            }
            else if (this.field_148301_e.pingToServer < 600L)
            {
                l = 2;
            }
            else if (this.field_148301_e.pingToServer < 1000L)
            {
                l = 3;
            }
            else
            {
                l = 4;
            }

            if (this.field_148301_e.pingToServer < 0L)
            {
                s1 = "(no connection)";
            }
            else
            {
                s1 = this.field_148301_e.pingToServer + "ms";
                s = this.field_148301_e.playerList;
            }
        }
        else
        {
            k = 1;
            l = (int)(Minecraft.getSystemTime() / 100L + (long)(slotIndex * 2) & 7L);

            if (l > 4)
            {
                l = 8 - l;
            }

            s1 = "Pinging...";
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256.0F, 256.0F);

        if (this.field_148301_e.getBase64EncodedIconData() != null && !this.field_148301_e.getBase64EncodedIconData().equals(this.field_148299_g))
        {
            this.field_148299_g = this.field_148301_e.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.field_148303_c.getServerList().saveServerList();
        }

        if (this.field_148305_h != null)
        {
            this.func_178012_a(x, y, this.field_148306_i);
        }
        else
        {
            this.func_178012_a(x, y, UNKNOWN_SERVER);
        }

        int i1 = mouseX - x;
        int j1 = mouseY - y;

        if (i1 >= listWidth - 15 && i1 <= listWidth - 5 && j1 >= 0 && j1 <= 8)
        {
            this.field_148303_c.setHoveringText(s1);
        }
        else if (i1 >= listWidth - j - 15 - 2 && i1 <= listWidth - 15 - 2 && j1 >= 0 && j1 <= 8)
        {
            this.field_148303_c.setHoveringText(s);
        }

        if (this.mc.gameSettings.touchscreen || isSelected)
        {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = mouseX - x;
            int l1 = mouseY - y;

            if (this.func_178013_b())
            {
                if (k1 < 32 && k1 > 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.field_148303_c.func_175392_a(this, slotIndex))
            {
                if (k1 < 16 && l1 < 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.field_148303_c.func_175394_b(this, slotIndex))
            {
                if (k1 < 16 && l1 > 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }

    protected void func_178012_a(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_)
    {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b()
    {
        return true;
    }

    private void prepareServerIcon()
    {
        if (this.field_148301_e.getBase64EncodedIconData() == null)
        {
            this.mc.getTextureManager().deleteTexture(this.field_148306_i);
            this.field_148305_h = null;
        }
        else
        {
            ByteBuf bytebuf = Unpooled.copiedBuffer((CharSequence)this.field_148301_e.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf bytebuf1 = Base64.decode(bytebuf);
            BufferedImage bufferedimage;
            label101:
            {
                try
                {
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break label101;
                }
                catch (Throwable throwable)
                {
                    logger.error("Invalid icon for server " + this.field_148301_e.serverName + " (" + this.field_148301_e.serverIP + ")", throwable);
                    this.field_148301_e.setBase64EncodedIconData((String)null);
                }
                finally
                {
                    bytebuf.release();
                    bytebuf1.release();
                }

                return;
            }

            if (this.field_148305_h == null)
            {
                this.field_148305_h = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.mc.getTextureManager().loadTexture(this.field_148306_i, this.field_148305_h);
            }

            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.field_148305_h.getTextureData(), 0, bufferedimage.getWidth());
            this.field_148305_h.updateDynamicTexture();
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        if (p_148278_5_ <= 32)
        {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b())
            {
                this.field_148303_c.selectServer(slotIndex);
                this.field_148303_c.connectToSelected();
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.field_148303_c.func_175392_a(this, slotIndex))
            {
                this.field_148303_c.func_175391_a(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.field_148303_c.func_175394_b(this, slotIndex))
            {
                this.field_148303_c.func_175393_b(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }
        }

        this.field_148303_c.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.field_148298_f < 250L)
        {
            this.field_148303_c.connectToSelected();
        }

        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }

    public ServerData getServerData()
    {
        return this.field_148301_e;
    }
}
