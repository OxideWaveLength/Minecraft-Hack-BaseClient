package net.minecraft.client.shader;

import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.JsonException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderLinkHelper
{
    private static final Logger logger = LogManager.getLogger();
    private static ShaderLinkHelper staticShaderLinkHelper;

    public static void setNewStaticShaderLinkHelper()
    {
        staticShaderLinkHelper = new ShaderLinkHelper();
    }

    public static ShaderLinkHelper getStaticShaderLinkHelper()
    {
        return staticShaderLinkHelper;
    }

    public void deleteShader(ShaderManager p_148077_1_)
    {
        p_148077_1_.getFragmentShaderLoader().deleteShader(p_148077_1_);
        p_148077_1_.getVertexShaderLoader().deleteShader(p_148077_1_);
        OpenGlHelper.glDeleteProgram(p_148077_1_.getProgram());
    }

    public int createProgram() throws JsonException
    {
        int i = OpenGlHelper.glCreateProgram();

        if (i <= 0)
        {
            throw new JsonException("Could not create shader program (returned program ID " + i + ")");
        }
        else
        {
            return i;
        }
    }

    public void linkProgram(ShaderManager manager) throws IOException
    {
        manager.getFragmentShaderLoader().attachShader(manager);
        manager.getVertexShaderLoader().attachShader(manager);
        OpenGlHelper.glLinkProgram(manager.getProgram());
        int i = OpenGlHelper.glGetProgrami(manager.getProgram(), OpenGlHelper.GL_LINK_STATUS);

        if (i == 0)
        {
            logger.warn("Error encountered when linking program containing VS " + manager.getVertexShaderLoader().getShaderFilename() + " and FS " + manager.getFragmentShaderLoader().getShaderFilename() + ". Log output:");
            logger.warn(OpenGlHelper.glGetProgramInfoLog(manager.getProgram(), 32768));
        }
    }
}
