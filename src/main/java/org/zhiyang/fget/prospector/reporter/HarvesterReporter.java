package org.zhiyang.fget.prospector.reporter;

/**
 * @author lizhiyang
 */
public interface HarvesterReporter {

    public ReporterInfo query(String id);

    public void report(ReporterInfo reporterInfo);

    public void shutdown();

}
