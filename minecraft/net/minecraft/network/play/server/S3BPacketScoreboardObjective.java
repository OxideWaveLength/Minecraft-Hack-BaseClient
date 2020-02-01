package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective implements Packet<INetHandlerPlayClient>
{
    private String objectiveName;
    private String objectiveValue;
    private IScoreObjectiveCriteria.EnumRenderType type;
    private int field_149342_c;

    public S3BPacketScoreboardObjective()
    {
    }

    public S3BPacketScoreboardObjective(ScoreObjective p_i45224_1_, int p_i45224_2_)
    {
        this.objectiveName = p_i45224_1_.getName();
        this.objectiveValue = p_i45224_1_.getDisplayName();
        this.type = p_i45224_1_.getCriteria().getRenderType();
        this.field_149342_c = p_i45224_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.objectiveName = buf.readStringFromBuffer(16);
        this.field_149342_c = buf.readByte();

        if (this.field_149342_c == 0 || this.field_149342_c == 2)
        {
            this.objectiveValue = buf.readStringFromBuffer(32);
            this.type = IScoreObjectiveCriteria.EnumRenderType.func_178795_a(buf.readStringFromBuffer(16));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.objectiveName);
        buf.writeByte(this.field_149342_c);

        if (this.field_149342_c == 0 || this.field_149342_c == 2)
        {
            buf.writeString(this.objectiveValue);
            buf.writeString(this.type.func_178796_a());
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleScoreboardObjective(this);
    }

    public String func_149339_c()
    {
        return this.objectiveName;
    }

    public String func_149337_d()
    {
        return this.objectiveValue;
    }

    public int func_149338_e()
    {
        return this.field_149342_c;
    }

    public IScoreObjectiveCriteria.EnumRenderType func_179817_d()
    {
        return this.type;
    }
}
