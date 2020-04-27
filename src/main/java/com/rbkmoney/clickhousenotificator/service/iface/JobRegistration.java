package com.rbkmoney.clickhousenotificator.service.iface;

public interface JobRegistration {

    void registerJob();

    void deregisterJob(String jobId);

}
