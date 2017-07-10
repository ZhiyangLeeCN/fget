package org.zhiyang.fget.store;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

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

    @Override
    public boolean exist(String path) {
        File file = new File(this.savePath.getPath() + "/" + path);
        return file.exists();
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
    public void put(String path, InputStream in, Map<String, String> metadata) throws Exception {
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
    public void put(String path, File file, Map<String, String> metadata) throws Exception {
        File saveFile = ensureSaveFileOK(path);
        this.put(saveFile.getPath(), new FileInputStream(file), metadata);
    }

    @Override
    public void shutdown() {

    }
}
