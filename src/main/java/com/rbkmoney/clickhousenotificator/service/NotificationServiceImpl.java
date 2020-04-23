package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.Message;
import com.rbkmoney.clickhousenotificator.domain.QueryResult;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import com.rbkmoney.clickhousenotificator.service.iface.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ReportNotificationDao reportNotificationDao;
    private final QueryResultSerde queryResultSerde;
    private final MailSenderServiceImpl mailSenderServiceImpl;

    @Override
    public void sentNotification(ReportModel reportModel) {
        log.info("NotificationProcessorImpl start sentNotification!");
        boolean isChanged = isChanged(reportModel);

        Report report = reportModel.getCurrentReport();
        if (!isChanged) {
            report.setStatus(ReportStatus.skipped);
            reportNotificationDao.insert(report);
            log.info("NotificationProcessorImpl skipped: {}", report);
            return;
        }

        mailSenderServiceImpl.send(Message.builder()
                .content(report.getResult())
                .build());
        report.setStatus(ReportStatus.send);
        reportNotificationDao.insert(report);
        log.info("NotificationProcessorImpl send: {}", report);
    }

    private boolean isChanged(ReportModel reportModel) {
        Report oldReport = reportModel.getLastReport();
        if (oldReport != null) {
            Optional<QueryResult> oldReportResult = queryResultSerde.deserialize(oldReport.getResult());
            Optional<QueryResult> newReportResult = queryResultSerde.deserialize(reportModel.getCurrentReport().getResult());
            if (newReportResult.isPresent()) {
                if (oldReportResult.isEmpty()) {
                    return true;
                }
                List<String> collect = oldReportResult.get().getResults().stream()
                        .map(queryResult -> queryResult.values().stream().collect(Collectors.joining("-")))
                        .collect(Collectors.toList());

                long count = newReportResult.get().getResults().stream()
                        .filter(queryResult -> collect.contains(queryResult.values().stream().collect(Collectors.joining("-"))))
                        .count();

                //TODO merge function with group params

                return collect.size() != count;
            }
        }
        return true;
    }

}
