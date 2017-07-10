package org.zhiyang.fget.store;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lizhiyang
 */
public interface FileStore {

    public boolean initialize();

    public boolean exist(String path);

    public void put(String path, InputStream in, Map<String, String> metadata) throws Exception;

    public void put(String path, File file, Map<String, String> metadata) throws Exception;

    public void shutdown();

}
