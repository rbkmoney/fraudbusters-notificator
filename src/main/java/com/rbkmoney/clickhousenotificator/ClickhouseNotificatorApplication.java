package com.rbkmoney.clickhousenotificator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class ClickhouseNotificatorApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseNotificatorApplication.class, args);
    }

}
