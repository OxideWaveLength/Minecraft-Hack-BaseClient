package net.minecraft.stats;

import net.minecraft.util.IChatComponent;

public class StatBasic extends StatBase
{
    public StatBasic(String statIdIn, IChatComponent statNameIn, IStatType typeIn)
    {
        super(statIdIn, statNameIn, typeIn);
    }

    public StatBasic(String statIdIn, IChatComponent statNameIn)
    {
        super(statIdIn, statNameIn);
    }

    /**
     * Register the stat into StatList.
     */
    public StatBase registerStat()
    {
        super.registerStat();
        StatList.generalStats.add(this);
        return this;
    }
}
