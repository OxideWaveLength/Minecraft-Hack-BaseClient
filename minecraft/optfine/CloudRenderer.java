package optfine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CloudRenderer
{
    private Minecraft mc;
    private boolean updated = false;
    private boolean renderFancy = false;
    int cloudTickCounter;
    float partialTicks;
    private int glListClouds = -1;
    private int cloudTickCounterUpdate = 0;
    private double cloudPlayerX = 0.0D;
    private double cloudPlayerY = 0.0D;
    private double cloudPlayerZ = 0.0D;

    public CloudRenderer(Minecraft p_i25_1_)
    {
        this.mc = p_i25_1_;
        this.glListClouds = GLAllocation.generateDisplayLists(1);
    }

    public void prepareToRender(boolean p_prepareToRender_1_, int p_prepareToRender_2_, float p_prepareToRender_3_)
    {
        if (this.renderFancy != p_prepareToRender_1_)
        {
            this.updated = false;
        }

        this.renderFancy = p_prepareToRender_1_;
        this.cloudTickCounter = p_prepareToRender_2_;
        this.partialTicks = p_prepareToRender_3_;
    }

    public boolean shouldUpdateGlList()
    {
        if (!this.updated)
        {
            return true;
        }
        else if (this.cloudTickCounter >= this.cloudTickCounterUpdate + 20)
        {
            return true;
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            boolean flag = this.cloudPlayerY + (double)entity.getEyeHeight() < 128.0D + (double)(this.mc.gameSettings.ofCloudsHeight * 128.0F);
            boolean flag1 = entity.prevPosY + (double)entity.getEyeHeight() < 128.0D + (double)(this.mc.gameSettings.ofCloudsHeight * 128.0F);
            return flag1 != flag;
        }
    }

    public void startUpdateGlList()
    {
        GL11.glNewList(this.glListClouds, GL11.GL_COMPILE);
    }

    public void endUpdateGlList()
    {
        GL11.glEndList();
        this.cloudTickCounterUpdate = this.cloudTickCounter;
        this.cloudPlayerX = this.mc.getRenderViewEntity().prevPosX;
        this.cloudPlayerY = this.mc.getRenderViewEntity().prevPosY;
        this.cloudPlayerZ = this.mc.getRenderViewEntity().prevPosZ;
        this.updated = true;
        GlStateManager.resetColor();
    }

    public void renderGlList()
    {
        Entity entity = this.mc.getRenderViewEntity();
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)this.partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)this.partialTicks;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)this.partialTicks;
        double d3 = (double)((float)(this.cloudTickCounter - this.cloudTickCounterUpdate) + this.partialTicks);
        float f = (float)(d0 - this.cloudPlayerX + d3 * 0.03D);
        float f1 = (float)(d1 - this.cloudPlayerY);
        float f2 = (float)(d2 - this.cloudPlayerZ);
        GlStateManager.pushMatrix();

        if (this.renderFancy)
        {
            GlStateManager.translate(-f / 12.0F, -f1, -f2 / 12.0F);
        }
        else
        {
            GlStateManager.translate(-f, -f1, -f2);
        }

        GlStateManager.callList(this.glListClouds);
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    public void reset()
    {
        this.updated = false;
    }
}
