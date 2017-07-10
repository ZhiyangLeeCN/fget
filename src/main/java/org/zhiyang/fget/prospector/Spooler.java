package org.zhiyang.fget.prospector;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhiyang.fget.FGetConfig;
import org.zhiyang.fget.common.ThreadFactoryImpl;
import org.zhiyang.fget.prospector.reporter.HarvesterReporter;
import org.zhiyang.fget.prospector.reporter.HarvesterReporterFactory;
import org.zhiyang.fget.prospector.reporter.ReporterInfo;
import org.zhiyang.fget.store.FileStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lizhiyang
 */
public class Spooler {

    private static Logger log = LoggerFactory.getLogger(Spooler.class);

    private FGetConfig fGetConfig;

    private ExecutorService harvesterExecutor;

    private ScheduledExecutorService scheduleExecutor;

    private BlockingQueue<Runnable> harvesterExecutorQueue;

    private FileStore fileStore;

    private HarvesterReporterFactory reporterFactory;

    private HarvesterReporter reporter;

    private ConcurrentMap</*URL MD5*/String, Harvester> harvesterTable;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Spooler(@NotNull FGetConfig fGetConfig, @NotNull FileStore fileStore,
                   @NotNull HarvesterReporterFactory reporterFactory) {

        this.fGetConfig = fGetConfig;
        this.fileStore = fileStore;
        this.reporterFactory = reporterFactory;
        this.harvesterTable = new ConcurrentHashMap<>();
        this.reporter = reporterFactory.newReporter();

        if (this.fGetConfig.isSpoolerExecutorQueueHaveBound()) {
            this.harvesterExecutorQueue = new ArrayBlockingQueue<Runnable>(
                    this.fGetConfig.getSpoolerExecutorQueueSize()
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

        this.scheduleExecutor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {

                HashMap<String, Harvester> table = new HashMap<>(Spooler.this.harvesterTable);

                for (Map.Entry<String, Harvester> entry : table.entrySet()) {

                    Harvester harvester = entry.getValue();

                    //如果任务完则保存结果并在任务列表中移除
                    if (harvester.isComplete()) {

                        Spooler.this.harvesterTable.remove(entry.getKey());
                    }

                    //汇报当前任务的完成状态
                    Spooler.this.reporter.report(new ReporterInfo(
                            harvester.id(),//任务唯一标识
                            harvester.getRealFileName(),//下载的真实文件名
                            harvester.getTargetSize(),//目标文件总大小
                            harvester.getReadSize(),//目前已经下载大小
                            harvester.isComplete(),//是否已经完成
                            harvester.isSuccess()//是否下载成功
                    ));

                }

            }

        }, 0,3000, TimeUnit.MILLISECONDS);

        return true;
    }

    public ReporterInfo query(String id) {
        HarvesterReporter reporter = null;
        try {
            this.lock.readLock().lockInterruptibly();
            reporter = this.reporterFactory.newReporter();
            return this.reporter.query(id);
        } catch (Exception e) {
            return null;
        } finally {
            this.lock.readLock().unlock();
            if (reporter != null) {
                reporter.shutdown();
            }
        }

    }

    @Nullable
    public String addHttpFile(String url) {

        String id = Harvester.getHttpFileUniqueStoreName(url);
        String savePath = this.fGetConfig.getStorePath();
        String filePath = savePath + "/" + id;
        if (this.fileStore.exist(filePath)) {
            return id;
        }

        try {

            if (this.lock.writeLock().tryLock(3000, TimeUnit.MILLISECONDS)) {
                Harvester harvester = this.harvesterTable.get(id);
                //检查如果任务完成但是是失败的，则移除重新添加下载任务
                if (harvester != null) {
                    if (harvester.isComplete() && !harvester.isSuccess()) {
                        this.harvesterTable.remove(harvester.id());
                        harvester = null;
                    }
                }
                if (harvester == null) {
                    harvester = new HttpFile(url, savePath, this.fileStore);
                    this.harvesterTable.put(id, harvester);
                    //先初始化进度汇报
                    this.reporter.report(new ReporterInfo(
                            harvester.id(),//下载任务标识ID
                            "loading...",//文件名，因为需要请求才能获取到文件名，所以先初始化为默认文本
                            0,//文件总大小，需要请求才能获取，先初始化为0
                            0,//文件已读大小，需要请求才能获取，先是初始化0
                            false,//是否完成
                            false//是否成功
                    ));
                    this.harvesterExecutor.execute(harvester);
                }

                return harvester.id();
            }

        } catch (InterruptedException | RejectedExecutionException e) {
            log.error("add http file task error, url:" + url, e);
        } finally {
            if (this.lock.writeLock().isHeldByCurrentThread()) {
                this.lock.writeLock().unlock();
            }
        }

        return null;

    }

    public void shutdown() {
        this.harvesterExecutor.shutdown();
        this.scheduleExecutor.shutdown();
        this.reporter.shutdown();
    }
}
