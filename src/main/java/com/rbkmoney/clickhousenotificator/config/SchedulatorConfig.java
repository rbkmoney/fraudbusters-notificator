package com.rbkmoney.clickhousenotificator.config;

import com.rbkmoney.clickhousenotificator.config.properties.SchedulatorRegisterRetryProperties;
import com.rbkmoney.damsel.schedule.SchedulatorSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;

@Configuration
public class SchedulatorConfig {

    @Autowired
    private SchedulatorRegisterRetryProperties schedulatorRegisterRetryProperties;

    @Bean
    public SchedulatorSrv.Iface schedulatorClient(@Value("${service.schedulator.url}") Resource resource,
                                                  @Value("${service.schedulator.networkTimeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI()).build(SchedulatorSrv.Iface.class);
    }

    @Bean
    public RetryTemplate schedulatorRegisterRetryTemplate() {
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(schedulatorRegisterRetryProperties.getMaxAttempts());
        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(schedulatorRegisterRetryProperties.getBackOffPeriod());
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }

}
