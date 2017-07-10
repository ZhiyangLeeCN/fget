package org.zhiyang.fget.prospector.reporter.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhiyang.fget.FGetConfig;
import org.zhiyang.fget.db.SSDB;
import org.zhiyang.fget.prospector.reporter.HarvesterReporter;
import org.zhiyang.fget.prospector.reporter.HarvesterReporterFactory;

/**
 * @author lizhiyang
 */
public class SSDBReporterFactory implements HarvesterReporterFactory {

    private static Logger log = LoggerFactory.getLogger(SSDBReporterFactory.class);

    private FGetConfig fGetConfig;

    public SSDBReporterFactory(FGetConfig fGetConfig) {
        this.fGetConfig = fGetConfig;
    }

    @Override
    public HarvesterReporter newReporter() {
        try {
            return new SSDBReporter(
                    new SSDB(
                            fGetConfig.getSsdbHost(),
                            fGetConfig.getSsdbPort(),
                            fGetConfig.getSsdbTimeout()
                    ));
        } catch (Exception e) {
            log.error("new Reporter(SSDBReporter) error", e);
            return null;
        }
    }
}
