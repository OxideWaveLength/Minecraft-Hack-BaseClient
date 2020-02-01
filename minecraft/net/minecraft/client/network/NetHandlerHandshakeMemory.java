package net.minecraft.client.network;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer
{
    private final MinecraftServer mcServer;
    private final NetworkManager networkManager;

    public NetHandlerHandshakeMemory(MinecraftServer p_i45287_1_, NetworkManager p_i45287_2_)
    {
        this.mcServer = p_i45287_1_;
        this.networkManager = p_i45287_2_;
    }

    /**
     * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
     * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
     * must pass a versioncheck or receive a disconnect otherwise
     */
    public void processHandshake(C00Handshake packetIn)
    {
        this.networkManager.setConnectionState(packetIn.getRequestedState());
        this.networkManager.setNetHandler(new NetHandlerLoginServer(this.mcServer, this.networkManager));
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
    }
}
