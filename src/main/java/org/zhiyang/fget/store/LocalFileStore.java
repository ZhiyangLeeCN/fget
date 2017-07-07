package org.zhiyang.fget.store;

import com.sun.istack.internal.Nullable;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author lizhiyang
 */
public class LocalFileStore implements FileStore {

    private static Logger log = LoggerFactory.getLogger(LocalFileStore.class);

    private File savePath;

    public LocalFileStore(String savePath) {
        this(new File(savePath));
    }

    public LocalFileStore(File savePath) {
        this.savePath = savePath;
    }

    @Override
    public boolean initialize() {

        try {
            FileUtils.forceMkdir(this.savePath);
            return true;
        } catch (IOException e) {
            log.error("initialize local file store error", e);
            return false;
        }
    }

    @Nullable
    private File ensureSaveFileOK(String filePath) throws IOException {

        File saveFile = new File(this.savePath.getPath() + "/" + filePath);
        FileUtils.forceMkdirParent(saveFile);
        if (saveFile.exists()) {
            saveFile.delete();
        }

        try {
            saveFile.createNewFile();
            return saveFile;
        } catch (IOException e) {
            throw e;
        }

    }

    @Override
    public void put(String path, InputStream in) throws Exception {
        File saveFile = ensureSaveFileOK(path);
        FileOutputStream fos = new FileOutputStream(saveFile);

        int size;
        byte[] buff = new byte[4096];
        while ((size = in.read(buff)) > 0) {
            fos.write(buff, 0, size);
        }

        fos.close();
    }

    @Override
    public void put(String path, File file) throws Exception {
        File saveFile = ensureSaveFileOK(path);
        this.put(saveFile.getPath(), new FileInputStream(file));
    }

    @Override
    public void shutdown() {

    }
}
