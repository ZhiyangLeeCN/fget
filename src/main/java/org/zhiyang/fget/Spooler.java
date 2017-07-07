package org.zhiyang.fget;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhiyang.fget.common.ThreadFactoryImpl;
import org.zhiyang.fget.prospector.Harvester;
import org.zhiyang.fget.prospector.reporter.HarvesterReporterFactory;
import org.zhiyang.fget.store.FileStore;

import java.util.concurrent.*;

/**
 * @author lizhiyang
 */
public class Spooler {

    private static Logger log = LoggerFactory.getLogger(Spooler.class);

    private FGetConfig fGetConfig;

    private ExecutorService harvesterExecutor;

    private BlockingQueue<Runnable> harvesterExecutorQueue;

    private ExecutorService scheduleExecutor;

    private FileStore fileStore;

    private HarvesterReporterFactory reporterFactory;

    private ConcurrentMap</*URL MD5*/String, Harvester> harvesterTable;

    public Spooler(@NotNull FGetConfig fGetConfig, @NotNull FileStore fileStore,
                   @NotNull HarvesterReporterFactory reporterFactory) {

        this.fGetConfig = fGetConfig;
        this.fileStore = fileStore;
        this.reporterFactory = reporterFactory;
        this.harvesterTable = new ConcurrentHashMap<>();

        if (this.fGetConfig.isSpoolerExecutorQueueHaveBound()) {
            this.harvesterExecutorQueue = new ArrayBlockingQueue<Runnable>(
                    this.fGetConfig.getSpoolerExecutorQueueSzie()
            );
        } else {
            this.harvesterExecutorQueue = new LinkedBlockingQueue<>();
        }

    }

    public boolean initialize() {

        this.harvesterExecutor = new ThreadPoolExecutor(
                this.fGetConfig.getSpoolerExecutorCorePoolSize(),
                this.fGetConfig.getSpoolerExecutorMaximumPoolSize(),
                this.fGetConfig.getSpoolerExecutorKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                this.harvesterExecutorQueue,
                new ThreadFactoryImpl("HarvesterThread_")
        );

        this.scheduleExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryImpl("SpoolerScheduleThread_")
        );

        return true;
    }
}
