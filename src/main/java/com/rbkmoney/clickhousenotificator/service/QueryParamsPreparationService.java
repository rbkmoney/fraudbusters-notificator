package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.constant.CustomParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;


@Service
@RequiredArgsConstructor
public class QueryParamsPreparationService {

    public Map<String, String> prepare() {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(ISO_DATE);
        String currentDateTime = now.format(ISO_DATE_TIME);
        String currentMonth = String.valueOf(now.getMonth().getValue());
        String currentYear = String.valueOf(now.getYear());
        return Map.of(CustomParameters.CURRENT_DATE_TIME, currentDateTime,
                CustomParameters.CURRENT_DATE, currentDate,
                CustomParameters.CURRENT_MONTH, currentMonth,
                CustomParameters.CURRENT_YEAR, currentYear);

    }

}
