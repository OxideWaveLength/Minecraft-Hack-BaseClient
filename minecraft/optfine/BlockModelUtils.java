package optfine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

public class BlockModelUtils
{
    public static IBakedModel makeModelCube(String p_makeModelCube_0_, int p_makeModelCube_1_)
    {
        TextureAtlasSprite textureatlassprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(p_makeModelCube_0_);
        return makeModelCube(textureatlassprite, p_makeModelCube_1_);
    }

    public static IBakedModel makeModelCube(TextureAtlasSprite p_makeModelCube_0_, int p_makeModelCube_1_)
    {
        List list = new ArrayList();
        EnumFacing[] aenumfacing = EnumFacing.values();
        List list1 = new ArrayList(aenumfacing.length);

        for (int i = 0; i < aenumfacing.length; ++i)
        {
            EnumFacing enumfacing = aenumfacing[i];
            List list2 = new ArrayList();
            list2.add(makeBakedQuad(enumfacing, p_makeModelCube_0_, p_makeModelCube_1_));
            list1.add(list2);
        }

        IBakedModel ibakedmodel = new SimpleBakedModel(list, list1, true, true, p_makeModelCube_0_, ItemCameraTransforms.DEFAULT);
        return ibakedmodel;
    }

    private static BakedQuad makeBakedQuad(EnumFacing p_makeBakedQuad_0_, TextureAtlasSprite p_makeBakedQuad_1_, int p_makeBakedQuad_2_)
    {
        Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
        Vector3f vector3f1 = new Vector3f(16.0F, 16.0F, 16.0F);
        BlockFaceUV blockfaceuv = new BlockFaceUV(new float[] {0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace blockpartface = new BlockPartFace(p_makeBakedQuad_0_, p_makeBakedQuad_2_, "#" + p_makeBakedQuad_0_.getName(), blockfaceuv);
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        BlockPartRotation blockpartrotation = null;
        boolean flag = false;
        boolean flag1 = true;
        FaceBakery facebakery = new FaceBakery();
        BakedQuad bakedquad = facebakery.makeBakedQuad(vector3f, vector3f1, blockpartface, p_makeBakedQuad_1_, p_makeBakedQuad_0_, modelrotation, blockpartrotation, flag, flag1);
        return bakedquad;
    }
}
