package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.ch.QueryRepository;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class QueryService {

    private final QueryPreparationService queryPrepareService;
    private final QueryRepository queryRepository;

    public List<Map<String, String>> query(Notification notification) {
        String preparedQuery = queryPrepareService.prepare(notification);
        return queryRepository.query(preparedQuery);
    }

}
