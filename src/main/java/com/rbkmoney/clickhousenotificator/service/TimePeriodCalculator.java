package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.parser.PeriodParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class TimePeriodCalculator {

    private final PeriodParser periodParser;

    public LocalDateTime calculate(Notification notification) {
        return LocalDateTime.now()
                .minus(periodParser.parse(notification.getPeriod()), ChronoUnit.MILLIS);
    }

}
