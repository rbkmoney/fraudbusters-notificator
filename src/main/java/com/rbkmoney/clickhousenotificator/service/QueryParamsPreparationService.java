package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.constant.CustomParameters;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;


@Service
@RequiredArgsConstructor
public class QueryParamsPreparationService {

    public static final String QUOTES = "'";

    public String prepare(Notification notification) {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = QUOTES + now.format(ISO_DATE) + QUOTES;
        String currentDateTime = QUOTES + now.format(ISO_DATE_TIME) + QUOTES;
        String currentMonth = String.valueOf(now.getMonth().getValue());
        String currentYear = String.valueOf(now.getYear());
        String queryText = notification.getQueryText();
        return queryText.replaceAll(CustomParameters.CURRENT_DATE_TIME, currentDateTime)
                .replaceAll(CustomParameters.CURRENT_DATE, currentDate)
                .replaceAll(CustomParameters.CURRENT_MONTH, currentMonth)
                .replaceAll(CustomParameters.CURRENT_YEAR, currentYear);
    }

    public Map<String, String> prepare() {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = QUOTES + now.format(ISO_DATE) + QUOTES;
        String currentDateTime = QUOTES + now.format(ISO_DATE_TIME) + QUOTES;
        String currentMonth = String.valueOf(now.getMonth().getValue());
        String currentYear = String.valueOf(now.getYear());
        return Map.of(CustomParameters.CURRENT_DATE_TIME, currentDateTime,
                CustomParameters.CURRENT_DATE, currentDate,
                CustomParameters.CURRENT_MONTH, currentMonth,
                CustomParameters.CURRENT_YEAR, currentYear);

    }

}
