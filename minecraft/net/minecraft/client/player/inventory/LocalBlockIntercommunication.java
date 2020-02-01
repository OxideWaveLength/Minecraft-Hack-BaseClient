package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication implements IInteractionObject
{
    private String guiID;
    private IChatComponent displayName;

    public LocalBlockIntercommunication(String guiIdIn, IChatComponent displayNameIn)
    {
        this.guiID = guiIdIn;
        this.displayName = displayNameIn;
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getName()
    {
        return this.displayName.getUnformattedText();
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return true;
    }

    public String getGuiID()
    {
        return this.guiID;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        return this.displayName;
    }
}
