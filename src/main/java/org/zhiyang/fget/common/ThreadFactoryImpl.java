package org.zhiyang.fget.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lizhiyang
 */
public class ThreadFactoryImpl implements ThreadFactory {

    private final AtomicInteger index = new AtomicInteger(0);

    private final String prefix;

    public ThreadFactoryImpl(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, this.prefix + index);
    }
}
