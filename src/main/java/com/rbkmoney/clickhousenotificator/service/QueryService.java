package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.fraudbusters.warehouse.Query;
import com.rbkmoney.fraudbusters.warehouse.Result;
import com.rbkmoney.fraudbusters.warehouse.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class QueryService {

    private final QueryParamsPreparationService queryPrepareService;
    private final WarehouseQueryService warehouseQueryService;

    public List<Map<String, String>> query(String statement) {
        Map<String, String> params = queryPrepareService.prepare();
        Query query = new Query();
        query.setParams(params);
        query.setStatement(statement);
        Result result = warehouseQueryService.execute(query);
        if (!result.isSetValues()) {
            return Collections.emptyList();
        }
        return result.getValues().stream()
                .map(Row::getValues)
                .collect(Collectors.toList());
    }

}
