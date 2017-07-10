package org.zhiyang.fget.rpc;

import org.zhiyang.fget.prospector.Spooler;
import org.zhiyang.fget.prospector.reporter.ReporterInfo;

/**
 * @author lizhiyang
 */
public class RpcService {

    private Spooler spooler;

    public RpcService(Spooler spooler) {
        this.spooler = spooler;
    }

    public String query(String id) {
        ReporterInfo reporterInfo = spooler.query(id);
        if (reporterInfo == null) {
            return null;
        }

        return reporterInfo.toJson();
    }

    public String addHttpFile(String url) {
        return spooler.addHttpFile(url);
    }

    public String version() {
        return "1.0";
    }

}
