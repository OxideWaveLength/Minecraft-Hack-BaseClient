package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase
{
    private final Item field_150960_a;

    public StatCrafting(String p_i45910_1_, String p_i45910_2_, IChatComponent statNameIn, Item p_i45910_4_)
    {
        super(p_i45910_1_ + p_i45910_2_, statNameIn);
        this.field_150960_a = p_i45910_4_;
        int i = Item.getIdFromItem(p_i45910_4_);

        if (i != 0)
        {
            IScoreObjectiveCriteria.INSTANCES.put(p_i45910_1_ + i, this.func_150952_k());
        }
    }

    public Item func_150959_a()
    {
        return this.field_150960_a;
    }
}
