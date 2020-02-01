package net.minecraft.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandTitle extends CommandBase
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "title";
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
        return "commands.title.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.title.usage", new Object[0]);
        }
        else
        {
            if (args.length < 3)
            {
                if ("title".equals(args[1]) || "subtitle".equals(args[1]))
                {
                    throw new WrongUsageException("commands.title.usage.title", new Object[0]);
                }

                if ("times".equals(args[1]))
                {
                    throw new WrongUsageException("commands.title.usage.times", new Object[0]);
                }
            }

            EntityPlayerMP entityplayermp = getPlayer(sender, args[0]);
            S45PacketTitle.Type s45packettitle$type = S45PacketTitle.Type.byName(args[1]);

            if (s45packettitle$type != S45PacketTitle.Type.CLEAR && s45packettitle$type != S45PacketTitle.Type.RESET)
            {
                if (s45packettitle$type == S45PacketTitle.Type.TIMES)
                {
                    if (args.length != 5)
                    {
                        throw new WrongUsageException("commands.title.usage", new Object[0]);
                    }
                    else
                    {
                        int i = parseInt(args[2]);
                        int j = parseInt(args[3]);
                        int k = parseInt(args[4]);
                        S45PacketTitle s45packettitle2 = new S45PacketTitle(i, j, k);
                        entityplayermp.playerNetServerHandler.sendPacket(s45packettitle2);
                        notifyOperators(sender, this, "commands.title.success", new Object[0]);
                    }
                }
                else if (args.length < 3)
                {
                    throw new WrongUsageException("commands.title.usage", new Object[0]);
                }
                else
                {
                    String s = buildString(args, 2);
                    IChatComponent ichatcomponent;

                    try
                    {
                        ichatcomponent = IChatComponent.Serializer.jsonToComponent(s);
                    }
                    catch (JsonParseException jsonparseexception)
                    {
                        Throwable throwable = ExceptionUtils.getRootCause(jsonparseexception);
                        throw new SyntaxErrorException("commands.tellraw.jsonException", new Object[] {throwable == null ? "" : throwable.getMessage()});
                    }

                    S45PacketTitle s45packettitle1 = new S45PacketTitle(s45packettitle$type, ChatComponentProcessor.processComponent(sender, ichatcomponent, entityplayermp));
                    entityplayermp.playerNetServerHandler.sendPacket(s45packettitle1);
                    notifyOperators(sender, this, "commands.title.success", new Object[0]);
                }
            }
            else if (args.length != 2)
            {
                throw new WrongUsageException("commands.title.usage", new Object[0]);
            }
            else
            {
                S45PacketTitle s45packettitle = new S45PacketTitle(s45packettitle$type, (IChatComponent)null);
                entityplayermp.playerNetServerHandler.sendPacket(s45packettitle);
                notifyOperators(sender, this, "commands.title.success", new Object[0]);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, S45PacketTitle.Type.getNames()) : null);
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
