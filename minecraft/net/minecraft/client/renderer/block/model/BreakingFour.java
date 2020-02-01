package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad
{
    private final TextureAtlasSprite texture;

    public BreakingFour(BakedQuad p_i46217_1_, TextureAtlasSprite textureIn)
    {
        super(Arrays.copyOf(p_i46217_1_.getVertexData(), p_i46217_1_.getVertexData().length), p_i46217_1_.tintIndex, FaceBakery.getFacingFromVertexData(p_i46217_1_.getVertexData()));
        this.texture = textureIn;
        this.func_178217_e();
    }

    private void func_178217_e()
    {
        for (int i = 0; i < 4; ++i)
        {
            this.func_178216_a(i);
        }
    }

    private void func_178216_a(int p_178216_1_)
    {
        int i = 7 * p_178216_1_;
        float f = Float.intBitsToFloat(this.vertexData[i]);
        float f1 = Float.intBitsToFloat(this.vertexData[i + 1]);
        float f2 = Float.intBitsToFloat(this.vertexData[i + 2]);
        float f3 = 0.0F;
        float f4 = 0.0F;

        switch (this.face)
        {
            case DOWN:
                f3 = f * 16.0F;
                f4 = (1.0F - f2) * 16.0F;
                break;

            case UP:
                f3 = f * 16.0F;
                f4 = f2 * 16.0F;
                break;

            case NORTH:
                f3 = (1.0F - f) * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
                break;

            case SOUTH:
                f3 = f * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
                break;

            case WEST:
                f3 = f2 * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
                break;

            case EAST:
                f3 = (1.0F - f2) * 16.0F;
                f4 = (1.0F - f1) * 16.0F;
        }

        this.vertexData[i + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)f3));
        this.vertexData[i + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)f4));
    }
}
