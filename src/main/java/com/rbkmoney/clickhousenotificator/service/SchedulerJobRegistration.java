package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.config.properties.SchedulatorJobProperties;
import com.rbkmoney.clickhousenotificator.exception.RegisterJobException;
import com.rbkmoney.clickhousenotificator.service.iface.JobRegistration;
import com.rbkmoney.damsel.domain.BusinessScheduleRef;
import com.rbkmoney.damsel.domain.CalendarRef;
import com.rbkmoney.damsel.schedule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerJobRegistration implements JobRegistration {

    private final SchedulatorSrv.Iface schedulatorClient;
    private final SchedulatorJobProperties schedulatorJobProperties;

    @Override
    public void registerJob() {
        log.info("SchedulerJobRegistration registerJob");
        try {
            String jobId = register();
            log.info("SchedulerJobRegistration job: {} successfully registered", jobId);
        } catch (RegisterJobException e) {
            log.warn("SchedulerJobRegistration registerJob fail", e);
        }
    }

    private String register() {
        RegisterJobRequest registerJobRequest = new RegisterJobRequest();
        registerJobRequest.setExecutorServicePath(schedulatorJobProperties.getServiceCallbackPath());
        Schedule schedule = buildsSchedule(schedulatorJobProperties.getSchedulerId(),
                schedulatorJobProperties.getCalendarId(),
                schedulatorJobProperties.getRevisionId());
        registerJobRequest.setSchedule(schedule);
        registerJobRequest.setContext(new byte[0]);
        try {
            schedulatorClient.registerJob(schedulatorJobProperties.getJobId(), registerJobRequest);
            return schedulatorJobProperties.getJobId();
        } catch (ScheduleAlreadyExists e) {
            throw new RegisterJobException(String.format("Schedule '%s' already exists", schedulatorJobProperties.getJobId()));
        } catch (TException e) {
            throw new IllegalStateException(String.format("Register job '%s' failed", schedulatorJobProperties.getJobId()), e);
        }
    }

    @Override
    public void deregisterJob(String jobId) {
        try {
            schedulatorClient.deregisterJob(jobId);
        } catch (TException e) {
            throw new IllegalStateException(String.format("Deregister job '%s' failed", jobId), e);
        }
    }

    private Schedule buildsSchedule(int scheduleRefId, int calendarRefId, long revision) {
        Schedule schedule = new Schedule();
        DominantBasedSchedule dominantBasedSchedule = new DominantBasedSchedule()
                .setBusinessScheduleRef(new BusinessScheduleRef().setId(scheduleRefId))
                .setCalendarRef(new CalendarRef().setId(calendarRefId))
                .setRevision(revision);
        schedule.setDominantSchedule(dominantBasedSchedule);
        return schedule;
    }

}
