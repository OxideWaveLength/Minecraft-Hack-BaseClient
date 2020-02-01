package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack
{
    InputStream getInputStream(ResourceLocation location) throws IOException;

    boolean resourceExists(ResourceLocation location);

    Set<String> getResourceDomains();

    <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException;

    BufferedImage getPackImage() throws IOException;

    String getPackName();
}
