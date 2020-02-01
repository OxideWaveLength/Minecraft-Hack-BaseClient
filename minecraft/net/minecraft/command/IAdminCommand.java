package net.minecraft.command;

public interface IAdminCommand
{
    /**
     * Send an informative message to the server operators
     */
    void notifyOperators(ICommandSender sender, ICommand command, int flags, String msgFormat, Object... msgParams);
}
