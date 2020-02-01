package optfine;

public class FileDownloadThread extends Thread
{
    private String urlString = null;
    private IFileDownloadListener listener = null;

    public FileDownloadThread(String p_i32_1_, IFileDownloadListener p_i32_2_)
    {
        this.urlString = p_i32_1_;
        this.listener = p_i32_2_;
    }

    public void run()
    {
        try
        {
            byte[] abyte = HttpUtils.get(this.urlString);
            this.listener.fileDownloadFinished(this.urlString, abyte, (Throwable)null);
        }
        catch (Exception exception)
        {
            this.listener.fileDownloadFinished(this.urlString, (byte[])null, exception);
        }
    }

    public String getUrlString()
    {
        return this.urlString;
    }

    public IFileDownloadListener getListener()
    {
        return this.listener;
    }
}
