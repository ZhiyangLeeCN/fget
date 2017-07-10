package org.zhiyang.fget.prospector.reporter.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhiyang.fget.db.SSDB;
import org.zhiyang.fget.prospector.reporter.HarvesterReporter;
import org.zhiyang.fget.prospector.reporter.ReporterInfo;

/**
 * @author lizhiyang
 */
public class SSDBReporter implements HarvesterReporter {

    private static Logger log = LoggerFactory.getLogger(SSDBReporter.class);

    private SSDB ssdbClient;

    public SSDBReporter(SSDB ssdb) {
        this.ssdbClient = ssdb;
    }

    @Override
    public ReporterInfo query(String id) {
        try {
            byte[] data = this.ssdbClient.hget("harvester_table", id);
            if (null == data) {
                return null;
            }

            return ReporterInfo.decode(data, ReporterInfo.class);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("ssdb reporter error", e);
            return null;
        }
    }

    @Override
    public void report(ReporterInfo reporterInfo) {
        try {
            this.ssdbClient.hset("harvester_table", reporterInfo.getId(), reporterInfo.encode());
        } catch (Exception e) {
           log.error("ssdb reporter hset error", e);
        }
    }

    @Override
    public void shutdown() {
        this.ssdbClient.close();
    }
}
