package net.minecraft.command.server;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public abstract class CommandBlockLogic implements ICommandSender
{
    /** The formatting for the timestamp on commands run. */
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss");

    /** The number of successful commands run. (used for redstone output) */
    private int successCount;
    private boolean trackOutput = true;

    /** The previously run command. */
    private IChatComponent lastOutput = null;

    /** The command stored in the command block. */
    private String commandStored = "";

    /** The custom name of the command block. (defaults to "@") */
    private String customName = "@";
    private final CommandResultStats resultStats = new CommandResultStats();

    /**
     * returns the successCount int.
     */
    public int getSuccessCount()
    {
        return this.successCount;
    }

    /**
     * Returns the lastOutput.
     */
    public IChatComponent getLastOutput()
    {
        return this.lastOutput;
    }

    /**
     * Stores data to NBT format.
     */
    public void writeDataToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("Command", this.commandStored);
        tagCompound.setInteger("SuccessCount", this.successCount);
        tagCompound.setString("CustomName", this.customName);
        tagCompound.setBoolean("TrackOutput", this.trackOutput);

        if (this.lastOutput != null && this.trackOutput)
        {
            tagCompound.setString("LastOutput", IChatComponent.Serializer.componentToJson(this.lastOutput));
        }

        this.resultStats.writeStatsToNBT(tagCompound);
    }

    /**
     * Reads NBT formatting and stored data into variables.
     */
    public void readDataFromNBT(NBTTagCompound nbt)
    {
        this.commandStored = nbt.getString("Command");
        this.successCount = nbt.getInteger("SuccessCount");

        if (nbt.hasKey("CustomName", 8))
        {
            this.customName = nbt.getString("CustomName");
        }

        if (nbt.hasKey("TrackOutput", 1))
        {
            this.trackOutput = nbt.getBoolean("TrackOutput");
        }

        if (nbt.hasKey("LastOutput", 8) && this.trackOutput)
        {
            this.lastOutput = IChatComponent.Serializer.jsonToComponent(nbt.getString("LastOutput"));
        }

        this.resultStats.readStatsFromNBT(nbt);
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return permLevel <= 2;
    }

    /**
     * Sets the command.
     */
    public void setCommand(String command)
    {
        this.commandStored = command;
        this.successCount = 0;
    }

    /**
     * Returns the command of the command block.
     */
    public String getCommand()
    {
        return this.commandStored;
    }

    public void trigger(World worldIn)
    {
        if (worldIn.isRemote)
        {
            this.successCount = 0;
        }

        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver != null && minecraftserver.isAnvilFileSet() && minecraftserver.isCommandBlockEnabled())
        {
            ICommandManager icommandmanager = minecraftserver.getCommandManager();

            try
            {
                this.lastOutput = null;
                this.successCount = icommandmanager.executeCommand(this, this.commandStored);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Executing command block");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Command to be executed");
                crashreportcategory.addCrashSectionCallable("Command", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return CommandBlockLogic.this.getCommand();
                    }
                });
                crashreportcategory.addCrashSectionCallable("Name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return CommandBlockLogic.this.getName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            this.successCount = 0;
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getName()
    {
        return this.customName;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(this.getName());
    }

    public void setName(String p_145754_1_)
    {
        this.customName = p_145754_1_;
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage(IChatComponent component)
    {
        if (this.trackOutput && this.getEntityWorld() != null && !this.getEntityWorld().isRemote)
        {
            this.lastOutput = (new ChatComponentText("[" + timestampFormat.format(new Date()) + "] ")).appendSibling(component);
            this.updateCommand();
        }
    }

    /**
     * Returns true if the command sender should be sent feedback about executed commands
     */
    public boolean sendCommandFeedback()
    {
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        return minecraftserver == null || !minecraftserver.isAnvilFileSet() || minecraftserver.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
    }

    public void setCommandStat(CommandResultStats.Type type, int amount)
    {
        this.resultStats.func_179672_a(this, type, amount);
    }

    public abstract void updateCommand();

    public abstract int func_145751_f();

    public abstract void func_145757_a(ByteBuf p_145757_1_);

    public void setLastOutput(IChatComponent lastOutputMessage)
    {
        this.lastOutput = lastOutputMessage;
    }

    public void setTrackOutput(boolean shouldTrackOutput)
    {
        this.trackOutput = shouldTrackOutput;
    }

    public boolean shouldTrackOutput()
    {
        return this.trackOutput;
    }

    public boolean tryOpenEditCommandBlock(EntityPlayer playerIn)
    {
        if (!playerIn.capabilities.isCreativeMode)
        {
            return false;
        }
        else
        {
            if (playerIn.getEntityWorld().isRemote)
            {
                playerIn.openEditCommandBlock(this);
            }

            return true;
        }
    }

    public CommandResultStats getCommandResultStats()
    {
        return this.resultStats;
    }
}
