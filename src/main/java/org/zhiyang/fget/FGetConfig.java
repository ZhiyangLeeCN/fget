package org.zhiyang.fget;

/**
 * @author lizhiyang
 */
public class FGetConfig {

    //阿里云OSS存储的网关地址
    private String ossEndpoint;

    //阿里云存储的AccessKey
    private String ossAccessKeyId;

    //阿里云存储的AccessKeySecret
    private String ossAccessKeySecret;

    //阿里云存储的BucketName
    private String ossBucketName;

    //SSDB的地址
    private String ssdbHost = "127.0.0.1";

    //SSDB的端口
    private int ssdbPort = 8888;

    //SSDB超时时间
    private int ssdbTimeout = 3000;

    //存储路径
    private String storePath = "files";

    //下载执行线程池的核心线程数量
    private int spoolerExecutorCorePoolSize = 100;

    //下载执行线程池的最大线程数量
    private int spoolerExecutorMaximumPoolSize = 100;

    //下载执行线程池大于核心线程数时多余的空闲线程最大存活时间(ms)
    private int spoolerExecutorKeepAliveTime = 60000;

    //下载执行线程池的队列是有界的还是无界的
    private boolean spoolerExecutorQueueHaveBound = true;

    //下载执行线程池的有界队列大小
    private int spoolerExecutorQueueSize = 100;

    public String getOssEndpoint() {
        return ossEndpoint;
    }

    public void setOssEndpoint(String ossEndpoint) {
        this.ossEndpoint = ossEndpoint;
    }

    public String getOssAccessKeyId() {
        return ossAccessKeyId;
    }

    public void setOssAccessKeyId(String ossAccessKeyId) {
        this.ossAccessKeyId = ossAccessKeyId;
    }

    public String getOssAccessKeySecret() {
        return ossAccessKeySecret;
    }

    public void setOssAccessKeySecret(String ossAccessKeySecret) {
        this.ossAccessKeySecret = ossAccessKeySecret;
    }

    public String getOssBucketName() {
        return ossBucketName;
    }

    public void setOssBucketName(String ossBucketName) {
        this.ossBucketName = ossBucketName;
    }

    public String getSsdbHost() {
        return ssdbHost;
    }

    public void setSsdbHost(String ssdbHost) {
        this.ssdbHost = ssdbHost;
    }

    public int getSsdbPort() {
        return ssdbPort;
    }

    public void setSsdbPort(int ssdbPort) {
        this.ssdbPort = ssdbPort;
    }

    public int getSsdbTimeout() {
        return ssdbTimeout;
    }

    public void setSsdbTimeout(int ssdbTimeout) {
        this.ssdbTimeout = ssdbTimeout;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public int getSpoolerExecutorCorePoolSize() {
        return spoolerExecutorCorePoolSize;
    }

    public void setSpoolerExecutorCorePoolSize(int spoolerExecutorCorePoolSize) {
        this.spoolerExecutorCorePoolSize = spoolerExecutorCorePoolSize;
    }

    public int getSpoolerExecutorMaximumPoolSize() {
        return spoolerExecutorMaximumPoolSize;
    }

    public void setSpoolerExecutorMaximumPoolSize(int spoolerExecutorMaximumPoolSize) {
        this.spoolerExecutorMaximumPoolSize = spoolerExecutorMaximumPoolSize;
    }

    public int getSpoolerExecutorKeepAliveTime() {
        return spoolerExecutorKeepAliveTime;
    }

    public void setSpoolerExecutorKeepAliveTime(int spoolerExecutorKeepAliveTime) {
        this.spoolerExecutorKeepAliveTime = spoolerExecutorKeepAliveTime;
    }

    public boolean isSpoolerExecutorQueueHaveBound() {
        return spoolerExecutorQueueHaveBound;
    }

    public void setSpoolerExecutorQueueHaveBound(boolean spoolerExecutorQueueHaveBound) {
        this.spoolerExecutorQueueHaveBound = spoolerExecutorQueueHaveBound;
    }

    public int getSpoolerExecutorQueueSize() {
        return spoolerExecutorQueueSize;
    }

    public void setSpoolerExecutorQueueSize(int spoolerExecutorQueueSize) {
        this.spoolerExecutorQueueSize = spoolerExecutorQueueSize;
    }
}
