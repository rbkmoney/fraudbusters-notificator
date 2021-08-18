package com.rbkmoney.fraudbusters.notificator.service;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.parser.PeriodParser;
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
                .minus(periodParser.parse(notification.getFrequency()), ChronoUnit.MILLIS);
    }

}
