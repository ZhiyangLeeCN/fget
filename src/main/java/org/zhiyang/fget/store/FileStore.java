package org.zhiyang.fget.store;

import java.io.File;
import java.io.InputStream;

/**
 * @author lizhiyang
 */
public interface FileStore {

    public boolean initialize();

    public void put(String path, InputStream in) throws Exception;

    public void put(String path, File file) throws Exception;

    public void shutdown();

}
