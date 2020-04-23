package com.rbkmoney.clickhousenotificator.repository;

import com.rbkmoney.clickhousenotificator.dao.ch.QueryRepository;
import com.rbkmoney.clickhousenotificator.util.TestChQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {QueryRepository.class})
public class QueryRepositoryTest extends ClickHouseAbstractTest {

    @Autowired
    private QueryRepository queryRepository;

    @Test
    public void query() throws SQLException {

        List<Map<String, String>> result = queryRepository.query(TestChQuery.QUERY_METRIC_RECURRENT);

        Map<String, String> stringObjectMap = result.get(0);
        Assert.assertEquals(stringObjectMap.get("shopId"), "ad8b7bfd-0760-4781-a400-51903ee8e504");
        Assert.assertEquals(stringObjectMap.get("metric"), "166.66666666666666");
    }

}