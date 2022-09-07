package viamcp;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import viamcp.gui.AsyncVersionSlider;
import viamcp.loader.MCPBackwardsLoader;
import viamcp.loader.MCPRewindLoader;
import viamcp.loader.MCPViaLoader;
import viamcp.platform.MCPViaInjector;
import viamcp.platform.MCPViaPlatform;
import viamcp.utils.JLoggerToLog4j;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaMCP {
    public final static int PROTOCOL_VERSION = 47;
    private static final ViaMCP instance = new ViaMCP();
    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaMCP"));
    private final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
    /**
     * Version Slider that works Asynchronously with the Version GUI
     * Please initialize this before usage with initAsyncSlider() or initAsyncSlider(x, y, width (min. 110), height)
     */
    public AsyncVersionSlider asyncSlider;
    private ExecutorService ASYNC_EXEC;
    private EventLoop EVENT_LOOP;

    private File file;
    private int version;
    private String lastServer;

    public static ViaMCP getInstance() {
        return instance;
    }

    public void start() {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaMCP-%d").build();
        ASYNC_EXEC = Executors.newFixedThreadPool(8, factory);

        EVENT_LOOP = new LocalEventLoopGroup(1, factory).next();
        EVENT_LOOP.submit(INIT_FUTURE::join);

        setVersion(PROTOCOL_VERSION);
        this.file = new File("ViaMCP");
        if (this.file.mkdir()) {
            this.getjLogger().info("Creating ViaMCP Folder");
        }

        Via.init(ViaManagerImpl.builder().injector(new MCPViaInjector()).loader(new MCPViaLoader()).platform(new MCPViaPlatform(file)).build());

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        new MCPBackwardsLoader(file);
        new MCPRewindLoader(file);

        INIT_FUTURE.complete(null);
    }

    public void initAsyncSlider() {
        asyncSlider = new AsyncVersionSlider(-1, 5, 5, 110, 20);
    }

    public void initAsyncSlider(int x, int y, int width, int height) {
        asyncSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height);
    }

    public Logger getjLogger() {
        return jLogger;
    }

    public CompletableFuture<Void> getInitFuture() {
        return INIT_FUTURE;
    }

    public ExecutorService getAsyncExecutor() {
        return ASYNC_EXEC;
    }

    public EventLoop getEventLoop() {
        return EVENT_LOOP;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLastServer() {
        return lastServer;
    }

    public void setLastServer(String lastServer) {
        this.lastServer = lastServer;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
