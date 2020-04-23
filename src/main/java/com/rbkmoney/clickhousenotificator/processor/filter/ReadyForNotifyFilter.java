package com.rbkmoney.clickhousenotificator.processor.filter;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.service.TimePeriodCalculator;
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
        Report lastByNotification = reportModel.getLastReport();
        if (lastByNotification == null)
            return true;
        boolean result = timePeriodCalculator.calculate(notification).isBefore(lastByNotification.getCreatedAt());
        log.info("QueryProcessorImpl filter result: {} notification: {} ", result, notification);
        return result;
    }

}
