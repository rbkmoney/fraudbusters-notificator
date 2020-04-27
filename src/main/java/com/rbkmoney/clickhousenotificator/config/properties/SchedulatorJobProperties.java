package com.rbkmoney.clickhousenotificator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties("schedulator.job")
public class SchedulatorJobProperties {
    @NotEmpty
    private String jobId;

    @NotNull
    private Integer revisionId;

    @NotNull
    private Integer schedulerId;

    @NotNull
    private Integer calendarId;

    @NotEmpty
    private String serviceCallbackPath;
}
