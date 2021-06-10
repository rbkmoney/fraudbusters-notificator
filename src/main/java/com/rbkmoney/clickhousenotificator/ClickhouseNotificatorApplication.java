package com.rbkmoney.clickhousenotificator;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class ClickhouseNotificatorApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseNotificatorApplication.class, args);
    }

}
