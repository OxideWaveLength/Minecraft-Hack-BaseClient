package optfine;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class CustomColorizer
{
    private static int[] grassColors = null;
    private static int[] waterColors = null;
    private static int[] foliageColors = null;
    private static int[] foliagePineColors = null;
    private static int[] foliageBirchColors = null;
    private static int[] swampFoliageColors = null;
    private static int[] swampGrassColors = null;
    private static int[][] blockPalettes = (int[][])null;
    private static int[][] paletteColors = (int[][])null;
    private static int[] skyColors = null;
    private static int[] fogColors = null;
    private static int[] underwaterColors = null;
    private static float[][][] lightMapsColorsRgb = (float[][][])null;
    private static int[] lightMapsHeight = null;
    private static float[][] sunRgbs = new float[16][3];
    private static float[][] torchRgbs = new float[16][3];
    private static int[] redstoneColors = null;
    private static int[] stemColors = null;
    private static int[] myceliumParticleColors = null;
    private static boolean useDefaultColorMultiplier = true;
    private static int particleWaterColor = -1;
    private static int particlePortalColor = -1;
    private static int lilyPadColor = -1;
    private static Vec3 fogColorNether = null;
    private static Vec3 fogColorEnd = null;
    private static Vec3 skyColorEnd = null;
    private static final int TYPE_NONE = 0;
    private static final int TYPE_GRASS = 1;
    private static final int TYPE_FOLIAGE = 2;
    private static Random random = new Random();

    public static void update()
    {
        grassColors = null;
        waterColors = null;
        foliageColors = null;
        foliageBirchColors = null;
        foliagePineColors = null;
        swampGrassColors = null;
        swampFoliageColors = null;
        skyColors = null;
        fogColors = null;
        underwaterColors = null;
        redstoneColors = null;
        stemColors = null;
        myceliumParticleColors = null;
        lightMapsColorsRgb = (float[][][])null;
        lightMapsHeight = null;
        lilyPadColor = -1;
        particleWaterColor = -1;
        particlePortalColor = -1;
        fogColorNether = null;
        fogColorEnd = null;
        skyColorEnd = null;
        blockPalettes = (int[][])null;
        paletteColors = (int[][])null;
        useDefaultColorMultiplier = true;
        String s = "mcpatcher/colormap/";
        grassColors = getCustomColors("textures/colormap/grass.png", 65536);
        foliageColors = getCustomColors("textures/colormap/foliage.png", 65536);
        String[] astring = new String[] {"water.png", "watercolorX.png"};
        waterColors = getCustomColors(s, astring, 65536);

        if (Config.isCustomColors())
        {
            String[] astring1 = new String[] {"pine.png", "pinecolor.png"};
            foliagePineColors = getCustomColors(s, astring1, 65536);
            String[] astring2 = new String[] {"birch.png", "birchcolor.png"};
            foliageBirchColors = getCustomColors(s, astring2, 65536);
            String[] astring3 = new String[] {"swampgrass.png", "swampgrasscolor.png"};
            swampGrassColors = getCustomColors(s, astring3, 65536);
            String[] astring4 = new String[] {"swampfoliage.png", "swampfoliagecolor.png"};
            swampFoliageColors = getCustomColors(s, astring4, 65536);
            String[] astring5 = new String[] {"sky0.png", "skycolor0.png"};
            skyColors = getCustomColors(s, astring5, 65536);
            String[] astring6 = new String[] {"fog0.png", "fogcolor0.png"};
            fogColors = getCustomColors(s, astring6, 65536);
            String[] astring7 = new String[] {"underwater.png", "underwatercolor.png"};
            underwaterColors = getCustomColors(s, astring7, 65536);
            String[] astring8 = new String[] {"redstone.png", "redstonecolor.png"};
            redstoneColors = getCustomColors(s, astring8, 16);
            String[] astring9 = new String[] {"stem.png", "stemcolor.png"};
            stemColors = getCustomColors(s, astring9, 8);
            String[] astring10 = new String[] {"myceliumparticle.png", "myceliumparticlecolor.png"};
            myceliumParticleColors = getCustomColors(s, astring10, -1);
            int[][] aint = new int[3][];
            lightMapsColorsRgb = new float[3][][];
            lightMapsHeight = new int[3];

            for (int i = 0; i < aint.length; ++i)
            {
                String s1 = "mcpatcher/lightmap/world" + (i - 1) + ".png";
                aint[i] = getCustomColors(s1, -1);

                if (aint[i] != null)
                {
                    lightMapsColorsRgb[i] = toRgb(aint[i]);
                }

                lightMapsHeight[i] = getTextureHeight(s1, 32);
            }

            readColorProperties("mcpatcher/color.properties");
            updateUseDefaultColorMultiplier();
        }
    }

    private static int getTextureHeight(String p_getTextureHeight_0_, int p_getTextureHeight_1_)
    {
        try
        {
            InputStream inputstream = Config.getResourceStream(new ResourceLocation(p_getTextureHeight_0_));

            if (inputstream == null)
            {
                return p_getTextureHeight_1_;
            }
            else
            {
                BufferedImage bufferedimage = ImageIO.read(inputstream);
                return bufferedimage == null ? p_getTextureHeight_1_ : bufferedimage.getHeight();
            }
        }
        catch (IOException var4)
        {
            return p_getTextureHeight_1_;
        }
    }

    private static float[][] toRgb(int[] p_toRgb_0_)
    {
        float[][] afloat = new float[p_toRgb_0_.length][3];

        for (int i = 0; i < p_toRgb_0_.length; ++i)
        {
            int j = p_toRgb_0_[i];
            float f = (float)(j >> 16 & 255) / 255.0F;
            float f1 = (float)(j >> 8 & 255) / 255.0F;
            float f2 = (float)(j & 255) / 255.0F;
            float[] afloat1 = afloat[i];
            afloat1[0] = f;
            afloat1[1] = f1;
            afloat1[2] = f2;
        }

        return afloat;
    }

    private static void readColorProperties(String p_readColorProperties_0_)
    {
        try
        {
            ResourceLocation resourcelocation = new ResourceLocation(p_readColorProperties_0_);
            InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null)
            {
                return;
            }

            Config.log("Loading " + p_readColorProperties_0_);
            Properties properties = new Properties();
            properties.load(inputstream);
            lilyPadColor = readColor(properties, "lilypad");
            particleWaterColor = readColor(properties, new String[] {"particle.water", "drop.water"});
            particlePortalColor = readColor(properties, "particle.portal");
            fogColorNether = readColorVec3(properties, "fog.nether");
            fogColorEnd = readColorVec3(properties, "fog.end");
            skyColorEnd = readColorVec3(properties, "sky.end");
            readCustomPalettes(properties, p_readColorProperties_0_);
        }
        catch (FileNotFoundException var4)
        {
            return;
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    private static void readCustomPalettes(Properties p_readCustomPalettes_0_, String p_readCustomPalettes_1_)
    {
        blockPalettes = new int[256][1];

        for (int i = 0; i < 256; ++i)
        {
            blockPalettes[i][0] = -1;
        }

        String s7 = "palette.block.";
        Map map = new HashMap();

        for (Object s : p_readCustomPalettes_0_.keySet())
        {
            String s1 = p_readCustomPalettes_0_.getProperty((String) s);

            if (((String) s).startsWith(s7))
            {
                map.put(s, s1);
            }
        }

        String[] astring2 = (String[])((String[])map.keySet().toArray(new String[map.size()]));
        paletteColors = new int[astring2.length][];

        for (int l = 0; l < astring2.length; ++l)
        {
            String s8 = astring2[l];
            String s2 = p_readCustomPalettes_0_.getProperty(s8);
            Config.log("Block palette: " + s8 + " = " + s2);
            String s3 = s8.substring(s7.length());
            String s4 = TextureUtils.getBasePath(p_readCustomPalettes_1_);
            s3 = TextureUtils.fixResourcePath(s3, s4);
            int[] aint = getCustomColors(s3, 65536);
            paletteColors[l] = aint;
            String[] astring = Config.tokenize(s2, " ,;");

            for (int j = 0; j < astring.length; ++j)
            {
                String s5 = astring[j];
                int k = -1;

                if (s5.contains(":"))
                {
                    String[] astring1 = Config.tokenize(s5, ":");
                    s5 = astring1[0];
                    String s6 = astring1[1];
                    k = Config.parseInt(s6, -1);

                    if (k < 0 || k > 15)
                    {
                        Config.log("Invalid block metadata: " + s5 + " in palette: " + s8);
                        continue;
                    }
                }

                int i1 = Config.parseInt(s5, -1);

                if (i1 >= 0 && i1 <= 255)
                {
                    if (i1 != Block.getIdFromBlock(Blocks.grass) && i1 != Block.getIdFromBlock(Blocks.tallgrass) && i1 != Block.getIdFromBlock(Blocks.leaves) && i1 != Block.getIdFromBlock(Blocks.vine))
                    {
                        if (k == -1)
                        {
                            blockPalettes[i1][0] = l;
                        }
                        else
                        {
                            if (blockPalettes[i1].length < 16)
                            {
                                blockPalettes[i1] = new int[16];
                                Arrays.fill((int[])blockPalettes[i1], (int) - 1);
                            }

                            blockPalettes[i1][k] = l;
                        }
                    }
                }
                else
                {
                    Config.log("Invalid block index: " + i1 + " in palette: " + s8);
                }
            }
        }
    }

    private static int readColor(Properties p_readColor_0_, String[] p_readColor_1_)
    {
        for (int i = 0; i < p_readColor_1_.length; ++i)
        {
            String s = p_readColor_1_[i];
            int j = readColor(p_readColor_0_, s);

            if (j >= 0)
            {
                return j;
            }
        }

        return -1;
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_)
    {
        String s = p_readColor_0_.getProperty(p_readColor_1_);

        if (s == null)
        {
            return -1;
        }
        else
        {
            try
            {
                int i = Integer.parseInt(s, 16) & 16777215;
                Config.log("Custom color: " + p_readColor_1_ + " = " + s);
                return i;
            }
            catch (NumberFormatException var4)
            {
                Config.log("Invalid custom color: " + p_readColor_1_ + " = " + s);
                return -1;
            }
        }
    }

    private static Vec3 readColorVec3(Properties p_readColorVec3_0_, String p_readColorVec3_1_)
    {
        int i = readColor(p_readColorVec3_0_, p_readColorVec3_1_);

        if (i < 0)
        {
            return null;
        }
        else
        {
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;
            return new Vec3((double)f, (double)f1, (double)f2);
        }
    }

    private static int[] getCustomColors(String p_getCustomColors_0_, String[] p_getCustomColors_1_, int p_getCustomColors_2_)
    {
        for (int i = 0; i < p_getCustomColors_1_.length; ++i)
        {
            String s = p_getCustomColors_1_[i];
            s = p_getCustomColors_0_ + s;
            int[] aint = getCustomColors(s, p_getCustomColors_2_);

            if (aint != null)
            {
                return aint;
            }
        }

        return null;
    }

    private static int[] getCustomColors(String p_getCustomColors_0_, int p_getCustomColors_1_)
    {
        try
        {
            ResourceLocation resourcelocation = new ResourceLocation(p_getCustomColors_0_);
            InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null)
            {
                return null;
            }
            else
            {
                int[] aint = TextureUtil.readImageData(Config.getResourceManager(), resourcelocation);

                if (aint == null)
                {
                    return null;
                }
                else if (p_getCustomColors_1_ > 0 && aint.length != p_getCustomColors_1_)
                {
                    Config.log("Invalid custom colors length: " + aint.length + ", path: " + p_getCustomColors_0_);
                    return null;
                }
                else
                {
                    Config.log("Loading custom colors: " + p_getCustomColors_0_);
                    return aint;
                }
            }
        }
        catch (FileNotFoundException var5)
        {
            return null;
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
            return null;
        }
    }

    public static void updateUseDefaultColorMultiplier()
    {
        useDefaultColorMultiplier = foliageBirchColors == null && foliagePineColors == null && swampGrassColors == null && swampFoliageColors == null && blockPalettes == null && Config.isSwampColors() && Config.isSmoothBiomes();
    }

    public static int getColorMultiplier(BakedQuad p_getColorMultiplier_0_, Block p_getColorMultiplier_1_, IBlockAccess p_getColorMultiplier_2_, BlockPos p_getColorMultiplier_3_, RenderEnv p_getColorMultiplier_4_)
    {
        if (useDefaultColorMultiplier)
        {
            return -1;
        }
        else
        {
            int[] aint = null;
            int[] aint1 = null;

            if (blockPalettes != null)
            {
                int i = p_getColorMultiplier_4_.getBlockId();

                if (i >= 0 && i < 256)
                {
                    int[] aint2 = blockPalettes[i];
                    int j = -1;

                    if (aint2.length > 1)
                    {
                        int k = p_getColorMultiplier_4_.getMetadata();
                        j = aint2[k];
                    }
                    else
                    {
                        j = aint2[0];
                    }

                    if (j >= 0)
                    {
                        aint = paletteColors[j];
                    }
                }

                if (aint != null)
                {
                    if (Config.isSmoothBiomes())
                    {
                        return getSmoothColorMultiplier(p_getColorMultiplier_1_, p_getColorMultiplier_2_, p_getColorMultiplier_3_, aint, aint, 0, 0, p_getColorMultiplier_4_);
                    }

                    return getCustomColor(aint, p_getColorMultiplier_2_, p_getColorMultiplier_3_);
                }
            }

            if (!p_getColorMultiplier_0_.hasTintIndex())
            {
                return -1;
            }
            else if (p_getColorMultiplier_1_ == Blocks.waterlily)
            {
                return getLilypadColorMultiplier(p_getColorMultiplier_2_, p_getColorMultiplier_3_);
            }
            else if (p_getColorMultiplier_1_ instanceof BlockStem)
            {
                return getStemColorMultiplier(p_getColorMultiplier_1_, p_getColorMultiplier_2_, p_getColorMultiplier_3_, p_getColorMultiplier_4_);
            }
            else
            {
                boolean flag = Config.isSwampColors();
                boolean flag1 = false;
                int l = 0;
                int i1 = 0;

                if (p_getColorMultiplier_1_ != Blocks.grass && p_getColorMultiplier_1_ != Blocks.tallgrass)
                {
                    if (p_getColorMultiplier_1_ == Blocks.leaves)
                    {
                        l = 2;
                        flag1 = Config.isSmoothBiomes();
                        i1 = p_getColorMultiplier_4_.getMetadata();

                        if ((i1 & 3) == 1)
                        {
                            aint = foliagePineColors;
                        }
                        else if ((i1 & 3) == 2)
                        {
                            aint = foliageBirchColors;
                        }
                        else
                        {
                            aint = foliageColors;

                            if (flag)
                            {
                                aint1 = swampFoliageColors;
                            }
                            else
                            {
                                aint1 = aint;
                            }
                        }
                    }
                    else if (p_getColorMultiplier_1_ == Blocks.vine)
                    {
                        l = 2;
                        flag1 = Config.isSmoothBiomes();
                        aint = foliageColors;

                        if (flag)
                        {
                            aint1 = swampFoliageColors;
                        }
                        else
                        {
                            aint1 = aint;
                        }
                    }
                }
                else
                {
                    l = 1;
                    flag1 = Config.isSmoothBiomes();
                    aint = grassColors;

                    if (flag)
                    {
                        aint1 = swampGrassColors;
                    }
                    else
                    {
                        aint1 = aint;
                    }
                }

                if (flag1)
                {
                    return getSmoothColorMultiplier(p_getColorMultiplier_1_, p_getColorMultiplier_2_, p_getColorMultiplier_3_, aint, aint1, l, i1, p_getColorMultiplier_4_);
                }
                else
                {
                    if (aint1 != aint && p_getColorMultiplier_2_.getBiomeGenForCoords(p_getColorMultiplier_3_) == BiomeGenBase.swampland)
                    {
                        aint = aint1;
                    }

                    return aint != null ? getCustomColor(aint, p_getColorMultiplier_2_, p_getColorMultiplier_3_) : -1;
                }
            }
        }
    }

    private static int getSmoothColorMultiplier(Block p_getSmoothColorMultiplier_0_, IBlockAccess p_getSmoothColorMultiplier_1_, BlockPos p_getSmoothColorMultiplier_2_, int[] p_getSmoothColorMultiplier_3_, int[] p_getSmoothColorMultiplier_4_, int p_getSmoothColorMultiplier_5_, int p_getSmoothColorMultiplier_6_, RenderEnv p_getSmoothColorMultiplier_7_)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int l = p_getSmoothColorMultiplier_2_.getX();
        int i1 = p_getSmoothColorMultiplier_2_.getY();
        int j1 = p_getSmoothColorMultiplier_2_.getZ();
        BlockPosM blockposm = p_getSmoothColorMultiplier_7_.getColorizerBlockPos();

        for (int k1 = l - 1; k1 <= l + 1; ++k1)
        {
            for (int l1 = j1 - 1; l1 <= j1 + 1; ++l1)
            {
                blockposm.setXyz(k1, i1, l1);
                int[] aint = p_getSmoothColorMultiplier_3_;

                if (p_getSmoothColorMultiplier_4_ != p_getSmoothColorMultiplier_3_ && p_getSmoothColorMultiplier_1_.getBiomeGenForCoords(blockposm) == BiomeGenBase.swampland)
                {
                    aint = p_getSmoothColorMultiplier_4_;
                }

                int i2 = 0;

                if (aint == null)
                {
                    switch (p_getSmoothColorMultiplier_5_)
                    {
                        case 1:
                            i2 = p_getSmoothColorMultiplier_1_.getBiomeGenForCoords(blockposm).getGrassColorAtPos(blockposm);
                            break;

                        case 2:
                            if ((p_getSmoothColorMultiplier_6_ & 3) == 1)
                            {
                                i2 = ColorizerFoliage.getFoliageColorPine();
                            }
                            else if ((p_getSmoothColorMultiplier_6_ & 3) == 2)
                            {
                                i2 = ColorizerFoliage.getFoliageColorBirch();
                            }
                            else
                            {
                                i2 = p_getSmoothColorMultiplier_1_.getBiomeGenForCoords(blockposm).getFoliageColorAtPos(blockposm);
                            }

                            break;

                        default:
                            i2 = p_getSmoothColorMultiplier_0_.colorMultiplier(p_getSmoothColorMultiplier_1_, blockposm);
                    }
                }
                else
                {
                    i2 = getCustomColor(aint, p_getSmoothColorMultiplier_1_, blockposm);
                }

                i += i2 >> 16 & 255;
                j += i2 >> 8 & 255;
                k += i2 & 255;
            }
        }

        int j2 = i / 9;
        int k2 = j / 9;
        int l2 = k / 9;
        return j2 << 16 | k2 << 8 | l2;
    }

    public static int getFluidColor(Block p_getFluidColor_0_, IBlockAccess p_getFluidColor_1_, BlockPos p_getFluidColor_2_)
    {
        return p_getFluidColor_0_.getMaterial() != Material.water ? p_getFluidColor_0_.colorMultiplier(p_getFluidColor_1_, p_getFluidColor_2_) : (waterColors != null ? (Config.isSmoothBiomes() ? getSmoothColor(waterColors, p_getFluidColor_1_, (double)p_getFluidColor_2_.getX(), (double)p_getFluidColor_2_.getY(), (double)p_getFluidColor_2_.getZ(), 3, 1) : getCustomColor(waterColors, p_getFluidColor_1_, p_getFluidColor_2_)) : (!Config.isSwampColors() ? 16777215 : p_getFluidColor_0_.colorMultiplier(p_getFluidColor_1_, p_getFluidColor_2_)));
    }

    private static int getCustomColor(int[] p_getCustomColor_0_, IBlockAccess p_getCustomColor_1_, BlockPos p_getCustomColor_2_)
    {
        BiomeGenBase biomegenbase = p_getCustomColor_1_.getBiomeGenForCoords(p_getCustomColor_2_);
        double d0 = (double)MathHelper.clamp_float(biomegenbase.getFloatTemperature(p_getCustomColor_2_), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp_float(biomegenbase.getFloatRainfall(), 0.0F, 1.0F);
        d1 = d1 * d0;
        int i = (int)((1.0D - d0) * 255.0D);
        int j = (int)((1.0D - d1) * 255.0D);
        return p_getCustomColor_0_[j << 8 | i] & 16777215;
    }

    public static void updatePortalFX(EntityFX p_updatePortalFX_0_)
    {
        if (particlePortalColor >= 0)
        {
            int i = particlePortalColor;
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;
            p_updatePortalFX_0_.setRBGColorF(f, f1, f2);
        }
    }

    public static void updateMyceliumFX(EntityFX p_updateMyceliumFX_0_)
    {
        if (myceliumParticleColors != null)
        {
            int i = myceliumParticleColors[random.nextInt(myceliumParticleColors.length)];
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;
            p_updateMyceliumFX_0_.setRBGColorF(f, f1, f2);
        }
    }

    public static void updateReddustFX(EntityFX p_updateReddustFX_0_, IBlockAccess p_updateReddustFX_1_, double p_updateReddustFX_2_, double p_updateReddustFX_4_, double p_updateReddustFX_6_)
    {
        if (redstoneColors != null)
        {
            IBlockState iblockstate = p_updateReddustFX_1_.getBlockState(new BlockPos(p_updateReddustFX_2_, p_updateReddustFX_4_, p_updateReddustFX_6_));
            int i = getRedstoneLevel(iblockstate, 15);
            int j = getRedstoneColor(i);

            if (j != -1)
            {
                int k = j >> 16 & 255;
                int l = j >> 8 & 255;
                int i1 = j & 255;
                float f = (float)k / 255.0F;
                float f1 = (float)l / 255.0F;
                float f2 = (float)i1 / 255.0F;
                p_updateReddustFX_0_.setRBGColorF(f, f1, f2);
            }
        }
    }

    private static int getRedstoneLevel(IBlockState p_getRedstoneLevel_0_, int p_getRedstoneLevel_1_)
    {
        Block block = p_getRedstoneLevel_0_.getBlock();

        if (!(block instanceof BlockRedstoneWire))
        {
            return p_getRedstoneLevel_1_;
        }
        else
        {
            Object object = p_getRedstoneLevel_0_.getValue(BlockRedstoneWire.POWER);

            if (!(object instanceof Integer))
            {
                return p_getRedstoneLevel_1_;
            }
            else
            {
                Integer integer = (Integer)object;
                return integer.intValue();
            }
        }
    }

    public static int getRedstoneColor(int p_getRedstoneColor_0_)
    {
        return redstoneColors == null ? -1 : (p_getRedstoneColor_0_ >= 0 && p_getRedstoneColor_0_ <= 15 ? redstoneColors[p_getRedstoneColor_0_] & 16777215 : -1);
    }

    public static void updateWaterFX(EntityFX p_updateWaterFX_0_, IBlockAccess p_updateWaterFX_1_, double p_updateWaterFX_2_, double p_updateWaterFX_4_, double p_updateWaterFX_6_)
    {
        if (waterColors != null)
        {
            int i = getFluidColor(Blocks.water, p_updateWaterFX_1_, new BlockPos(p_updateWaterFX_2_, p_updateWaterFX_4_, p_updateWaterFX_6_));
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;

            if (particleWaterColor >= 0)
            {
                int i1 = particleWaterColor >> 16 & 255;
                int j1 = particleWaterColor >> 8 & 255;
                int k1 = particleWaterColor & 255;
                f *= (float)i1 / 255.0F;
                f1 *= (float)j1 / 255.0F;
                f2 *= (float)k1 / 255.0F;
            }

            p_updateWaterFX_0_.setRBGColorF(f, f1, f2);
        }
    }

    public static int getLilypadColorMultiplier(IBlockAccess p_getLilypadColorMultiplier_0_, BlockPos p_getLilypadColorMultiplier_1_)
    {
        return lilyPadColor < 0 ? Blocks.waterlily.colorMultiplier(p_getLilypadColorMultiplier_0_, p_getLilypadColorMultiplier_1_) : lilyPadColor;
    }

    public static Vec3 getFogColorNether(Vec3 p_getFogColorNether_0_)
    {
        return fogColorNether == null ? p_getFogColorNether_0_ : fogColorNether;
    }

    public static Vec3 getFogColorEnd(Vec3 p_getFogColorEnd_0_)
    {
        return fogColorEnd == null ? p_getFogColorEnd_0_ : fogColorEnd;
    }

    public static Vec3 getSkyColorEnd(Vec3 p_getSkyColorEnd_0_)
    {
        return skyColorEnd == null ? p_getSkyColorEnd_0_ : skyColorEnd;
    }

    public static Vec3 getSkyColor(Vec3 p_getSkyColor_0_, IBlockAccess p_getSkyColor_1_, double p_getSkyColor_2_, double p_getSkyColor_4_, double p_getSkyColor_6_)
    {
        if (skyColors == null)
        {
            return p_getSkyColor_0_;
        }
        else
        {
            int i = getSmoothColor(skyColors, p_getSkyColor_1_, p_getSkyColor_2_, p_getSkyColor_4_, p_getSkyColor_6_, 7, 1);
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;
            float f3 = (float)p_getSkyColor_0_.xCoord / 0.5F;
            float f4 = (float)p_getSkyColor_0_.yCoord / 0.66275F;
            float f5 = (float)p_getSkyColor_0_.zCoord;
            f = f * f3;
            f1 = f1 * f4;
            f2 = f2 * f5;
            return new Vec3((double)f, (double)f1, (double)f2);
        }
    }

    public static Vec3 getFogColor(Vec3 p_getFogColor_0_, IBlockAccess p_getFogColor_1_, double p_getFogColor_2_, double p_getFogColor_4_, double p_getFogColor_6_)
    {
        if (fogColors == null)
        {
            return p_getFogColor_0_;
        }
        else
        {
            int i = getSmoothColor(fogColors, p_getFogColor_1_, p_getFogColor_2_, p_getFogColor_4_, p_getFogColor_6_, 7, 1);
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;
            float f3 = (float)p_getFogColor_0_.xCoord / 0.753F;
            float f4 = (float)p_getFogColor_0_.yCoord / 0.8471F;
            float f5 = (float)p_getFogColor_0_.zCoord;
            f = f * f3;
            f1 = f1 * f4;
            f2 = f2 * f5;
            return new Vec3((double)f, (double)f1, (double)f2);
        }
    }

    public static Vec3 getUnderwaterColor(IBlockAccess p_getUnderwaterColor_0_, double p_getUnderwaterColor_1_, double p_getUnderwaterColor_3_, double p_getUnderwaterColor_5_)
    {
        if (underwaterColors == null)
        {
            return null;
        }
        else
        {
            int i = getSmoothColor(underwaterColors, p_getUnderwaterColor_0_, p_getUnderwaterColor_1_, p_getUnderwaterColor_3_, p_getUnderwaterColor_5_, 7, 1);
            int j = i >> 16 & 255;
            int k = i >> 8 & 255;
            int l = i & 255;
            float f = (float)j / 255.0F;
            float f1 = (float)k / 255.0F;
            float f2 = (float)l / 255.0F;
            return new Vec3((double)f, (double)f1, (double)f2);
        }
    }

    public static int getSmoothColor(int[] p_getSmoothColor_0_, IBlockAccess p_getSmoothColor_1_, double p_getSmoothColor_2_, double p_getSmoothColor_4_, double p_getSmoothColor_6_, int p_getSmoothColor_8_, int p_getSmoothColor_9_)
    {
        if (p_getSmoothColor_0_ == null)
        {
            return -1;
        }
        else
        {
            int i = MathHelper.floor_double(p_getSmoothColor_2_);
            int j = MathHelper.floor_double(p_getSmoothColor_4_);
            int k = MathHelper.floor_double(p_getSmoothColor_6_);
            int l = p_getSmoothColor_8_ * p_getSmoothColor_9_ / 2;
            int i1 = 0;
            int j1 = 0;
            int k1 = 0;
            int l1 = 0;
            BlockPosM blockposm = new BlockPosM(0, 0, 0);

            for (int i2 = i - l; i2 <= i + l; i2 += p_getSmoothColor_9_)
            {
                for (int j2 = k - l; j2 <= k + l; j2 += p_getSmoothColor_9_)
                {
                    blockposm.setXyz(i2, j, j2);
                    int k2 = getCustomColor(p_getSmoothColor_0_, p_getSmoothColor_1_, blockposm);
                    i1 += k2 >> 16 & 255;
                    j1 += k2 >> 8 & 255;
                    k1 += k2 & 255;
                    ++l1;
                }
            }

            int l2 = i1 / l1;
            int i3 = j1 / l1;
            int j3 = k1 / l1;
            return l2 << 16 | i3 << 8 | j3;
        }
    }

    public static int mixColors(int p_mixColors_0_, int p_mixColors_1_, float p_mixColors_2_)
    {
        if (p_mixColors_2_ <= 0.0F)
        {
            return p_mixColors_1_;
        }
        else if (p_mixColors_2_ >= 1.0F)
        {
            return p_mixColors_0_;
        }
        else
        {
            float f = 1.0F - p_mixColors_2_;
            int i = p_mixColors_0_ >> 16 & 255;
            int j = p_mixColors_0_ >> 8 & 255;
            int k = p_mixColors_0_ & 255;
            int l = p_mixColors_1_ >> 16 & 255;
            int i1 = p_mixColors_1_ >> 8 & 255;
            int j1 = p_mixColors_1_ & 255;
            int k1 = (int)((float)i * p_mixColors_2_ + (float)l * f);
            int l1 = (int)((float)j * p_mixColors_2_ + (float)i1 * f);
            int i2 = (int)((float)k * p_mixColors_2_ + (float)j1 * f);
            return k1 << 16 | l1 << 8 | i2;
        }
    }

    private static int averageColor(int p_averageColor_0_, int p_averageColor_1_)
    {
        int i = p_averageColor_0_ >> 16 & 255;
        int j = p_averageColor_0_ >> 8 & 255;
        int k = p_averageColor_0_ & 255;
        int l = p_averageColor_1_ >> 16 & 255;
        int i1 = p_averageColor_1_ >> 8 & 255;
        int j1 = p_averageColor_1_ & 255;
        int k1 = (i + l) / 2;
        int l1 = (j + i1) / 2;
        int i2 = (k + j1) / 2;
        return k1 << 16 | l1 << 8 | i2;
    }

    public static int getStemColorMultiplier(Block p_getStemColorMultiplier_0_, IBlockAccess p_getStemColorMultiplier_1_, BlockPos p_getStemColorMultiplier_2_, RenderEnv p_getStemColorMultiplier_3_)
    {
        if (stemColors == null)
        {
            return p_getStemColorMultiplier_0_.colorMultiplier(p_getStemColorMultiplier_1_, p_getStemColorMultiplier_2_);
        }
        else
        {
            int i = p_getStemColorMultiplier_3_.getMetadata();

            if (i < 0)
            {
                i = 0;
            }

            if (i >= stemColors.length)
            {
                i = stemColors.length - 1;
            }

            return stemColors[i];
        }
    }

    public static boolean updateLightmap(World p_updateLightmap_0_, float p_updateLightmap_1_, int[] p_updateLightmap_2_, boolean p_updateLightmap_3_)
    {
        if (p_updateLightmap_0_ == null)
        {
            return false;
        }
        else if (lightMapsColorsRgb == null)
        {
            return false;
        }
        else if (!Config.isCustomColors())
        {
            return false;
        }
        else
        {
            int i = p_updateLightmap_0_.provider.getDimensionId();

            if (i >= -1 && i <= 1)
            {
                int j = i + 1;
                float[][] afloat = lightMapsColorsRgb[j];

                if (afloat == null)
                {
                    return false;
                }
                else
                {
                    int k = lightMapsHeight[j];

                    if (p_updateLightmap_3_ && k < 64)
                    {
                        return false;
                    }
                    else
                    {
                        int l = afloat.length / k;

                        if (l < 16)
                        {
                            Config.warn("Invalid lightmap width: " + l + " for: /environment/lightmap" + i + ".png");
                            lightMapsColorsRgb[j] = (float[][])null;
                            return false;
                        }
                        else
                        {
                            int i1 = 0;

                            if (p_updateLightmap_3_)
                            {
                                i1 = l * 16 * 2;
                            }

                            float f = 1.1666666F * (p_updateLightmap_0_.getSunBrightness(1.0F) - 0.2F);

                            if (p_updateLightmap_0_.getLastLightningBolt() > 0)
                            {
                                f = 1.0F;
                            }

                            f = Config.limitTo1(f);
                            float f1 = f * (float)(l - 1);
                            float f2 = Config.limitTo1(p_updateLightmap_1_ + 0.5F) * (float)(l - 1);
                            float f3 = Config.limitTo1(Config.getGameSettings().gammaSetting);
                            boolean flag = f3 > 1.0E-4F;
                            getLightMapColumn(afloat, f1, i1, l, sunRgbs);
                            getLightMapColumn(afloat, f2, i1 + 16 * l, l, torchRgbs);
                            float[] afloat1 = new float[3];

                            for (int j1 = 0; j1 < 16; ++j1)
                            {
                                for (int k1 = 0; k1 < 16; ++k1)
                                {
                                    for (int l1 = 0; l1 < 3; ++l1)
                                    {
                                        float f4 = Config.limitTo1(sunRgbs[j1][l1] + torchRgbs[k1][l1]);

                                        if (flag)
                                        {
                                            float f5 = 1.0F - f4;
                                            f5 = 1.0F - f5 * f5 * f5 * f5;
                                            f4 = f3 * f5 + (1.0F - f3) * f4;
                                        }

                                        afloat1[l1] = f4;
                                    }

                                    int i2 = (int)(afloat1[0] * 255.0F);
                                    int j2 = (int)(afloat1[1] * 255.0F);
                                    int k2 = (int)(afloat1[2] * 255.0F);
                                    p_updateLightmap_2_[j1 * 16 + k1] = -16777216 | i2 << 16 | j2 << 8 | k2;
                                }
                            }

                            return true;
                        }
                    }
                }
            }
            else
            {
                return false;
            }
        }
    }

    private static void getLightMapColumn(float[][] p_getLightMapColumn_0_, float p_getLightMapColumn_1_, int p_getLightMapColumn_2_, int p_getLightMapColumn_3_, float[][] p_getLightMapColumn_4_)
    {
        int i = (int)Math.floor((double)p_getLightMapColumn_1_);
        int j = (int)Math.ceil((double)p_getLightMapColumn_1_);

        if (i == j)
        {
            for (int i1 = 0; i1 < 16; ++i1)
            {
                float[] afloat3 = p_getLightMapColumn_0_[p_getLightMapColumn_2_ + i1 * p_getLightMapColumn_3_ + i];
                float[] afloat4 = p_getLightMapColumn_4_[i1];

                for (int j1 = 0; j1 < 3; ++j1)
                {
                    afloat4[j1] = afloat3[j1];
                }
            }
        }
        else
        {
            float f = 1.0F - (p_getLightMapColumn_1_ - (float)i);
            float f1 = 1.0F - ((float)j - p_getLightMapColumn_1_);

            for (int k = 0; k < 16; ++k)
            {
                float[] afloat = p_getLightMapColumn_0_[p_getLightMapColumn_2_ + k * p_getLightMapColumn_3_ + i];
                float[] afloat1 = p_getLightMapColumn_0_[p_getLightMapColumn_2_ + k * p_getLightMapColumn_3_ + j];
                float[] afloat2 = p_getLightMapColumn_4_[k];

                for (int l = 0; l < 3; ++l)
                {
                    afloat2[l] = afloat[l] * f + afloat1[l] * f1;
                }
            }
        }
    }

    public static Vec3 getWorldFogColor(Vec3 p_getWorldFogColor_0_, WorldClient p_getWorldFogColor_1_, Entity p_getWorldFogColor_2_, float p_getWorldFogColor_3_)
    {
        int i = p_getWorldFogColor_1_.provider.getDimensionId();

        switch (i)
        {
            case -1:
                p_getWorldFogColor_0_ = getFogColorNether(p_getWorldFogColor_0_);
                break;

            case 0:
                Minecraft minecraft = Minecraft.getMinecraft();
                p_getWorldFogColor_0_ = getFogColor(p_getWorldFogColor_0_, minecraft.theWorld, p_getWorldFogColor_2_.posX, p_getWorldFogColor_2_.posY + 1.0D, p_getWorldFogColor_2_.posZ);
                break;

            case 1:
                p_getWorldFogColor_0_ = getFogColorEnd(p_getWorldFogColor_0_);
        }

        return p_getWorldFogColor_0_;
    }

    public static Vec3 getWorldSkyColor(Vec3 p_getWorldSkyColor_0_, WorldClient p_getWorldSkyColor_1_, Entity p_getWorldSkyColor_2_, float p_getWorldSkyColor_3_)
    {
        int i = p_getWorldSkyColor_1_.provider.getDimensionId();

        switch (i)
        {
            case 0:
                Minecraft minecraft = Minecraft.getMinecraft();
                p_getWorldSkyColor_0_ = getSkyColor(p_getWorldSkyColor_0_, minecraft.theWorld, p_getWorldSkyColor_2_.posX, p_getWorldSkyColor_2_.posY + 1.0D, p_getWorldSkyColor_2_.posZ);
                break;

            case 1:
                p_getWorldSkyColor_0_ = getSkyColorEnd(p_getWorldSkyColor_0_);
        }

        return p_getWorldSkyColor_0_;
    }
}
