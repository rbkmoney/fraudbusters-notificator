package com.rbkmoney.clickhousenotificator.processor;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import com.rbkmoney.clickhousenotificator.service.QueryService;
import com.rbkmoney.clickhousenotificator.service.iface.NotificationService;
import com.rbkmoney.damsel.schedule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryProcessorImpl implements ScheduledJobExecutorSrv.Iface {

    private final NotificationDao notificationDao;
    private final ReportNotificationDao reportNotificationDao;
    private final QueryService queryService;
    private final QueryResultSerde queryResultSerde;
    private final NotificationService notificationService;
    private final Predicate<ReportModel> readyForNotifyFilter;

    @Override
    public ContextValidationResponse validateExecutionContext(ByteBuffer context) throws TException {
        log.info("QueryProcessorImpl validateExecutionContext context!");
        ContextValidationResponse contextValidationResponse = new ContextValidationResponse();
        ValidationResponseStatus validationResponseStatus = new ValidationResponseStatus();
        validationResponseStatus.setSuccess(new ValidationSuccess());
        contextValidationResponse.setResponseStatus(validationResponseStatus);
        return contextValidationResponse;
    }

    @Override
    public ByteBuffer executeJob(ExecuteJobRequest request) throws TException {
        log.info("QueryProcessorImpl start process!");
        List<Notification> activeNotifications = notificationDao.getByStatus(NotificationStatus.ACTIVE);
        log.info("QueryProcessorImpl active notifications: {}", activeNotifications);
        if (!CollectionUtils.isEmpty(activeNotifications)) {
            activeNotifications.stream()
                    .map(this::initReportModel)
                    .filter(readyForNotifyFilter)
                    .map(this::queryForNotify)
                    .filter(Optional::isPresent)
                    .forEach(reportModel -> notificationService.sentNotification(reportModel.get()));
        }
        log.info("QueryProcessorImpl finished process!");
        return ByteBuffer.wrap(new byte[0]);
    }

    private ReportModel initReportModel(final Notification notification) {
        Report lastByNotification = reportNotificationDao.getLastByNotification(notification.getName());
        return ReportModel.builder()
                .notification(notification)
                .lastReport(lastByNotification)
                .build();
    }

    private Optional<ReportModel> queryForNotify(final ReportModel reportModel) {
        Notification notification = reportModel.getNotification();
        try {
            List<Map<String, String>> queryResult = queryService.query(notification);
            Report currentReport = new Report();
            currentReport.setNotificationName(notification.getName());
            currentReport.setResult(queryResultSerde.serialize(queryResult));
            currentReport.setCreatedAt(LocalDateTime.now());
            currentReport.setStatus(ReportStatus.created);
            Optional<Long> insert = reportNotificationDao.insert(currentReport);
            insert.ifPresent(currentReport::setId);
            log.info("QueryProcessorImpl queryForNotify notification: {} result: {}", notification, queryResult);
            return Optional.of(ReportModel.builder()
                    .notification(notification)
                    .lastReport(reportModel.getLastReport())
                    .currentReport(currentReport)
                    .build());
        } catch (Exception e) {
            log.error("NotificationProcessorImpl error when queryForNotify for notification: {} e: ", notification, e);
        }
        return Optional.empty();
    }

}
