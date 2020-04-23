package com.rbkmoney.clickhousenotificator.parser;

import org.springframework.stereotype.Service;

@Service
public class PeriodParser {

    public Long parse(String period) {
        switch (period) {
            case "1s":
                return 1000L;
            case "1m":
                return 60 * 1000L;
            case "1h":
                return 60 * 60 * 1000L;
            case "1d":
                return 24 * 60 * 60 * 1000L;
            default:
                return 0L;
        }
    }

}
