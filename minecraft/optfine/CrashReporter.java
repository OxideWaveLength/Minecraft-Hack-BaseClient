package optfine;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;

public class CrashReporter
{
    public static void onCrashReport(CrashReport p_onCrashReport_0_)
    {
        try
        {
            GameSettings gamesettings = Config.getGameSettings();

            if (gamesettings == null)
            {
                return;
            }

            if (!gamesettings.snooperEnabled)
            {
                return;
            }

            String s = "http://optifine.net/crashReport";
            String s1 = makeReport(p_onCrashReport_0_);
            byte[] abyte = s1.getBytes("ASCII");
            IFileUploadListener ifileuploadlistener = new IFileUploadListener()
            {
                public void fileUploadFinished(String p_fileUploadFinished_1_, byte[] p_fileUploadFinished_2_, Throwable p_fileUploadFinished_3_)
                {
                }
            };
            Map map = new HashMap();
            map.put("OF-Version", Config.getVersion());
            map.put("OF-Summary", makeSummary(p_onCrashReport_0_));
            FileUploadThread fileuploadthread = new FileUploadThread(s, map, abyte, ifileuploadlistener);
            fileuploadthread.setPriority(10);
            fileuploadthread.start();
            Thread.sleep(1000L);
        }
        catch (Exception exception)
        {
            Config.dbg(exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    private static String makeReport(CrashReport p_makeReport_0_)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("OptiFineVersion: " + Config.getVersion() + "\n");
        stringbuffer.append("Summary: " + makeSummary(p_makeReport_0_) + "\n");
        stringbuffer.append("\n");
        stringbuffer.append(p_makeReport_0_.getCompleteReport());
        stringbuffer.append("\n");
        stringbuffer.append("OpenGlVersion: " + Config.openGlVersion + "\n");
        stringbuffer.append("OpenGlRenderer: " + Config.openGlRenderer + "\n");
        stringbuffer.append("OpenGlVendor: " + Config.openGlVendor + "\n");
        stringbuffer.append("CpuCount: " + Config.getAvailableProcessors() + "\n");
        return stringbuffer.toString();
    }

    private static String makeSummary(CrashReport p_makeSummary_0_)
    {
        Throwable throwable = p_makeSummary_0_.getCrashCause();

        if (throwable == null)
        {
            return "Unknown";
        }
        else
        {
            StackTraceElement[] astacktraceelement = throwable.getStackTrace();
            String s = "unknown";

            if (astacktraceelement.length > 0)
            {
                s = astacktraceelement[0].toString().trim();
            }

            String s1 = throwable.getClass().getName() + ": " + throwable.getMessage() + " (" + p_makeSummary_0_.getDescription() + ")" + " [" + s + "]";
            return s1;
        }
    }
}
