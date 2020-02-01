package net.minecraft.world;

import net.minecraft.util.IChatComponent;

public interface IWorldNameable
{
    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    String getName();

    /**
     * Returns true if this thing is named
     */
    boolean hasCustomName();

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    IChatComponent getDisplayName();
}
