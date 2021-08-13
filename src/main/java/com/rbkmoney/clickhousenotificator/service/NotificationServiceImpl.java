package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.service.factory.MailFactory;
import com.rbkmoney.clickhousenotificator.service.filter.ChangeQueryResultFilter;
import com.rbkmoney.clickhousenotificator.service.iface.MailSenderService;
import com.rbkmoney.clickhousenotificator.service.iface.NotificationService;
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
        log.info("NotificationProcessorImpl start sentNotification!");
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
