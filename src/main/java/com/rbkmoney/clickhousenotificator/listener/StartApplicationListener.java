package com.rbkmoney.clickhousenotificator.listener;

import com.rbkmoney.clickhousenotificator.service.iface.JobRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "schedulator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StartApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private final JobRegistration retryableJobRegistrationDecorator;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            retryableJobRegistrationDecorator.registerJob();
        } catch (Exception e) {
            log.error("Unexpected exception while register job'", e);
        }
    }

}
