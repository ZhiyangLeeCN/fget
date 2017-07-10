package org.zhiyang.fget;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhiyang.fget.common.MixAll;
import org.zhiyang.fget.prospector.Spooler;
import org.zhiyang.fget.prospector.reporter.HarvesterReporterFactory;
import org.zhiyang.fget.prospector.reporter.impl.SSDBReporterFactory;
import org.zhiyang.fget.rpc.RpcServer;
import org.zhiyang.fget.store.AliyunOSS;
import org.zhiyang.fget.store.FileStore;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lizhiyang
 */
public class Boot {

    private static FileStore fileStore;
    private static Spooler spooler;

    private static Logger log = LoggerFactory.getLogger(Boot.class);

    private static Options buildSocksServerOptions()
    {
        Options options = new Options();

        //可选，指定socks(yaml)配置文件的位置
        options.addOption(new Option("c", true,"fget server config file"));

        //可选，打印配置内容
        options.addOption(new Option("p", false, "print socks server config"));

        return options;

    }

    public static void main(String[] args) {

        try {

            CommandLine commandLine = MixAll.parseCommandLine(buildSocksServerOptions(), args);
            if (commandLine == null) {
                System.err.println("init command line error");
            } else {

                //初始化配置信息
                final FGetConfig fGetConfig = new FGetConfig();
                InputStream in = new BufferedInputStream(new FileInputStream(commandLine.getOptionValue("c")));
                Properties properties = new Properties();
                properties.load(in);
                MixAll.properties2Object(properties, fGetConfig);

                fileStore = new AliyunOSS(
                        fGetConfig.getOssEndpoint(),
                        fGetConfig.getOssAccessKeyId(),
                        fGetConfig.getOssAccessKeySecret(),
                        fGetConfig.getOssBucketName()
                );
                fileStore.initialize();

                final HarvesterReporterFactory harvesterReporterFactory = new SSDBReporterFactory(fGetConfig);
                spooler = new Spooler(fGetConfig, fileStore, harvesterReporterFactory);
                spooler.initialize();

                final RpcServer rpcServer = new RpcServer(spooler);
                rpcServer.start();

                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                        private volatile boolean hasShutdown = false;
                        private AtomicInteger shutdownTimes = new AtomicInteger(0);

                        @Override
                        public void run() {
                            synchronized (this) {
                                log.info("shutdown hook was invoked, " + this.shutdownTimes.incrementAndGet());
                                if (!this.hasShutdown) {
                                    this.hasShutdown = true;
                                    long begineTime = System.currentTimeMillis();
                                    rpcServer.stop();
                                    spooler.shutdown();
                                    fileStore.shutdown();
                                    long consumingTimeTotal = System.currentTimeMillis() - begineTime;
                                    log.info("shutdown hook over, consuming time total(ms): " + consumingTimeTotal);
                                }
                            }
                        }
                    }
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
