package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public interface INetHandlerPlayServer extends INetHandler
{
    void handleAnimation(C0APacketAnimation packetIn);

    /**
     * Process chat messages (broadcast back to clients) and commands (executes)
     */
    void processChatMessage(C01PacketChatMessage packetIn);

    /**
     * Retrieves possible tab completions for the requested command string and sends them to the client
     */
    void processTabComplete(C14PacketTabComplete packetIn);

    /**
     * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or
     * acquiring 'open inventory' achievement
     */
    void processClientStatus(C16PacketClientStatus packetIn);

    /**
     * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
     * and whether to show the cape
     */
    void processClientSettings(C15PacketClientSettings packetIn);

    /**
     * Received in response to the server requesting to confirm that the client-side open container matches the servers'
     * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
     * contents
     */
    void processConfirmTransaction(C0FPacketConfirmTransaction packetIn);

    /**
     * Enchants the item identified by the packet given some convoluted conditions (matching window, which
     * should/shouldn't be in use?)
     */
    void processEnchantItem(C11PacketEnchantItem packetIn);

    /**
     * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
     * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
     * the same open container/inventory
     */
    void processClickWindow(C0EPacketClickWindow packetIn);

    /**
     * Processes the client closing windows (container)
     */
    void processCloseWindow(C0DPacketCloseWindow packetIn);

    /**
     * Synchronizes serverside and clientside book contents and signing
     */
    void processVanilla250Packet(C17PacketCustomPayload packetIn);

    /**
     * Processes interactions ((un)leashing, opening command block GUI) and attacks on an entity with players currently
     * equipped item
     */
    void processUseEntity(C02PacketUseEntity packetIn);

    /**
     * Updates a players' ping statistics
     */
    void processKeepAlive(C00PacketKeepAlive packetIn);

    /**
     * Processes clients perspective on player positioning and/or orientation
     */
    void processPlayer(C03PacketPlayer packetIn);

    /**
     * Processes a player starting/stopping flying
     */
    void processPlayerAbilities(C13PacketPlayerAbilities packetIn);

    /**
     * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items?. (0:
     * initiated, 1: reinitiated, 2? , 3-4 drop item (respectively without or with player control), 5: stopped; x,y,z,
     * side clicked on;)
     */
    void processPlayerDigging(C07PacketPlayerDigging packetIn);

    /**
     * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
     * height of the horse the player is riding
     */
    void processEntityAction(C0BPacketEntityAction packetIn);

    /**
     * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
     * flying/sprinting
     */
    void processInput(C0CPacketInput packetIn);

    /**
     * Updates which quickbar slot is selected
     */
    void processHeldItemChange(C09PacketHeldItemChange packetIn);

    /**
     * Update the server with an ItemStack in a slot.
     */
    void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn);

    void processUpdateSign(C12PacketUpdateSign packetIn);

    /**
     * Processes block placement and block activation (anvil, furnace, etc.)
     */
    void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn);

    void handleSpectate(C18PacketSpectate packetIn);

    void handleResourcePackStatus(C19PacketResourcePackStatus packetIn);
}
