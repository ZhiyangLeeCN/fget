package org.zhiyang.fget.prospector.reporter;

import org.zhiyang.fget.common.RemotingSerializable;

/**
 * @author lizhiyang
 */
public class ReporterInfo extends RemotingSerializable {

    private String id;

    private String realFileName;

    private long totalSize = 0;

    private long readSize = 0;

    private boolean complete = false;

    private boolean success = false;

    public ReporterInfo() {

    }

    public ReporterInfo(String id, String realFileName, long totalSize, long readSize, boolean complete, boolean success) {
        this.id = id;
        this.realFileName = realFileName;
        this.totalSize = totalSize;
        this.readSize = readSize;
        this.complete = complete;
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getReadSize() {
        return readSize;
    }

    public void setReadSize(long readSize) {
        this.readSize = readSize;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
