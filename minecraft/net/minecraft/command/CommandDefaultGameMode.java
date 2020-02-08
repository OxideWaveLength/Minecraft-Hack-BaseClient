package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandDefaultGameMode extends CommandGameMode {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "defaultgamemode";
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.defaultgamemode.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
		} else {
			WorldSettings.GameType worldsettings$gametype = this.getGameModeFromCommand(sender, args[0]);
			this.setGameType(worldsettings$gametype);
			notifyOperators(sender, this, "commands.defaultgamemode.success", new Object[] { new ChatComponentTranslation("gameMode." + worldsettings$gametype.getName(), new Object[0]) });
		}
	}

	protected void setGameType(WorldSettings.GameType p_71541_1_) {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		minecraftserver.setGameType(p_71541_1_);

		if (minecraftserver.getForceGamemode()) {
			for (EntityPlayerMP entityplayermp : MinecraftServer.getServer().getConfigurationManager().func_181057_v()) {
				entityplayermp.setGameType(p_71541_1_);
				entityplayermp.fallDistance = 0.0F;
			}
		}
	}
}
