package org.zhiyang.fget.prospector;

import org.zhiyang.fget.common.MixAll;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lizhiyang
 */
public abstract class Harvester implements Runnable {

    private long readSize = 0;

    //启动文件下载
    public abstract void run();

    //停止文件下载
    public abstract void stop();

    //下载是否完成
    public abstract boolean isComplete();

    //下载是否成功
    public abstract boolean isSuccess();

    //获取失败的异常，成功则为null
    public abstract Throwable cause();

    //获取保存文件的路径
    public abstract String getSaveFilePath();

    //获取下载进度百分比
    public abstract long getProgressRate();

    //获取下载目标总大小
    public abstract long getTargetSize();

    //获取当前已下载大小
    public final long getReadSize() {
        return readSize;
    }

    //生成用于存储的唯一文件名
    public static String getHttpFileUniqueStoreName(String url) {
        return MixAll.md5(url);
    }

    protected final class ReadReporterInputStream extends InputStream {

        private InputStream inputStream;

        public ReadReporterInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int size = inputStream.read(b, off, len);
            if (size > 0) {
                readSize += size;
            }

            return size;
        }

        @Override
        public long skip(long n) throws IOException {
            return inputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            inputStream.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }

    }

}
