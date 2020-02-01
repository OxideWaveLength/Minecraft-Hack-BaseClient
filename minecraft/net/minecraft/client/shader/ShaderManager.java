package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager
{
    private static final Logger logger = LogManager.getLogger();
    private static final ShaderDefault defaultShaderUniform = new ShaderDefault();
    private static ShaderManager staticShaderManager = null;
    private static int currentProgram = -1;
    private static boolean field_148000_e = true;
    private final Map<String, Object> shaderSamplers = Maps.<String, Object>newHashMap();
    private final List<String> samplerNames = Lists.<String>newArrayList();
    private final List<Integer> shaderSamplerLocations = Lists.<Integer>newArrayList();
    private final List<ShaderUniform> shaderUniforms = Lists.<ShaderUniform>newArrayList();
    private final List<Integer> shaderUniformLocations = Lists.<Integer>newArrayList();
    private final Map<String, ShaderUniform> mappedShaderUniforms = Maps.<String, ShaderUniform>newHashMap();
    private final int program;
    private final String programFilename;
    private final boolean useFaceCulling;
    private boolean isDirty;
    private final JsonBlendingMode field_148016_p;
    private final List<Integer> attribLocations;
    private final List<String> attributes;
    private final ShaderLoader vertexShaderLoader;
    private final ShaderLoader fragmentShaderLoader;

    public ShaderManager(IResourceManager resourceManager, String programName) throws JsonException, IOException
    {
        JsonParser jsonparser = new JsonParser();
        ResourceLocation resourcelocation = new ResourceLocation("shaders/program/" + programName + ".json");
        this.programFilename = programName;
        InputStream inputstream = null;

        try
        {
            inputstream = resourceManager.getResource(resourcelocation).getInputStream();
            JsonObject jsonobject = jsonparser.parse(IOUtils.toString(inputstream, Charsets.UTF_8)).getAsJsonObject();
            String s = JsonUtils.getString(jsonobject, "vertex");
            String s1 = JsonUtils.getString(jsonobject, "fragment");
            JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "samplers", (JsonArray)null);

            if (jsonarray != null)
            {
                int i = 0;

                for (JsonElement jsonelement : jsonarray)
                {
                    try
                    {
                        this.parseSampler(jsonelement);
                    }
                    catch (Exception exception2)
                    {
                        JsonException jsonexception1 = JsonException.func_151379_a(exception2);
                        jsonexception1.func_151380_a("samplers[" + i + "]");
                        throw jsonexception1;
                    }

                    ++i;
                }
            }

            JsonArray jsonarray1 = JsonUtils.getJsonArray(jsonobject, "attributes", (JsonArray)null);

            if (jsonarray1 != null)
            {
                int j = 0;
                this.attribLocations = Lists.<Integer>newArrayListWithCapacity(jsonarray1.size());
                this.attributes = Lists.<String>newArrayListWithCapacity(jsonarray1.size());

                for (JsonElement jsonelement1 : jsonarray1)
                {
                    try
                    {
                        this.attributes.add(JsonUtils.getString(jsonelement1, "attribute"));
                    }
                    catch (Exception exception1)
                    {
                        JsonException jsonexception2 = JsonException.func_151379_a(exception1);
                        jsonexception2.func_151380_a("attributes[" + j + "]");
                        throw jsonexception2;
                    }

                    ++j;
                }
            }
            else
            {
                this.attribLocations = null;
                this.attributes = null;
            }

            JsonArray jsonarray2 = JsonUtils.getJsonArray(jsonobject, "uniforms", (JsonArray)null);

            if (jsonarray2 != null)
            {
                int k = 0;

                for (JsonElement jsonelement2 : jsonarray2)
                {
                    try
                    {
                        this.parseUniform(jsonelement2);
                    }
                    catch (Exception exception)
                    {
                        JsonException jsonexception3 = JsonException.func_151379_a(exception);
                        jsonexception3.func_151380_a("uniforms[" + k + "]");
                        throw jsonexception3;
                    }

                    ++k;
                }
            }

            this.field_148016_p = JsonBlendingMode.func_148110_a(JsonUtils.getJsonObject(jsonobject, "blend", (JsonObject)null));
            this.useFaceCulling = JsonUtils.getBoolean(jsonobject, "cull", true);
            this.vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.VERTEX, s);
            this.fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.FRAGMENT, s1);
            this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
            ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
            this.setupUniforms();

            if (this.attributes != null)
            {
                for (String s2 : this.attributes)
                {
                    int l = OpenGlHelper.glGetAttribLocation(this.program, s2);
                    this.attribLocations.add(Integer.valueOf(l));
                }
            }
        }
        catch (Exception exception3)
        {
            JsonException jsonexception = JsonException.func_151379_a(exception3);
            jsonexception.func_151381_b(resourcelocation.getResourcePath());
            throw jsonexception;
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }

        this.markDirty();
    }

    public void deleteShader()
    {
        ShaderLinkHelper.getStaticShaderLinkHelper().deleteShader(this);
    }

    public void endShader()
    {
        OpenGlHelper.glUseProgram(0);
        currentProgram = -1;
        staticShaderManager = null;
        field_148000_e = true;

        for (int i = 0; i < this.shaderSamplerLocations.size(); ++i)
        {
            if (this.shaderSamplers.get(this.samplerNames.get(i)) != null)
            {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
                GlStateManager.bindTexture(0);
            }
        }
    }

    public void useShader()
    {
        this.isDirty = false;
        staticShaderManager = this;
        this.field_148016_p.func_148109_a();

        if (this.program != currentProgram)
        {
            OpenGlHelper.glUseProgram(this.program);
            currentProgram = this.program;
        }

        if (this.useFaceCulling)
        {
            GlStateManager.enableCull();
        }
        else
        {
            GlStateManager.disableCull();
        }

        for (int i = 0; i < this.shaderSamplerLocations.size(); ++i)
        {
            if (this.shaderSamplers.get(this.samplerNames.get(i)) != null)
            {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
                GlStateManager.enableTexture2D();
                Object object = this.shaderSamplers.get(this.samplerNames.get(i));
                int j = -1;

                if (object instanceof Framebuffer)
                {
                    j = ((Framebuffer)object).framebufferTexture;
                }
                else if (object instanceof ITextureObject)
                {
                    j = ((ITextureObject)object).getGlTextureId();
                }
                else if (object instanceof Integer)
                {
                    j = ((Integer)object).intValue();
                }

                if (j != -1)
                {
                    GlStateManager.bindTexture(j);
                    OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, (CharSequence)this.samplerNames.get(i)), i);
                }
            }
        }

        for (ShaderUniform shaderuniform : this.shaderUniforms)
        {
            shaderuniform.upload();
        }
    }

    public void markDirty()
    {
        this.isDirty = true;
    }

    /**
     * gets a shader uniform for the name given. null if not found.
     */
    public ShaderUniform getShaderUniform(String p_147991_1_)
    {
        return this.mappedShaderUniforms.containsKey(p_147991_1_) ? (ShaderUniform)this.mappedShaderUniforms.get(p_147991_1_) : null;
    }

    /**
     * gets a shader uniform for the name given. if not found, returns a default not-null value
     */
    public ShaderUniform getShaderUniformOrDefault(String p_147984_1_)
    {
        return (ShaderUniform)(this.mappedShaderUniforms.containsKey(p_147984_1_) ? (ShaderUniform)this.mappedShaderUniforms.get(p_147984_1_) : defaultShaderUniform);
    }

    /**
     * goes through the parsed uniforms and samplers and connects them to their GL counterparts.
     */
    private void setupUniforms()
    {
        int i = 0;

        for (int j = 0; i < this.samplerNames.size(); ++j)
        {
            String s = (String)this.samplerNames.get(i);
            int k = OpenGlHelper.glGetUniformLocation(this.program, s);

            if (k == -1)
            {
                logger.warn("Shader " + this.programFilename + "could not find sampler named " + s + " in the specified shader program.");
                this.shaderSamplers.remove(s);
                this.samplerNames.remove(j);
                --j;
            }
            else
            {
                this.shaderSamplerLocations.add(Integer.valueOf(k));
            }

            ++i;
        }

        for (ShaderUniform shaderuniform : this.shaderUniforms)
        {
            String s1 = shaderuniform.getShaderName();
            int l = OpenGlHelper.glGetUniformLocation(this.program, s1);

            if (l == -1)
            {
                logger.warn("Could not find uniform named " + s1 + " in the specified" + " shader program.");
            }
            else
            {
                this.shaderUniformLocations.add(Integer.valueOf(l));
                shaderuniform.setUniformLocation(l);
                this.mappedShaderUniforms.put(s1, shaderuniform);
            }
        }
    }

    private void parseSampler(JsonElement p_147996_1_) throws JsonException
    {
        JsonObject jsonobject = JsonUtils.getJsonObject(p_147996_1_, "sampler");
        String s = JsonUtils.getString(jsonobject, "name");

        if (!JsonUtils.isString(jsonobject, "file"))
        {
            this.shaderSamplers.put(s, (Object)null);
            this.samplerNames.add(s);
        }
        else
        {
            this.samplerNames.add(s);
        }
    }

    /**
     * adds a shader sampler texture. if it already exists, replaces it.
     */
    public void addSamplerTexture(String p_147992_1_, Object p_147992_2_)
    {
        if (this.shaderSamplers.containsKey(p_147992_1_))
        {
            this.shaderSamplers.remove(p_147992_1_);
        }

        this.shaderSamplers.put(p_147992_1_, p_147992_2_);
        this.markDirty();
    }

    private void parseUniform(JsonElement p_147987_1_) throws JsonException
    {
        JsonObject jsonobject = JsonUtils.getJsonObject(p_147987_1_, "uniform");
        String s = JsonUtils.getString(jsonobject, "name");
        int i = ShaderUniform.parseType(JsonUtils.getString(jsonobject, "type"));
        int j = JsonUtils.getInt(jsonobject, "count");
        float[] afloat = new float[Math.max(j, 16)];
        JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "values");

        if (jsonarray.size() != j && jsonarray.size() > 1)
        {
            throw new JsonException("Invalid amount of values specified (expected " + j + ", found " + jsonarray.size() + ")");
        }
        else
        {
            int k = 0;

            for (JsonElement jsonelement : jsonarray)
            {
                try
                {
                    afloat[k] = JsonUtils.getFloat(jsonelement, "value");
                }
                catch (Exception exception)
                {
                    JsonException jsonexception = JsonException.func_151379_a(exception);
                    jsonexception.func_151380_a("values[" + k + "]");
                    throw jsonexception;
                }

                ++k;
            }

            if (j > 1 && jsonarray.size() == 1)
            {
                while (k < j)
                {
                    afloat[k] = afloat[0];
                    ++k;
                }
            }

            int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
            ShaderUniform shaderuniform = new ShaderUniform(s, i + l, j, this);

            if (i <= 3)
            {
                shaderuniform.set((int)afloat[0], (int)afloat[1], (int)afloat[2], (int)afloat[3]);
            }
            else if (i <= 7)
            {
                shaderuniform.func_148092_b(afloat[0], afloat[1], afloat[2], afloat[3]);
            }
            else
            {
                shaderuniform.set(afloat);
            }

            this.shaderUniforms.add(shaderuniform);
        }
    }

    public ShaderLoader getVertexShaderLoader()
    {
        return this.vertexShaderLoader;
    }

    public ShaderLoader getFragmentShaderLoader()
    {
        return this.fragmentShaderLoader;
    }

    public int getProgram()
    {
        return this.program;
    }
}
