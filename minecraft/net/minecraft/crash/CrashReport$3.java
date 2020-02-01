package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.client.renderer.OpenGlHelper;

class CrashReport3 implements Callable
{
    final CrashReport field_71490_a;

    CrashReport3(CrashReport p_i1340_1_)
    {
        this.field_71490_a = p_i1340_1_;
    }

    public String call()
    {
        return OpenGlHelper.func_183029_j();
    }
}
