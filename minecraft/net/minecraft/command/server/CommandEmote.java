package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandEmote extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "me";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.me.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.me.usage", new Object[0]);
        }
        else
        {
            IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 0, !(sender instanceof EntityPlayer));
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.emote", new Object[] {sender.getDisplayName(), ichatcomponent}));
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }
}
