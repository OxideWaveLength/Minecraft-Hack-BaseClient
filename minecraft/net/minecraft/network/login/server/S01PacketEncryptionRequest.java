package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class S01PacketEncryptionRequest implements Packet<INetHandlerLoginClient>
{
    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;

    public S01PacketEncryptionRequest()
    {
    }

    public S01PacketEncryptionRequest(String serverId, PublicKey key, byte[] verifyToken)
    {
        this.hashedServerId = serverId;
        this.publicKey = key;
        this.verifyToken = verifyToken;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.hashedServerId = buf.readStringFromBuffer(20);
        this.publicKey = CryptManager.decodePublicKey(buf.readByteArray());
        this.verifyToken = buf.readByteArray();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.hashedServerId);
        buf.writeByteArray(this.publicKey.getEncoded());
        buf.writeByteArray(this.verifyToken);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginClient handler)
    {
        handler.handleEncryptionRequest(this);
    }

    public String getServerId()
    {
        return this.hashedServerId;
    }

    public PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    public byte[] getVerifyToken()
    {
        return this.verifyToken;
    }
}
