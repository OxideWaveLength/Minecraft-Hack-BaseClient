package net.minecraft.command;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "difficulty";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.difficulty.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new WrongUsageException("commands.difficulty.usage", new Object[0]);
		} else {
			EnumDifficulty enumdifficulty = this.getDifficultyFromCommand(args[0]);
			MinecraftServer.getServer().setDifficultyForAllWorlds(enumdifficulty);
			notifyOperators(sender, this, "commands.difficulty.success", new Object[] { new ChatComponentTranslation(enumdifficulty.getDifficultyResourceKey(), new Object[0]) });
		}
	}

	protected EnumDifficulty getDifficultyFromCommand(String p_180531_1_) throws CommandException, NumberInvalidException {
		return !p_180531_1_.equalsIgnoreCase("peaceful") && !p_180531_1_.equalsIgnoreCase("p") ? (!p_180531_1_.equalsIgnoreCase("easy") && !p_180531_1_.equalsIgnoreCase("e") ? (!p_180531_1_.equalsIgnoreCase("normal") && !p_180531_1_.equalsIgnoreCase("n") ? (!p_180531_1_.equalsIgnoreCase("hard") && !p_180531_1_.equalsIgnoreCase("h") ? EnumDifficulty.getDifficultyEnum(parseInt(p_180531_1_, 0, 3)) : EnumDifficulty.HARD) : EnumDifficulty.NORMAL) : EnumDifficulty.EASY) : EnumDifficulty.PEACEFUL;
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "peaceful", "easy", "normal", "hard" }) : null;
	}
}
