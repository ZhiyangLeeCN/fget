package org.zhiyang.fget.store;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author lizhiyang
 */
public class AliyunOSS implements FileStore {

    private String bucketName;

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private OSSClient storeClient;

    public AliyunOSS(String endpoint,
                     String accessKeyId,
                     String accessKeySecret,
                     String bucketName)
    {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
    }

    @Override
    public boolean initialize() {
        this.storeClient = new OSSClient(this.endpoint, this.accessKeyId, this.accessKeySecret);
        return true;
    }

    @Override
    public boolean exist(String path) {
        return this.storeClient.doesObjectExist(this.bucketName, path);
    }

    public ObjectMetadata getMetadata(Map<String, String> map) {
        if (null == map) {
            return null;
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        for (Map.Entry<String, String> entry: map.entrySet()) {
            objectMetadata.setHeader(entry.getKey(), entry.getValue());
        }

        return objectMetadata;
    }

    @Override
    public void put(String path, InputStream in, Map<String, String> metadata) throws Exception {
        this.storeClient.putObject(this.bucketName, path, in, getMetadata(metadata));
    }

    @Override
    public void put(String path, File file, Map<String, String> metadata) throws Exception {
        this.storeClient.putObject(this.bucketName, path, file, getMetadata(metadata));
    }

    @Override
    public void shutdown() {
        this.storeClient.shutdown();
    }
}
