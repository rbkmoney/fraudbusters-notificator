package com.rbkmoney.fraudbusters.notificator.processor.filter;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;
import com.rbkmoney.fraudbusters.notificator.service.TimePeriodCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadyForNotifyFilter implements Predicate<ReportModel> {

    private final TimePeriodCalculator timePeriodCalculator;

    @Override
    public boolean test(ReportModel reportModel) {
        Notification notification = reportModel.getNotification();
        Report lastReportByNotification = reportModel.getLastReport();
        if (lastReportByNotification == null) {
            return true;
        }
        boolean result = timePeriodCalculator.calculate(notification).isAfter(lastReportByNotification.getCreatedAt());
        log.info("ReadyForNotifyFilter filter result: {} notification: {} ", result, notification);
        return result;
    }

}
