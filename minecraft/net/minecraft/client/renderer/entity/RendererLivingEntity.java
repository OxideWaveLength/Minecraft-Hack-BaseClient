package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T>
{
    private static final Logger logger = LogManager.getLogger();
    private static final DynamicTexture field_177096_e = new DynamicTexture(16, 16);
    protected ModelBase mainModel;
    protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
    protected List<LayerRenderer<T>> layerRenderers = Lists.<LayerRenderer<T>>newArrayList();
    protected boolean renderOutlines = false;

    public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn);
        this.mainModel = modelBaseIn;
        this.shadowSize = shadowSizeIn;
    }

    protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer)
    {
        return this.layerRenderers.add((LayerRenderer<T>)layer);
    }

    protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer)
    {
        return this.layerRenderers.remove(layer);
    }

    public ModelBase getMainModel()
    {
        return this.mainModel;
    }

    /**
     * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
     * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
     * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
     */
    protected float interpolateRotation(float par1, float par2, float par3)
    {
        float f;

        for (f = par2 - par1; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return par1 + par3 * f;
    }

    public void transformHeldFull3DItemLayer()
    {
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = entity.isRiding();
        this.mainModel.isChild = entity.isChild();

        try
        {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;

            if (entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity.ridingEntity;
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f2 = f1 - f;
                float f3 = MathHelper.wrapAngleTo180_float(f2);

                if (f3 < -85.0F)
                {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F)
                {
                    f3 = 85.0F;
                }

                f = f1 - f3;

                if (f3 * f3 > 2500.0F)
                {
                    f += f3 * 0.2F;
                }
            }

            float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f8 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f8, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entity, partialTicks);
            float f4 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

            if (entity.isChild())
            {
                f6 *= 3.0F;
            }

            if (f5 > 1.0F)
            {
                f5 = 1.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625F, entity);

            if (this.renderOutlines)
            {
                boolean flag1 = this.setScoreTeamColor(entity);
                this.renderModel(entity, f6, f5, f8, f2, f7, 0.0625F);

                if (flag1)
                {
                    this.unsetScoreTeamColor();
                }
            }
            else
            {
                boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModel(entity, f6, f5, f8, f2, f7, 0.0625F);

                if (flag)
                {
                    this.unsetBrightness();
                }

                GlStateManager.depthMask(true);

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                {
                    this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, 0.0625F);
                }
            }

            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception)
        {
            logger.error((String)"Couldn\'t render entity", (Throwable)exception);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        if (!this.renderOutlines)
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    protected boolean setScoreTeamColor(T entityLivingBaseIn)
    {
        int i = 16777215;

        if (entityLivingBaseIn instanceof EntityPlayer)
        {
            ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)entityLivingBaseIn.getTeam();

            if (scoreplayerteam != null)
            {
                String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());

                if (s.length() >= 2)
                {
                    i = this.getFontRendererFromRenderManager().getColorCode(s.charAt(1));
                }
            }
        }

        float f1 = (float)(i >> 16 & 255) / 255.0F;
        float f2 = (float)(i >> 8 & 255) / 255.0F;
        float f = (float)(i & 255) / 255.0F;
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(f1, f2, f, 1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        return true;
    }

    protected void unsetScoreTeamColor()
    {
        GlStateManager.enableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_)
    {
        boolean flag = !entitylivingbaseIn.isInvisible();
        boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);

        if (flag || flag1)
        {
            if (!this.bindEntityTexture(entitylivingbaseIn))
            {
                return;
            }

            if (flag1)
            {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);

            if (flag1)
            {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }

    protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks)
    {
        return this.setBrightness(entityLivingBaseIn, partialTicks, true);
    }

    protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures)
    {
        float f = entitylivingbaseIn.getBrightness(partialTicks);
        int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
        boolean flag = (i >> 24 & 255) > 0;
        boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;

        if (!flag && !flag1)
        {
            return false;
        }
        else if (!flag && !combineTextures)
        {
            return false;
        }
        else
        {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            this.brightnessBuffer.position(0);

            if (flag1)
            {
                this.brightnessBuffer.put(1.0F);
                this.brightnessBuffer.put(0.0F);
                this.brightnessBuffer.put(0.0F);
                this.brightnessBuffer.put(0.3F);
            }
            else
            {
                float f1 = (float)(i >> 24 & 255) / 255.0F;
                float f2 = (float)(i >> 16 & 255) / 255.0F;
                float f3 = (float)(i >> 8 & 255) / 255.0F;
                float f4 = (float)(i & 255) / 255.0F;
                this.brightnessBuffer.put(f2);
                this.brightnessBuffer.put(f3);
                this.brightnessBuffer.put(f4);
                this.brightnessBuffer.put(1.0F - f1);
            }

            this.brightnessBuffer.flip();
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, (FloatBuffer)this.brightnessBuffer);
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(field_177096_e.getGlTextureId());
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            return true;
        }
    }

    protected void unsetBrightness()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture(0);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z)
    {
        GlStateManager.translate((float)x, (float)y, (float)z);
    }

    protected void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);

        if (bat.deathTime > 0)
        {
            float f = ((float)bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt_float(f);

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            GlStateManager.rotate(f * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
        }
        else
        {
            String s = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName());

            if (s != null && (s.equals("Dinnerbone") || s.equals("Grumm")) && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE)))
            {
                GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
     */
    protected float getSwingProgress(T livingBase, float partialTickTime)
    {
        return livingBase.getSwingProgress(partialTickTime);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(T livingBase, float partialTicks)
    {
        return (float)livingBase.ticksExisted + partialTicks;
    }

    protected void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_)
    {
        for (LayerRenderer<T> layerrenderer : this.layerRenderers)
        {
            boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
            layerrenderer.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);

            if (flag)
            {
                this.unsetBrightness();
            }
        }
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn)
    {
        return 90.0F;
    }

    /**
     * Returns an ARGB int color back. Args: entityLiving, lightBrightness, partialTickTime
     */
    protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        return 0;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime)
    {
    }

    public void renderName(T entity, double x, double y, double z)
    {
        if (this.canRenderName(entity))
        {
            double d0 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f = entity.isSneaking() ? 32.0F : 64.0F;

            if (d0 < (double)(f * f))
            {
                String s = entity.getDisplayName().getFormattedText();
                float f1 = 0.02666667F;
                GlStateManager.alphaFunc(516, 0.1F);

                if (entity.isSneaking())
                {
                    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float)z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                    GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    int i = fontrenderer.getStringWidth(s) / 2;
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldrenderer.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
                else
                {
                    this.renderOffsetLivingLabel(entity, x, y - (entity.isChild() ? (double)(entity.height / 2.0F) : 0.0D), z, s, 0.02666667F, d0);
                }
            }
        }
    }

    protected boolean canRenderName(T entity)
    {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;

        if (entity instanceof EntityPlayer && entity != entityplayersp)
        {
            Team team = entity.getTeam();
            Team team1 = entityplayersp.getTeam();

            if (team != null)
            {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible)
                {
                    case ALWAYS:
                        return true;

                    case NEVER:
                        return false;

                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null || team.isSameTeam(team1);

                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null || !team.isSameTeam(team1);

                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer && !entity.isInvisibleToPlayer(entityplayersp) && entity.riddenByEntity == null;
    }

    public void setRenderOutlines(boolean renderOutlinesIn)
    {
        this.renderOutlines = renderOutlinesIn;
    }

    static
    {
        int[] aint = field_177096_e.getTextureData();

        for (int i = 0; i < 256; ++i)
        {
            aint[i] = -1;
        }

        field_177096_e.updateDynamicTexture();
    }
}
