package net.minecraft.client.stream;

import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public class NullStream implements IStream
{
    private final Throwable field_152938_a;

    public NullStream(Throwable p_i1006_1_)
    {
        this.field_152938_a = p_i1006_1_;
    }

    /**
     * Shuts down a steam
     */
    public void shutdownStream()
    {
    }

    public void func_152935_j()
    {
    }

    public void func_152922_k()
    {
    }

    public boolean func_152936_l()
    {
        return false;
    }

    public boolean isReadyToBroadcast()
    {
        return false;
    }

    public boolean isBroadcasting()
    {
        return false;
    }

    public void func_152911_a(Metadata p_152911_1_, long p_152911_2_)
    {
    }

    public void func_176026_a(Metadata p_176026_1_, long p_176026_2_, long p_176026_4_)
    {
    }

    public boolean isPaused()
    {
        return false;
    }

    public void requestCommercial()
    {
    }

    /**
     * pauses a stream
     */
    public void pause()
    {
    }

    /**
     * unpauses a stream
     */
    public void unpause()
    {
    }

    public void updateStreamVolume()
    {
    }

    public void func_152930_t()
    {
    }

    public void stopBroadcasting()
    {
    }

    public IngestServer[] func_152925_v()
    {
        return new IngestServer[0];
    }

    public void func_152909_x()
    {
    }

    public IngestServerTester func_152932_y()
    {
        return null;
    }

    public boolean func_152908_z()
    {
        return false;
    }

    public int func_152920_A()
    {
        return 0;
    }

    public boolean func_152927_B()
    {
        return false;
    }

    public String func_152921_C()
    {
        return null;
    }

    public ChatUserInfo func_152926_a(String p_152926_1_)
    {
        return null;
    }

    public void func_152917_b(String p_152917_1_)
    {
    }

    public boolean func_152928_D()
    {
        return false;
    }

    public ErrorCode func_152912_E()
    {
        return null;
    }

    public boolean func_152913_F()
    {
        return false;
    }

    /**
     * mutes or unmutes the microphone based on the boolean parameter passed into the method
     */
    public void muteMicrophone(boolean p_152910_1_)
    {
    }

    public boolean func_152929_G()
    {
        return false;
    }

    public IStream.AuthFailureReason func_152918_H()
    {
        return IStream.AuthFailureReason.ERROR;
    }

    public Throwable func_152937_a()
    {
        return this.field_152938_a;
    }
}
