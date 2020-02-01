package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOn extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "save-on";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.save-on.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        boolean flag = false;

        for (int i = 0; i < minecraftserver.worldServers.length; ++i)
        {
            if (minecraftserver.worldServers[i] != null)
            {
                WorldServer worldserver = minecraftserver.worldServers[i];

                if (worldserver.disableLevelSaving)
                {
                    worldserver.disableLevelSaving = false;
                    flag = true;
                }
            }
        }

        if (flag)
        {
            notifyOperators(sender, this, "commands.save.enabled", new Object[0]);
        }
        else
        {
            throw new CommandException("commands.save-on.alreadyOn", new Object[0]);
        }
    }
}
