package com.rbkmoney.fraudbusters.notificator.processor;

import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationTemplateDao;
import com.rbkmoney.fraudbusters.notificator.dao.ReportNotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;
import com.rbkmoney.fraudbusters.notificator.serializer.QueryResultSerde;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import com.rbkmoney.fraudbusters.notificator.service.iface.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProcessorImpl implements NotificationProcessor {

    private final NotificationDao notificationDao;
    private final ReportNotificationDao reportNotificationDao;
    private final NotificationTemplateDao notificationTemplateDao;
    private final QueryService queryService;
    private final QueryResultSerde queryResultSerde;
    private final NotificationService notificationService;
    private final Predicate<ReportModel> readyForNotifyFilter;

    @Override
    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    @SchedulerLock(name = "TaskScheduler_invoke_process")
    public void process() {
        log.info("NotificationProcessorImpl start process");
        List<Notification> activeNotifications = notificationDao.getByStatus(NotificationStatus.ACTIVE);
        log.info("NotificationProcessorImpl active notifications: {}", activeNotifications);
        if (!CollectionUtils.isEmpty(activeNotifications)) {
            activeNotifications.stream()
                    .map(this::initReportModel)
                    .filter(readyForNotifyFilter)
                    .map(this::enrichByCurrentReport)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(notificationService::send);
        }
        log.info("NotificationProcessorImpl finished process");
    }

    private ReportModel initReportModel(final Notification notification) {
        Report lastByNotification = reportNotificationDao.getLastSendById(notification.getId());
        NotificationTemplate notificationTemplate = notificationTemplateDao.getById(notification.getTemplateId());
        return ReportModel.builder()
                .notification(notification)
                .notificationTemplate(notificationTemplate)
                .lastReport(lastByNotification)
                .build();
    }

    private Optional<ReportModel> enrichByCurrentReport(final ReportModel reportModel) {
        Notification notification = reportModel.getNotification();
        NotificationTemplate notificationTemplate = reportModel.getNotificationTemplate();
        try {
            List<Map<String, String>> queryResult = queryService.query(notificationTemplate.getQueryText());
            Report currentReport = new Report();
            currentReport.setNotificationId(notification.getId());
            currentReport.setResult(queryResultSerde.serialize(queryResult));
            currentReport.setCreatedAt(LocalDateTime.now());
            currentReport.setStatus(ReportStatus.created);
            Optional<Long> insert = reportNotificationDao.insert(currentReport);
            insert.ifPresent(currentReport::setId);
            log.info("NotificationProcessorImpl enrichByCurrentReport notification: {} template: {} result: {}",
                    notification,
                    notificationTemplate, queryResult);
            return Optional.of(ReportModel.builder()
                    .notification(notification)
                    .notificationTemplate(notificationTemplate)
                    .lastReport(reportModel.getLastReport())
                    .currentReport(currentReport)
                    .build());
        } catch (Exception e) {
            log.error(
                    "NotificationProcessorImpl error when enrichByCurrentReport for notification: {} template: {} e: ",
                    notification, notificationTemplate, e);
        }
        return Optional.empty();
    }

}
