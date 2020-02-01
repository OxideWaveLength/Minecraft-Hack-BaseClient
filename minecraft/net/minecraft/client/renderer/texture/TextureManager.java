package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import optfine.Config;
import optfine.RandomMobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements ITickable, IResourceManagerReloadListener
{
    private static final Logger logger = LogManager.getLogger();
    private final Map mapTextureObjects = Maps.newHashMap();
    private final List listTickables = Lists.newArrayList();
    private final Map mapTextureCounters = Maps.newHashMap();
    private IResourceManager theResourceManager;
    private static final String __OBFID = "CL_00001064";

    public TextureManager(IResourceManager resourceManager)
    {
        this.theResourceManager = resourceManager;
    }

    public void bindTexture(ResourceLocation resource)
    {
        if (Config.isRandomMobs())
        {
            resource = RandomMobs.getTextureLocation(resource);
        }

        Object object = (ITextureObject)this.mapTextureObjects.get(resource);

        if (object == null)
        {
            object = new SimpleTexture(resource);
            this.loadTexture(resource, (ITextureObject)object);
        }

        TextureUtil.bindTexture(((ITextureObject)object).getGlTextureId());
    }

    public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj)
    {
        if (this.loadTexture(textureLocation, textureObj))
        {
            this.listTickables.add(textureObj);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean loadTexture(ResourceLocation textureLocation, final ITextureObject textureObj)
    {
        boolean flag = true;
        ITextureObject itextureobject = textureObj;

        try
        {
            textureObj.loadTexture(this.theResourceManager);
        }
        catch (IOException ioexception)
        {
            logger.warn((String)("Failed to load texture: " + textureLocation), (Throwable)ioexception);
            itextureobject = TextureUtil.missingTexture;
            this.mapTextureObjects.put(textureLocation, itextureobject);
            flag = false;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", textureLocation);
            crashreportcategory.addCrashSectionCallable("Texture object class", new Callable()
            {
                private static final String __OBFID = "CL_00001065";
                public String call() throws Exception
                {
                    return textureObj.getClass().getName();
                }
            });
            throw new ReportedException(crashreport);
        }

        this.mapTextureObjects.put(textureLocation, itextureobject);
        return flag;
    }

    public ITextureObject getTexture(ResourceLocation textureLocation)
    {
        return (ITextureObject)this.mapTextureObjects.get(textureLocation);
    }

    public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture)
    {
        Integer integer = (Integer)this.mapTextureCounters.get(name);

        if (integer == null)
        {
            integer = Integer.valueOf(1);
        }
        else
        {
            integer = Integer.valueOf(integer.intValue() + 1);
        }

        this.mapTextureCounters.put(name, integer);
        ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", new Object[] {name, integer}));
        this.loadTexture(resourcelocation, texture);
        return resourcelocation;
    }

    public void tick()
    {
        for (Object itickable : this.listTickables)
        {
            ((ITickable) itickable).tick();
        }
    }

    public void deleteTexture(ResourceLocation textureLocation)
    {
        ITextureObject itextureobject = this.getTexture(textureLocation);

        if (itextureobject != null)
        {
            TextureUtil.deleteTexture(itextureobject.getGlTextureId());
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        Config.dbg("*** Reloading textures ***");
        Config.log("Resource packs: " + Config.getResourcePackNames());
        Iterator iterator = this.mapTextureObjects.keySet().iterator();

        while (iterator.hasNext())
        {
            ResourceLocation resourcelocation = (ResourceLocation)iterator.next();

            if (resourcelocation.getResourcePath().startsWith("mcpatcher/"))
            {
                ITextureObject itextureobject = (ITextureObject)this.mapTextureObjects.get(resourcelocation);

                if (itextureobject instanceof AbstractTexture)
                {
                    AbstractTexture abstracttexture = (AbstractTexture)itextureobject;
                    abstracttexture.deleteGlTexture();
                }

                iterator.remove();
            }
        }

        for (Object entry : this.mapTextureObjects.entrySet())
        {
            this.loadTexture((ResourceLocation)((Entry) entry).getKey(), (ITextureObject)((Entry) entry).getValue());
        }
    }
}
