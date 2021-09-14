package com.rbkmoney.fraudbusters.notificator.service;

import com.rbkmoney.fraudbusters.notificator.dao.ReportNotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;
import com.rbkmoney.fraudbusters.notificator.service.factory.MailFactory;
import com.rbkmoney.fraudbusters.notificator.service.filter.ChangeQueryResultFilter;
import com.rbkmoney.fraudbusters.notificator.service.iface.MailSenderService;
import com.rbkmoney.fraudbusters.notificator.service.iface.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ReportNotificationDao reportNotificationDao;
    private final MailSenderService mailSenderService;
    private final MailFactory mailFactory;
    private final ChangeQueryResultFilter changeQueryResultFilter;

    @Override
    public void send(ReportModel reportModel) {
        log.info("NotificationServiceImpl start send notification");
        Report report = reportModel.getCurrentReport();
        if (!changeQueryResultFilter.test(reportModel)) {
            report.setStatus(ReportStatus.skipped);
            log.info("NotificationServiceImpl skipped: {}", report);
            return;
        }
        sendMail(reportModel, report);
        reportNotificationDao.insert(report);
        log.info("NotificationServiceImpl send: {}", report);
    }

    private void sendMail(ReportModel reportModel, Report report) {
        mailFactory.create(reportModel).ifPresentOrElse(message -> {
            try {
                mailSenderService.send(message);
                report.setStatus(ReportStatus.send);
            } catch (Exception e) {
                log.error("Error when send message report: {} e: ", report, e);
                report.setStatus(ReportStatus.failed);
            }
        }, () -> report.setStatus(ReportStatus.failed));
    }

}
