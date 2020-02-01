package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class S3EPacketTeams implements Packet<INetHandlerPlayClient>
{
    private String field_149320_a = "";
    private String field_149318_b = "";
    private String field_149319_c = "";
    private String field_149316_d = "";
    private String field_179816_e;
    private int field_179815_f;
    private Collection<String> field_149317_e;
    private int field_149314_f;
    private int field_149315_g;

    public S3EPacketTeams()
    {
        this.field_179816_e = Team.EnumVisible.ALWAYS.field_178830_e;
        this.field_179815_f = -1;
        this.field_149317_e = Lists.<String>newArrayList();
    }

    public S3EPacketTeams(ScorePlayerTeam p_i45225_1_, int p_i45225_2_)
    {
        this.field_179816_e = Team.EnumVisible.ALWAYS.field_178830_e;
        this.field_179815_f = -1;
        this.field_149317_e = Lists.<String>newArrayList();
        this.field_149320_a = p_i45225_1_.getRegisteredName();
        this.field_149314_f = p_i45225_2_;

        if (p_i45225_2_ == 0 || p_i45225_2_ == 2)
        {
            this.field_149318_b = p_i45225_1_.getTeamName();
            this.field_149319_c = p_i45225_1_.getColorPrefix();
            this.field_149316_d = p_i45225_1_.getColorSuffix();
            this.field_149315_g = p_i45225_1_.func_98299_i();
            this.field_179816_e = p_i45225_1_.getNameTagVisibility().field_178830_e;
            this.field_179815_f = p_i45225_1_.getChatFormat().getColorIndex();
        }

        if (p_i45225_2_ == 0)
        {
            this.field_149317_e.addAll(p_i45225_1_.getMembershipCollection());
        }
    }

    public S3EPacketTeams(ScorePlayerTeam p_i45226_1_, Collection<String> p_i45226_2_, int p_i45226_3_)
    {
        this.field_179816_e = Team.EnumVisible.ALWAYS.field_178830_e;
        this.field_179815_f = -1;
        this.field_149317_e = Lists.<String>newArrayList();

        if (p_i45226_3_ != 3 && p_i45226_3_ != 4)
        {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        else if (p_i45226_2_ != null && !p_i45226_2_.isEmpty())
        {
            this.field_149314_f = p_i45226_3_;
            this.field_149320_a = p_i45226_1_.getRegisteredName();
            this.field_149317_e.addAll(p_i45226_2_);
        }
        else
        {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.field_149320_a = buf.readStringFromBuffer(16);
        this.field_149314_f = buf.readByte();

        if (this.field_149314_f == 0 || this.field_149314_f == 2)
        {
            this.field_149318_b = buf.readStringFromBuffer(32);
            this.field_149319_c = buf.readStringFromBuffer(16);
            this.field_149316_d = buf.readStringFromBuffer(16);
            this.field_149315_g = buf.readByte();
            this.field_179816_e = buf.readStringFromBuffer(32);
            this.field_179815_f = buf.readByte();
        }

        if (this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4)
        {
            int i = buf.readVarIntFromBuffer();

            for (int j = 0; j < i; ++j)
            {
                this.field_149317_e.add(buf.readStringFromBuffer(40));
            }
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.field_149320_a);
        buf.writeByte(this.field_149314_f);

        if (this.field_149314_f == 0 || this.field_149314_f == 2)
        {
            buf.writeString(this.field_149318_b);
            buf.writeString(this.field_149319_c);
            buf.writeString(this.field_149316_d);
            buf.writeByte(this.field_149315_g);
            buf.writeString(this.field_179816_e);
            buf.writeByte(this.field_179815_f);
        }

        if (this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4)
        {
            buf.writeVarIntToBuffer(this.field_149317_e.size());

            for (String s : this.field_149317_e)
            {
                buf.writeString(s);
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleTeams(this);
    }

    public String func_149312_c()
    {
        return this.field_149320_a;
    }

    public String func_149306_d()
    {
        return this.field_149318_b;
    }

    public String func_149311_e()
    {
        return this.field_149319_c;
    }

    public String func_149309_f()
    {
        return this.field_149316_d;
    }

    public Collection<String> func_149310_g()
    {
        return this.field_149317_e;
    }

    public int func_149307_h()
    {
        return this.field_149314_f;
    }

    public int func_149308_i()
    {
        return this.field_149315_g;
    }

    public int func_179813_h()
    {
        return this.field_179815_f;
    }

    public String func_179814_i()
    {
        return this.field_179816_e;
    }
}
