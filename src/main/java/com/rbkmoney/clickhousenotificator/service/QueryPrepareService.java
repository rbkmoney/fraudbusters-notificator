package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.constant.CustomParameters;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE;


@Service
@RequiredArgsConstructor
public class QueryPrepareService {

    public static final String QUOTES = "'";

    public String prepare(Notification notification) {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = QUOTES + now.format(ISO_DATE) + QUOTES;
        String currentMonth = String.valueOf(now.getMonth().getValue());
        String currentYear = String.valueOf(now.getYear());
        String queryText = notification.getQueryText();
        return queryText.replaceAll(CustomParameters.$_CURRENT_DATE, currentDate)
                .replaceAll(CustomParameters.$_CURRENT_MONTH, currentMonth)
                .replaceAll(CustomParameters.$_CURRENT_YEAR, currentYear);
    }

}
