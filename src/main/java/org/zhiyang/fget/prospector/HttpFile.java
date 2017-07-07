package org.zhiyang.fget.prospector;

import com.sun.istack.internal.NotNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhiyang.fget.store.FileStore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lizhiyang
 */
public class HttpFile extends Harvester {

    private static Logger log = LoggerFactory.getLogger(HttpFile.class);

    private static OkHttpClient httpClient = new OkHttpClient();

    private String id;

    private String url;

    private String savePath;

    private String realFileName;

    private long targetSize = 0;

    private FileStore fileStore;

    private Response response;

    private volatile boolean complete = false;

    private volatile boolean success = false;

    private Throwable throwable = null;


    public HttpFile(@NotNull String url, @NotNull String savePath,
                    @NotNull FileStore fileStore) {
        this.id = Harvester.getHttpFileUniqueStoreName(url);
        this.url = url;
        this.savePath = savePath;
        this.fileStore = fileStore;
    }

    /**
     * 提取下载文件的文件名，如果从HTTP请求头中获取不到则从URL中获取，以上
     * 都失败则生成UUID的文件名
     *
     * @param header HTTP请求头选项中获取
     * @param url 下载URL
     * @return 文件名
     */
    public static String extractFileName(String header, String url) {

        if (header != null) {

            Pattern fileNamePattern = Pattern.compile("filename=(.*)");
            Matcher matcher = fileNamePattern.matcher(header);
            if (matcher.find()) {
                String matchStr = matcher.group(1);
                return matchStr.trim().replaceAll("\\\"", "");
            }

        } else {

            if (url != null) {
                try {
                    URI uri = new URI(url);
                    String[] paths = uri.getPath().split("/");
                    return paths[paths.length - 1];
                } catch (URISyntaxException ignored) {
                    //忽略这个异常，都失败默认返回生成的UUID文件名
                }
            }

        }

        return UUID.randomUUID().toString();

    }

    private void setSuccess()
    {
        this.success = true;
        this.throwable = null;
    }

    private void setFail(Throwable throwable) {
        this.success = false;
        this.throwable = throwable;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void run() {

        Request request = new Request.Builder().url(this.url).build();

        try {

            this.response = httpClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (null == responseBody) {
                throw new IOException("get response body error");
            }

            //初始化用于存储的文件名
            this.realFileName = extractFileName(response.header("Content-Disposition"), this.url);

            //存储下载文件并标记本次下载任务已经完成
            this.targetSize = responseBody.contentLength();
            fileStore.put(this.getSaveFilePath(), new ReadReporterInputStream(responseBody.byteStream()));

            this.setSuccess();

        } catch (Exception e) {
            log.error("download http file error, url:" + this.url, e);
            this.setFail(e);
        } finally {
            //关闭资源的传输流
            this.stop();
        }

    }

    @Override
    public void stop() {
        this.complete = true;
        if (this.response != null) {
            this.response.close();
        }
    }

    public String getRealFileName() {
        return realFileName;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public Throwable cause() {
        return this.throwable;
    }

    @Override
    public String getSaveFilePath() {
        return this.savePath + "/" + this.id;
    }

    @Override
    public long getProgressRate() {
        if (this.targetSize == 0) {
            return  0;
        } else {
            return (long) ((this.getReadSize() * 1.0 / this.targetSize) * 100);
        }
    }

    @Override
    public long getTargetSize() {
        return this.targetSize;
    }
}
