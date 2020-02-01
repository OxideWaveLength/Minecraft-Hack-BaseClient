package optfine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

public class NaturalTextures
{
    private static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

    public static void update()
    {
        propertiesByIndex = new NaturalProperties[0];

        if (Config.isNaturalTextures())
        {
            String s = "optifine/natural.properties";

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (!Config.hasResource(resourcelocation))
                {
                    Config.dbg("NaturalTextures: configuration \"" + s + "\" not found");
                    propertiesByIndex = makeDefaultProperties();
                    return;
                }

                InputStream inputstream = Config.getResourceStream(resourcelocation);
                ArrayList arraylist = new ArrayList(256);
                String s1 = Config.readInputStream(inputstream);
                inputstream.close();
                String[] astring = Config.tokenize(s1, "\n\r");
                Config.dbg("Natural Textures: Parsing configuration \"" + s + "\"");
                TextureMap texturemap = TextureUtils.getTextureMapBlocks();

                for (int i = 0; i < astring.length; ++i)
                {
                    String s2 = astring[i].trim();

                    if (!s2.startsWith("#"))
                    {
                        String[] astring1 = Config.tokenize(s2, "=");

                        if (astring1.length != 2)
                        {
                            Config.warn("Natural Textures: Invalid \"" + s + "\" line: " + s2);
                        }
                        else
                        {
                            String s3 = astring1[0].trim();
                            String s4 = astring1[1].trim();
                            TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe("minecraft:blocks/" + s3);

                            if (textureatlassprite == null)
                            {
                                Config.warn("Natural Textures: Texture not found: \"" + s + "\" line: " + s2);
                            }
                            else
                            {
                                int j = textureatlassprite.getIndexInMap();

                                if (j < 0)
                                {
                                    Config.warn("Natural Textures: Invalid \"" + s + "\" line: " + s2);
                                }
                                else
                                {
                                    NaturalProperties naturalproperties = new NaturalProperties(s4);

                                    if (naturalproperties.isValid())
                                    {
                                        while (arraylist.size() <= j)
                                        {
                                            arraylist.add(null);
                                        }

                                        arraylist.set(j, naturalproperties);
                                        Config.dbg("NaturalTextures: " + s3 + " = " + s4);
                                    }
                                }
                            }
                        }
                    }
                }

                propertiesByIndex = (NaturalProperties[])((NaturalProperties[])arraylist.toArray(new NaturalProperties[arraylist.size()]));
            }
            catch (FileNotFoundException var16)
            {
                Config.warn("NaturalTextures: configuration \"" + s + "\" not found");
                propertiesByIndex = makeDefaultProperties();
                return;
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    public static BakedQuad getNaturalTexture(BlockPos p_getNaturalTexture_0_, BakedQuad p_getNaturalTexture_1_)
    {
        TextureAtlasSprite textureatlassprite = p_getNaturalTexture_1_.getSprite();

        if (textureatlassprite == null)
        {
            return p_getNaturalTexture_1_;
        }
        else
        {
            NaturalProperties naturalproperties = getNaturalProperties(textureatlassprite);

            if (naturalproperties == null)
            {
                return p_getNaturalTexture_1_;
            }
            else
            {
                int i = ConnectedTextures.getSide(p_getNaturalTexture_1_.getFace());
                int j = Config.getRandom(p_getNaturalTexture_0_, i);
                int k = 0;
                boolean flag = false;

                if (naturalproperties.rotation > 1)
                {
                    k = j & 3;
                }

                if (naturalproperties.rotation == 2)
                {
                    k = k / 2 * 2;
                }

                if (naturalproperties.flip)
                {
                    flag = (j & 4) != 0;
                }

                return naturalproperties.getQuad(p_getNaturalTexture_1_, k, flag);
            }
        }
    }

    public static NaturalProperties getNaturalProperties(TextureAtlasSprite p_getNaturalProperties_0_)
    {
        if (!(p_getNaturalProperties_0_ instanceof TextureAtlasSprite))
        {
            return null;
        }
        else
        {
            int i = p_getNaturalProperties_0_.getIndexInMap();

            if (i >= 0 && i < propertiesByIndex.length)
            {
                NaturalProperties naturalproperties = propertiesByIndex[i];
                return naturalproperties;
            }
            else
            {
                return null;
            }
        }
    }

    private static NaturalProperties[] makeDefaultProperties()
    {
        Config.dbg("NaturalTextures: Creating default configuration.");
        List list = new ArrayList();
        setIconProperties(list, "coarse_dirt", "4F");
        setIconProperties(list, "grass_side", "F");
        setIconProperties(list, "grass_side_overlay", "F");
        setIconProperties(list, "stone_slab_top", "F");
        setIconProperties(list, "gravel", "2");
        setIconProperties(list, "log_oak", "2F");
        setIconProperties(list, "log_spruce", "2F");
        setIconProperties(list, "log_birch", "F");
        setIconProperties(list, "log_jungle", "2F");
        setIconProperties(list, "log_acacia", "2F");
        setIconProperties(list, "log_big_oak", "2F");
        setIconProperties(list, "log_oak_top", "4F");
        setIconProperties(list, "log_spruce_top", "4F");
        setIconProperties(list, "log_birch_top", "4F");
        setIconProperties(list, "log_jungle_top", "4F");
        setIconProperties(list, "log_acacia_top", "4F");
        setIconProperties(list, "log_big_oak_top", "4F");
        setIconProperties(list, "leaves_oak", "2F");
        setIconProperties(list, "leaves_spruce", "2F");
        setIconProperties(list, "leaves_birch", "2F");
        setIconProperties(list, "leaves_jungle", "2");
        setIconProperties(list, "leaves_big_oak", "2F");
        setIconProperties(list, "leaves_acacia", "2F");
        setIconProperties(list, "gold_ore", "2F");
        setIconProperties(list, "iron_ore", "2F");
        setIconProperties(list, "coal_ore", "2F");
        setIconProperties(list, "diamond_ore", "2F");
        setIconProperties(list, "redstone_ore", "2F");
        setIconProperties(list, "lapis_ore", "2F");
        setIconProperties(list, "obsidian", "4F");
        setIconProperties(list, "snow", "4F");
        setIconProperties(list, "grass_side_snowed", "F");
        setIconProperties(list, "cactus_side", "2F");
        setIconProperties(list, "clay", "4F");
        setIconProperties(list, "mycelium_side", "F");
        setIconProperties(list, "mycelium_top", "4F");
        setIconProperties(list, "farmland_wet", "2F");
        setIconProperties(list, "farmland_dry", "2F");
        setIconProperties(list, "netherrack", "4F");
        setIconProperties(list, "soul_sand", "4F");
        setIconProperties(list, "glowstone", "4");
        setIconProperties(list, "end_stone", "4");
        setIconProperties(list, "sandstone_top", "4");
        setIconProperties(list, "sandstone_bottom", "4F");
        setIconProperties(list, "redstone_lamp_on", "4F");
        setIconProperties(list, "redstone_lamp_off", "4F");
        NaturalProperties[] anaturalproperties = (NaturalProperties[])((NaturalProperties[])list.toArray(new NaturalProperties[list.size()]));
        return anaturalproperties;
    }

    private static void setIconProperties(List p_setIconProperties_0_, String p_setIconProperties_1_, String p_setIconProperties_2_)
    {
        TextureMap texturemap = TextureUtils.getTextureMapBlocks();
        TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe("minecraft:blocks/" + p_setIconProperties_1_);

        if (textureatlassprite == null)
        {
            Config.warn("*** NaturalProperties: Icon not found: " + p_setIconProperties_1_ + " ***");
        }
        else if (!(textureatlassprite instanceof TextureAtlasSprite))
        {
            Config.warn("*** NaturalProperties: Icon is not IconStitched: " + p_setIconProperties_1_ + ": " + textureatlassprite.getClass().getName() + " ***");
        }
        else
        {
            int i = textureatlassprite.getIndexInMap();

            if (i < 0)
            {
                Config.warn("*** NaturalProperties: Invalid index for icon: " + p_setIconProperties_1_ + ": " + i + " ***");
            }
            else if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + p_setIconProperties_1_ + ".png")))
            {
                while (i >= p_setIconProperties_0_.size())
                {
                    p_setIconProperties_0_.add(null);
                }

                NaturalProperties naturalproperties = new NaturalProperties(p_setIconProperties_2_);
                p_setIconProperties_0_.set(i, naturalproperties);
                Config.dbg("NaturalTextures: " + p_setIconProperties_1_ + " = " + p_setIconProperties_2_);
            }
        }
    }
}
