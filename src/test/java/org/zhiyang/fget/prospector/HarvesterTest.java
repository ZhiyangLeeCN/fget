package org.zhiyang.fget.prospector;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.zhiyang.fget.store.FileStore;
import org.zhiyang.fget.store.LocalFileStore;

import java.io.File;

/**
 * @author lizhiyang
 */
public class HarvesterTest {

    @Test
    public void testExtractName() throws Exception {

        String headRawFileName1 = "attachment; filename=filename.xls";
        String headRawFileName2 = "attachment; filename=\"filename.xls\"";
        String headRawFileName3 = "attachment; filename=\"QQPinyin_Setup_5.6.4103.400.exe\"";

        String downloadUrl = "http://dl.softmgr.qq.com/original/Input/QQPinyin_Setup_5.6.4103.400.exe";

        String headFileName1 = HttpFile.extractFileName(headRawFileName1, null);
        String headFileName2 = HttpFile.extractFileName(headRawFileName2, null);
        String headFileName3 = HttpFile.extractFileName(headRawFileName3, null);
        String urlFileName = HttpFile.extractFileName(null, downloadUrl);

        Assert.assertEquals("filename.xls", headFileName1);
        Assert.assertEquals("filename.xls", headFileName2);
        Assert.assertEquals("QQPinyin_Setup_5.6.4103.400.exe", headFileName3);
        Assert.assertEquals("QQPinyin_Setup_5.6.4103.400.exe", urlFileName);

    }

    @Test
    public void testDownload() throws Exception {

        String fileUrl = "http://dl.softmgr.qq.com/original/Input/QQPinyin_Setup_5.6.4103.400.exe";

        File downloadSave = new File("test/dowload");
        FileStore fileStore = new LocalFileStore(downloadSave);
        boolean initResult = fileStore.initialize();
        Assert.assertTrue(initResult);

        HttpFile httpFile = new HttpFile(fileUrl, "http", fileStore);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!httpFile.isComplete()) {
                    System.out.println("current progress rate:" + httpFile.getProgressRate());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Assert.fail(e.getMessage());
                    }
                }
            }
        }).start();

        httpFile.run();

        while (true) {

            if (httpFile.isComplete()) {
                File downloadFile = new File("test/dowload/" + httpFile.getSaveFilePath());
                Assert.assertTrue(downloadFile.exists());
                break;
            }

        }

        FileUtils.forceDelete(downloadSave);
    }
}