package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandToggleDownfall extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "toggledownfall";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.downfall.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        this.toggleDownfall();
        notifyOperators(sender, this, "commands.downfall.success", new Object[0]);
    }

    /**
     * Toggle rain and enable thundering.
     */
    protected void toggleDownfall()
    {
        WorldInfo worldinfo = MinecraftServer.getServer().worldServers[0].getWorldInfo();
        worldinfo.setRaining(!worldinfo.isRaining());
    }
}
