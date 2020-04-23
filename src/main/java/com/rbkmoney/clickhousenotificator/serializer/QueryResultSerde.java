package com.rbkmoney.clickhousenotificator.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.domain.QueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryResultSerde {

    private final ObjectMapper objectMapper;

    public String serialize(List<Map<String, String>> queryResult) {
        try {
            QueryResult result = new QueryResult();
            result.setResults(queryResult);
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            log.error("QueryResultSerde error when serialize queryResult: {} e:", queryResult, e);
        }
        return "";
    }

    public Optional<QueryResult> deserialize(String string) {
        try {
            return Optional.ofNullable(objectMapper.readValue(string, QueryResult.class));
        } catch (JsonProcessingException e) {
            log.error("QueryResultSerde error when deserialize string: {} e:", string, e);
        }
        return Optional.empty();
    }
}
