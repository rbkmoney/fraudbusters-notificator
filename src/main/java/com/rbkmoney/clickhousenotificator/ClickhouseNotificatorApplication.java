package com.rbkmoney.clickhousenotificator;

import com.rbkmoney.clickhousenotificator.config.properties.SchedulatorJobProperties;
import com.rbkmoney.clickhousenotificator.service.iface.JobRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PreDestroy;

@ServletComponentScan
@SpringBootApplication
public class ClickhouseNotificatorApplication extends SpringApplication {

    @Autowired
    private JobRegistration retryableJobRegistrationDecorator;

    @Autowired
    private SchedulatorJobProperties schedulatorJobProperties;

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseNotificatorApplication.class, args);
    }

    @PreDestroy
    public void destroy() {
        retryableJobRegistrationDecorator.deregisterJob(schedulatorJobProperties.getJobId());
    }

}
