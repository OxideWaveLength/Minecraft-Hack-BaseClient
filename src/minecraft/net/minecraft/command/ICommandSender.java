package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface ICommandSender {
	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	String getName();

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username
	 * in chat
	 */
	IChatComponent getDisplayName();

	/**
	 * Send a chat message to the CommandSender
	 */
	void addChatMessage(IChatComponent component);

	/**
	 * Returns {@code true} if the CommandSender is allowed to execute the command,
	 * {@code false} if not
	 */
	boolean canCommandSenderUseCommand(int permLevel, String commandName);

	/**
	 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are
	 * not an entity in the world, return the coordinates 0, 0, 0
	 */
	BlockPos getPosition();

	/**
	 * Get the position vector. <b>{@code null} is not allowed!</b> If you are not
	 * an entity in the world, return 0.0D, 0.0D, 0.0D
	 */
	Vec3 getPositionVector();

	/**
	 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are
	 * not an entity in the world, return the overworld
	 */
	World getEntityWorld();

	/**
	 * Returns the entity associated with the command sender. MAY BE NULL!
	 */
	Entity getCommandSenderEntity();

	/**
	 * Returns true if the command sender should be sent feedback about executed
	 * commands
	 */
	boolean sendCommandFeedback();

	void setCommandStat(CommandResultStats.Type type, int amount);
}
