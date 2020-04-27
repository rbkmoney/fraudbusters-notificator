package com.rbkmoney.clickhousenotificator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("schedulator.register-retry")
public class SchedulatorRegisterRetryProperties {

    @NotNull
    private Integer maxAttempts;

    @NotNull
    private Integer backOffPeriod;

}
