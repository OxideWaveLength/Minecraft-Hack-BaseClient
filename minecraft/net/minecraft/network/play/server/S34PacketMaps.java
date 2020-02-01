package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class S34PacketMaps implements Packet<INetHandlerPlayClient>
{
    private int mapId;
    private byte mapScale;
    private Vec4b[] mapVisiblePlayersVec4b;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    public S34PacketMaps()
    {
    }

    public S34PacketMaps(int mapIdIn, byte scale, Collection<Vec4b> visiblePlayers, byte[] colors, int minX, int minY, int maxX, int maxY)
    {
        this.mapId = mapIdIn;
        this.mapScale = scale;
        this.mapVisiblePlayersVec4b = (Vec4b[])visiblePlayers.toArray(new Vec4b[visiblePlayers.size()]);
        this.mapMinX = minX;
        this.mapMinY = minY;
        this.mapMaxX = maxX;
        this.mapMaxY = maxY;
        this.mapDataBytes = new byte[maxX * maxY];

        for (int i = 0; i < maxX; ++i)
        {
            for (int j = 0; j < maxY; ++j)
            {
                this.mapDataBytes[i + j * maxX] = colors[minX + i + (minY + j) * 128];
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.mapId = buf.readVarIntFromBuffer();
        this.mapScale = buf.readByte();
        this.mapVisiblePlayersVec4b = new Vec4b[buf.readVarIntFromBuffer()];

        for (int i = 0; i < this.mapVisiblePlayersVec4b.length; ++i)
        {
            short short1 = (short)buf.readByte();
            this.mapVisiblePlayersVec4b[i] = new Vec4b((byte)(short1 >> 4 & 15), buf.readByte(), buf.readByte(), (byte)(short1 & 15));
        }

        this.mapMaxX = buf.readUnsignedByte();

        if (this.mapMaxX > 0)
        {
            this.mapMaxY = buf.readUnsignedByte();
            this.mapMinX = buf.readUnsignedByte();
            this.mapMinY = buf.readUnsignedByte();
            this.mapDataBytes = buf.readByteArray();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.mapId);
        buf.writeByte(this.mapScale);
        buf.writeVarIntToBuffer(this.mapVisiblePlayersVec4b.length);

        for (Vec4b vec4b : this.mapVisiblePlayersVec4b)
        {
            buf.writeByte((vec4b.func_176110_a() & 15) << 4 | vec4b.func_176111_d() & 15);
            buf.writeByte(vec4b.func_176112_b());
            buf.writeByte(vec4b.func_176113_c());
        }

        buf.writeByte(this.mapMaxX);

        if (this.mapMaxX > 0)
        {
            buf.writeByte(this.mapMaxY);
            buf.writeByte(this.mapMinX);
            buf.writeByte(this.mapMinY);
            buf.writeByteArray(this.mapDataBytes);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleMaps(this);
    }

    public int getMapId()
    {
        return this.mapId;
    }

    /**
     * Sets new MapData from the packet to given MapData param
     */
    public void setMapdataTo(MapData mapdataIn)
    {
        mapdataIn.scale = this.mapScale;
        mapdataIn.mapDecorations.clear();

        for (int i = 0; i < this.mapVisiblePlayersVec4b.length; ++i)
        {
            Vec4b vec4b = this.mapVisiblePlayersVec4b[i];
            mapdataIn.mapDecorations.put("icon-" + i, vec4b);
        }

        for (int j = 0; j < this.mapMaxX; ++j)
        {
            for (int k = 0; k < this.mapMaxY; ++k)
            {
                mapdataIn.colors[this.mapMinX + j + (this.mapMinY + k) * 128] = this.mapDataBytes[j + k * this.mapMaxX];
            }
        }
    }
}
