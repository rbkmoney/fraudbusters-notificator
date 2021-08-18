package com.rbkmoney.fraudbusters.notificator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class FraudbustersNotificatorApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudbustersNotificatorApplication.class, args);
    }

}
