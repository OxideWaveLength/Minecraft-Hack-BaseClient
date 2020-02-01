package net.minecraft.network.rcon;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender
{
    /** Single instance of RConConsoleSource */
    private static final RConConsoleSource instance = new RConConsoleSource();

    /** RCon string buffer for log. */
    private StringBuffer buffer = new StringBuffer();

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getName()
    {
        return "Rcon";
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(this.getName());
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage(IChatComponent component)
    {
        this.buffer.append(component.getUnformattedText());
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    public BlockPos getPosition()
    {
        return new BlockPos(0, 0, 0);
    }

    /**
     * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return 0.0D,
     * 0.0D, 0.0D
     */
    public Vec3 getPositionVector()
    {
        return new Vec3(0.0D, 0.0D, 0.0D);
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the overworld
     */
    public World getEntityWorld()
    {
        return MinecraftServer.getServer().getEntityWorld();
    }

    /**
     * Returns the entity associated with the command sender. MAY BE NULL!
     */
    public Entity getCommandSenderEntity()
    {
        return null;
    }

    /**
     * Returns true if the command sender should be sent feedback about executed commands
     */
    public boolean sendCommandFeedback()
    {
        return true;
    }

    public void setCommandStat(CommandResultStats.Type type, int amount)
    {
    }
}
