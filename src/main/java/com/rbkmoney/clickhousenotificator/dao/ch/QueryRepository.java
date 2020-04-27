package com.rbkmoney.clickhousenotificator.dao.ch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryRepository {

    private final JdbcTemplate jdbcTemplateCH;

    public List<Map<String, String>> query(String select) {
        log.info("QueryRepository select: {}", select);
        List<Map<String, Object>> result = jdbcTemplateCH.queryForList(select);
        List<Map<String, String>> resultStringMap = result.stream()
                .map(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))
                        )
                ).collect(Collectors.toList());
        log.info("QueryRepository queryResult: {}", resultStringMap);
        return resultStringMap;
    }

}
