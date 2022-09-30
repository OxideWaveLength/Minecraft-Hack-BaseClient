package me.wavelength.baseclient.viamcp.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.Collections;

import me.wavelength.baseclient.viamcp.ViaMCP;
import me.wavelength.baseclient.viamcp.protocols.ProtocolCollection;

public class AsyncVersionSlider extends GuiButton
{
    private float dragValue = (float) (ProtocolCollection.values().length - Arrays.asList(ProtocolCollection.values()).indexOf(ProtocolCollection.getProtocolCollectionById(ViaMCP.PROTOCOL_VERSION))) / ProtocolCollection.values().length;

    private final ProtocolCollection[] values;
    private float sliderValue;
    public boolean dragging;

    public AsyncVersionSlider(int buttonId, int x, int y , int widthIn, int heightIn)
    {
        super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
        this.values = ProtocolCollection.values();
        Collections.reverse(Arrays.asList(values));
        this.sliderValue = dragValue;
        this.displayString = values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName();
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        super.drawButton(mc, mouseX, mouseY);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                this.dragValue = sliderValue;
                this.displayString = values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName();
                ViaMCP.getInstance().setVersion(values[(int) (this.sliderValue * (values.length - 1))].getVersion().getVersion());
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.dragValue = sliderValue;
            this.displayString = values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName();
            ViaMCP.getInstance().setVersion(values[(int) (this.sliderValue * (values.length - 1))].getVersion().getVersion());
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
        this.dragging = false;
    }

    public void setVersion(int protocol)
    {
        this.dragValue = (float) (ProtocolCollection.values().length - Arrays.asList(ProtocolCollection.values()).indexOf(ProtocolCollection.getProtocolCollectionById(protocol))) / ProtocolCollection.values().length;
        this.sliderValue = this.dragValue;
        this.displayString = values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName();
    }
}
