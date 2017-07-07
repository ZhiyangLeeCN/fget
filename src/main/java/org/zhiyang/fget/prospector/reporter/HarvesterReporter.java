package org.zhiyang.fget.prospector.reporter;

import java.util.concurrent.locks.Lock;

/**
 * @author lizhiyang
 */
public interface HarvesterReporter {

    public Lock getLock(String id);

    public void reportProgress(String id, long totalSize, long readSize, boolean complete);

    public void reportSuccessResult(String id, String path);

}
