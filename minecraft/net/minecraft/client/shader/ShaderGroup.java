package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderGroup
{
    private Framebuffer mainFramebuffer;
    private IResourceManager resourceManager;
    private String shaderGroupName;
    private final List<Shader> listShaders = Lists.<Shader>newArrayList();
    private final Map<String, Framebuffer> mapFramebuffers = Maps.<String, Framebuffer>newHashMap();
    private final List<Framebuffer> listFramebuffers = Lists.<Framebuffer>newArrayList();
    private Matrix4f projectionMatrix;
    private int mainFramebufferWidth;
    private int mainFramebufferHeight;
    private float field_148036_j;
    private float field_148037_k;

    public ShaderGroup(TextureManager p_i1050_1_, IResourceManager p_i1050_2_, Framebuffer p_i1050_3_, ResourceLocation p_i1050_4_) throws JsonException, IOException, JsonSyntaxException
    {
        this.resourceManager = p_i1050_2_;
        this.mainFramebuffer = p_i1050_3_;
        this.field_148036_j = 0.0F;
        this.field_148037_k = 0.0F;
        this.mainFramebufferWidth = p_i1050_3_.framebufferWidth;
        this.mainFramebufferHeight = p_i1050_3_.framebufferHeight;
        this.shaderGroupName = p_i1050_4_.toString();
        this.resetProjectionMatrix();
        this.parseGroup(p_i1050_1_, p_i1050_4_);
    }

    public void parseGroup(TextureManager p_152765_1_, ResourceLocation p_152765_2_) throws JsonException, IOException, JsonSyntaxException
    {
        JsonParser jsonparser = new JsonParser();
        InputStream inputstream = null;

        try
        {
            IResource iresource = this.resourceManager.getResource(p_152765_2_);
            inputstream = iresource.getInputStream();
            JsonObject jsonobject = jsonparser.parse(IOUtils.toString(inputstream, Charsets.UTF_8)).getAsJsonObject();

            if (JsonUtils.isJsonArray(jsonobject, "targets"))
            {
                JsonArray jsonarray = jsonobject.getAsJsonArray("targets");
                int i = 0;

                for (JsonElement jsonelement : jsonarray)
                {
                    try
                    {
                        this.initTarget(jsonelement);
                    }
                    catch (Exception exception1)
                    {
                        JsonException jsonexception1 = JsonException.func_151379_a(exception1);
                        jsonexception1.func_151380_a("targets[" + i + "]");
                        throw jsonexception1;
                    }

                    ++i;
                }
            }

            if (JsonUtils.isJsonArray(jsonobject, "passes"))
            {
                JsonArray jsonarray1 = jsonobject.getAsJsonArray("passes");
                int j = 0;

                for (JsonElement jsonelement1 : jsonarray1)
                {
                    try
                    {
                        this.parsePass(p_152765_1_, jsonelement1);
                    }
                    catch (Exception exception)
                    {
                        JsonException jsonexception2 = JsonException.func_151379_a(exception);
                        jsonexception2.func_151380_a("passes[" + j + "]");
                        throw jsonexception2;
                    }

                    ++j;
                }
            }
        }
        catch (Exception exception2)
        {
            JsonException jsonexception = JsonException.func_151379_a(exception2);
            jsonexception.func_151381_b(p_152765_2_.getResourcePath());
            throw jsonexception;
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }
    }

    private void initTarget(JsonElement p_148027_1_) throws JsonException
    {
        if (JsonUtils.isString(p_148027_1_))
        {
            this.addFramebuffer(p_148027_1_.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
        }
        else
        {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_148027_1_, "target");
            String s = JsonUtils.getString(jsonobject, "name");
            int i = JsonUtils.getInt(jsonobject, "width", this.mainFramebufferWidth);
            int j = JsonUtils.getInt(jsonobject, "height", this.mainFramebufferHeight);

            if (this.mapFramebuffers.containsKey(s))
            {
                throw new JsonException(s + " is already defined");
            }

            this.addFramebuffer(s, i, j);
        }
    }

    private void parsePass(TextureManager p_152764_1_, JsonElement p_152764_2_) throws JsonException, IOException
    {
        JsonObject jsonobject = JsonUtils.getJsonObject(p_152764_2_, "pass");
        String s = JsonUtils.getString(jsonobject, "name");
        String s1 = JsonUtils.getString(jsonobject, "intarget");
        String s2 = JsonUtils.getString(jsonobject, "outtarget");
        Framebuffer framebuffer = this.getFramebuffer(s1);
        Framebuffer framebuffer1 = this.getFramebuffer(s2);

        if (framebuffer == null)
        {
            throw new JsonException("Input target \'" + s1 + "\' does not exist");
        }
        else if (framebuffer1 == null)
        {
            throw new JsonException("Output target \'" + s2 + "\' does not exist");
        }
        else
        {
            Shader shader = this.addShader(s, framebuffer, framebuffer1);
            JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "auxtargets", (JsonArray)null);

            if (jsonarray != null)
            {
                int i = 0;

                for (JsonElement jsonelement : jsonarray)
                {
                    try
                    {
                        JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonelement, "auxtarget");
                        String s4 = JsonUtils.getString(jsonobject1, "name");
                        String s3 = JsonUtils.getString(jsonobject1, "id");
                        Framebuffer framebuffer2 = this.getFramebuffer(s3);

                        if (framebuffer2 == null)
                        {
                            ResourceLocation resourcelocation = new ResourceLocation("textures/effect/" + s3 + ".png");

                            try
                            {
                                this.resourceManager.getResource(resourcelocation);
                            }
                            catch (FileNotFoundException var24)
                            {
                                throw new JsonException("Render target or texture \'" + s3 + "\' does not exist");
                            }

                            p_152764_1_.bindTexture(resourcelocation);
                            ITextureObject itextureobject = p_152764_1_.getTexture(resourcelocation);
                            int j = JsonUtils.getInt(jsonobject1, "width");
                            int k = JsonUtils.getInt(jsonobject1, "height");
                            boolean flag = JsonUtils.getBoolean(jsonobject1, "bilinear");

                            if (flag)
                            {
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                            }
                            else
                            {
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                            }

                            shader.addAuxFramebuffer(s4, Integer.valueOf(itextureobject.getGlTextureId()), j, k);
                        }
                        else
                        {
                            shader.addAuxFramebuffer(s4, framebuffer2, framebuffer2.framebufferTextureWidth, framebuffer2.framebufferTextureHeight);
                        }
                    }
                    catch (Exception exception1)
                    {
                        JsonException jsonexception = JsonException.func_151379_a(exception1);
                        jsonexception.func_151380_a("auxtargets[" + i + "]");
                        throw jsonexception;
                    }

                    ++i;
                }
            }

            JsonArray jsonarray1 = JsonUtils.getJsonArray(jsonobject, "uniforms", (JsonArray)null);

            if (jsonarray1 != null)
            {
                int l = 0;

                for (JsonElement jsonelement1 : jsonarray1)
                {
                    try
                    {
                        this.initUniform(jsonelement1);
                    }
                    catch (Exception exception)
                    {
                        JsonException jsonexception1 = JsonException.func_151379_a(exception);
                        jsonexception1.func_151380_a("uniforms[" + l + "]");
                        throw jsonexception1;
                    }

                    ++l;
                }
            }
        }
    }

    private void initUniform(JsonElement p_148028_1_) throws JsonException
    {
        JsonObject jsonobject = JsonUtils.getJsonObject(p_148028_1_, "uniform");
        String s = JsonUtils.getString(jsonobject, "name");
        ShaderUniform shaderuniform = ((Shader)this.listShaders.get(this.listShaders.size() - 1)).getShaderManager().getShaderUniform(s);

        if (shaderuniform == null)
        {
            throw new JsonException("Uniform \'" + s + "\' does not exist");
        }
        else
        {
            float[] afloat = new float[4];
            int i = 0;

            for (JsonElement jsonelement : JsonUtils.getJsonArray(jsonobject, "values"))
            {
                try
                {
                    afloat[i] = JsonUtils.getFloat(jsonelement, "value");
                }
                catch (Exception exception)
                {
                    JsonException jsonexception = JsonException.func_151379_a(exception);
                    jsonexception.func_151380_a("values[" + i + "]");
                    throw jsonexception;
                }

                ++i;
            }

            switch (i)
            {
                case 0:
                default:
                    break;

                case 1:
                    shaderuniform.set(afloat[0]);
                    break;

                case 2:
                    shaderuniform.set(afloat[0], afloat[1]);
                    break;

                case 3:
                    shaderuniform.set(afloat[0], afloat[1], afloat[2]);
                    break;

                case 4:
                    shaderuniform.set(afloat[0], afloat[1], afloat[2], afloat[3]);
            }
        }
    }

    public Framebuffer getFramebufferRaw(String p_177066_1_)
    {
        return (Framebuffer)this.mapFramebuffers.get(p_177066_1_);
    }

    public void addFramebuffer(String p_148020_1_, int p_148020_2_, int p_148020_3_)
    {
        Framebuffer framebuffer = new Framebuffer(p_148020_2_, p_148020_3_, true);
        framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.mapFramebuffers.put(p_148020_1_, framebuffer);

        if (p_148020_2_ == this.mainFramebufferWidth && p_148020_3_ == this.mainFramebufferHeight)
        {
            this.listFramebuffers.add(framebuffer);
        }
    }

    public void deleteShaderGroup()
    {
        for (Framebuffer framebuffer : this.mapFramebuffers.values())
        {
            framebuffer.deleteFramebuffer();
        }

        for (Shader shader : this.listShaders)
        {
            shader.deleteShader();
        }

        this.listShaders.clear();
    }

    public Shader addShader(String p_148023_1_, Framebuffer p_148023_2_, Framebuffer p_148023_3_) throws JsonException, IOException
    {
        Shader shader = new Shader(this.resourceManager, p_148023_1_, p_148023_2_, p_148023_3_);
        this.listShaders.add(this.listShaders.size(), shader);
        return shader;
    }

    private void resetProjectionMatrix()
    {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.setIdentity();
        this.projectionMatrix.m00 = 2.0F / (float)this.mainFramebuffer.framebufferTextureWidth;
        this.projectionMatrix.m11 = 2.0F / (float)(-this.mainFramebuffer.framebufferTextureHeight);
        this.projectionMatrix.m22 = -0.0020001999F;
        this.projectionMatrix.m33 = 1.0F;
        this.projectionMatrix.m03 = -1.0F;
        this.projectionMatrix.m13 = 1.0F;
        this.projectionMatrix.m23 = -1.0001999F;
    }

    public void createBindFramebuffers(int width, int height)
    {
        this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
        this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
        this.resetProjectionMatrix();

        for (Shader shader : this.listShaders)
        {
            shader.setProjectionMatrix(this.projectionMatrix);
        }

        for (Framebuffer framebuffer : this.listFramebuffers)
        {
            framebuffer.createBindFramebuffer(width, height);
        }
    }

    public void loadShaderGroup(float partialTicks)
    {
        if (partialTicks < this.field_148037_k)
        {
            this.field_148036_j += 1.0F - this.field_148037_k;
            this.field_148036_j += partialTicks;
        }
        else
        {
            this.field_148036_j += partialTicks - this.field_148037_k;
        }

        for (this.field_148037_k = partialTicks; this.field_148036_j > 20.0F; this.field_148036_j -= 20.0F)
        {
            ;
        }

        for (Shader shader : this.listShaders)
        {
            shader.loadShader(this.field_148036_j / 20.0F);
        }
    }

    public final String getShaderGroupName()
    {
        return this.shaderGroupName;
    }

    private Framebuffer getFramebuffer(String p_148017_1_)
    {
        return p_148017_1_ == null ? null : (p_148017_1_.equals("minecraft:main") ? this.mainFramebuffer : (Framebuffer)this.mapFramebuffers.get(p_148017_1_));
    }
}
