package optfine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.Block;

public class BlockUtils
{
    private static ReflectorClass ForgeBlock = new ReflectorClass(Block.class);
    private static ReflectorMethod ForgeBlock_setLightOpacity = new ReflectorMethod(ForgeBlock, "setLightOpacity");
    private static boolean directAccessValid = true;
    private static Map mapOriginalOpacity = new IdentityHashMap();

    public static void setLightOpacity(Block p_setLightOpacity_0_, int p_setLightOpacity_1_)
    {
        if (!mapOriginalOpacity.containsKey(p_setLightOpacity_0_))
        {
            mapOriginalOpacity.put(p_setLightOpacity_0_, Integer.valueOf(p_setLightOpacity_0_.getLightOpacity()));
        }

        if (directAccessValid)
        {
            try
            {
                p_setLightOpacity_0_.setLightOpacity(p_setLightOpacity_1_);
                return;
            }
            catch (IllegalAccessError illegalaccesserror)
            {
                directAccessValid = false;

                if (!ForgeBlock_setLightOpacity.exists())
                {
                    throw illegalaccesserror;
                }
            }
        }

        Reflector.callVoid(p_setLightOpacity_0_, ForgeBlock_setLightOpacity, new Object[] {Integer.valueOf(p_setLightOpacity_1_)});
    }

    public static void restoreLightOpacity(Block p_restoreLightOpacity_0_)
    {
        if (mapOriginalOpacity.containsKey(p_restoreLightOpacity_0_))
        {
            int i = ((Integer)mapOriginalOpacity.get(p_restoreLightOpacity_0_)).intValue();
            setLightOpacity(p_restoreLightOpacity_0_, i);
        }
    }
}
