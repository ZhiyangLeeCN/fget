package org.zhiyang.fget.store;

import com.aliyun.oss.OSSClient;

import java.io.File;
import java.io.InputStream;

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
    public void put(String path, InputStream in) throws Exception {
        this.storeClient.putObject(this.bucketName, path, in);
    }

    @Override
    public void put(String path, File file) throws Exception {
        this.storeClient.putObject(this.bucketName, path, file);
    }

    @Override
    public void shutdown() {

    }
}
