package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.exception.RegisterJobException;
import com.rbkmoney.clickhousenotificator.service.iface.JobRegistration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryableJobRegistrationDecorator implements JobRegistration {

    private final RetryTemplate schedulatorRegisterRetryTemplate;
    private final JobRegistration schedulerJobRegistration;

    @PostConstruct
    public void init() {
        schedulatorRegisterRetryTemplate.registerListener(new RetryableJobRegistrationDecorator.SchedulerRegisterRetryListener());
    }

    @Override
    public void registerJob() {
        schedulatorRegisterRetryTemplate.execute(context -> {
            try {
                schedulerJobRegistration.registerJob();
            } catch (RegisterJobException e) {
                log.warn("Register job fail", e);
            }
            return null;
        });
    }

    @Override
    public void deregisterJob(String jobId) {
        schedulatorRegisterRetryTemplate.execute(context -> {
            try {
                schedulerJobRegistration.deregisterJob(jobId);
            } catch (RegisterJobException e) {
                throw new IllegalStateException(String.format("Deregister job '%s' failed", jobId), e);
            }
            return null;
        });
    }

    private static final class SchedulerRegisterRetryListener extends RetryListenerSupport {
        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.error("Exception while register 'every minute' job. Retry count: {}", context.getRetryCount(), context.getLastThrowable());
        }
    }

}
