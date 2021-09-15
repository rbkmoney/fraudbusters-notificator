package com.rbkmoney.fraudbusters.notificator.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlFilterUtils {

    static String prepareSearchField(String searchField) {
        if (StringUtils.hasText(searchField)) {
            return "%" + searchField + "%";
        }
        return searchField;
    }
}
