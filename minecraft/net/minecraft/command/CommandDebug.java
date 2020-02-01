package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase
{
    private static final Logger logger = LogManager.getLogger();
    private long field_147206_b;
    private int field_147207_c;

    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "debug";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.debug.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.debug.usage", new Object[0]);
        }
        else
        {
            if (args[0].equals("start"))
            {
                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                notifyOperators(sender, this, "commands.debug.start", new Object[0]);
                MinecraftServer.getServer().enableProfiling();
                this.field_147206_b = MinecraftServer.getCurrentTimeMillis();
                this.field_147207_c = MinecraftServer.getServer().getTickCounter();
            }
            else
            {
                if (!args[0].equals("stop"))
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                if (!MinecraftServer.getServer().theProfiler.profilingEnabled)
                {
                    throw new CommandException("commands.debug.notStarted", new Object[0]);
                }

                long i = MinecraftServer.getCurrentTimeMillis();
                int j = MinecraftServer.getServer().getTickCounter();
                long k = i - this.field_147206_b;
                int l = j - this.field_147207_c;
                this.func_147205_a(k, l);
                MinecraftServer.getServer().theProfiler.profilingEnabled = false;
                notifyOperators(sender, this, "commands.debug.stop", new Object[] {Float.valueOf((float)k / 1000.0F), Integer.valueOf(l)});
            }
        }
    }

    private void func_147205_a(long p_147205_1_, int p_147205_3_)
    {
        File file1 = new File(MinecraftServer.getServer().getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        file1.getParentFile().mkdirs();

        try
        {
            FileWriter filewriter = new FileWriter(file1);
            filewriter.write(this.func_147204_b(p_147205_1_, p_147205_3_));
            filewriter.close();
        }
        catch (Throwable throwable)
        {
            logger.error("Could not save profiler results to " + file1, throwable);
        }
    }

    private String func_147204_b(long p_147204_1_, int p_147204_3_)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(func_147203_d());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(p_147204_1_).append(" ms\n");
        stringbuilder.append("Tick span: ").append(p_147204_3_).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format("%.2f", new Object[] {Float.valueOf((float)p_147204_3_ / ((float)p_147204_1_ / 1000.0F))})).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.func_147202_a(0, "root", stringbuilder);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private void func_147202_a(int p_147202_1_, String p_147202_2_, StringBuilder p_147202_3_)
    {
        List<Profiler.Result> list = MinecraftServer.getServer().theProfiler.getProfilingData(p_147202_2_);

        if (list != null && list.size() >= 3)
        {
            for (int i = 1; i < list.size(); ++i)
            {
                Profiler.Result profiler$result = (Profiler.Result)list.get(i);
                p_147202_3_.append(String.format("[%02d] ", new Object[] {Integer.valueOf(p_147202_1_)}));

                for (int j = 0; j < p_147202_1_; ++j)
                {
                    p_147202_3_.append(" ");
                }

                p_147202_3_.append(profiler$result.field_76331_c).append(" - ").append(String.format("%.2f", new Object[] {Double.valueOf(profiler$result.field_76332_a)})).append("%/").append(String.format("%.2f", new Object[] {Double.valueOf(profiler$result.field_76330_b)})).append("%\n");

                if (!profiler$result.field_76331_c.equals("unspecified"))
                {
                    try
                    {
                        this.func_147202_a(p_147202_1_ + 1, p_147202_2_ + "." + profiler$result.field_76331_c, p_147202_3_);
                    }
                    catch (Exception exception)
                    {
                        p_147202_3_.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
                    }
                }
            }
        }
    }

    private static String func_147203_d()
    {
        String[] astring = new String[] {"Shiny numbers!", "Am I not running fast enough? :(", "I\'m working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it\'ll have more motivation to work faster! Poor server."};

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"start", "stop"}): null;
    }
}
