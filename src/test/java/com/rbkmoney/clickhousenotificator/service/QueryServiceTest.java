package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.warehouse.Query;
import com.rbkmoney.fraudbusters.warehouse.Result;
import com.rbkmoney.fraudbusters.warehouse.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {QueryService.class, QueryParamsPreparationService.class})
class QueryServiceTest {

    @Autowired
    private QueryService queryService;

    @MockBean
    WarehouseQueryService warehouseQueryService;


    @Test
    void queryWithEmptyResult() {
        String statement = "select * from table where id = :id";

        when(warehouseQueryService.execute(any(Query.class))).thenReturn(new Result());

        List<Map<String, String>> actualResult = queryService.query(statement);

        assertTrue(actualResult.isEmpty());

    }

    @Test
    void queryOk() {
        String statement = "select * from table where id = :id";
        Row firstRow = TestObjectsFactory.testRow();
        Row secondRow = TestObjectsFactory.testRow();
        Result result = new Result();
        List<Row> rows = List.of(firstRow, secondRow);
        result.setValues(rows);
        when(warehouseQueryService.execute(any(Query.class))).thenReturn(result);

        List<Map<String, String>> actualResult = queryService.query(statement);

        assertEquals(actualResult.size(), rows.size());
        assertTrue(actualResult.contains(firstRow.getValues()));
        assertTrue(actualResult.contains(secondRow.getValues()));
    }
}
