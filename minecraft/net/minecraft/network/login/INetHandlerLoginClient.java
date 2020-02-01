package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;

public interface INetHandlerLoginClient extends INetHandler
{
    void handleEncryptionRequest(S01PacketEncryptionRequest packetIn);

    void handleLoginSuccess(S02PacketLoginSuccess packetIn);

    void handleDisconnect(S00PacketDisconnect packetIn);

    void handleEnableCompression(S03PacketEnableCompression packetIn);
}
