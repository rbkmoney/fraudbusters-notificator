package com.rbkmoney.clickhousenotificator.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import com.rbkmoney.clickhousenotificator.util.NotificationFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangeQueryResultFilterTest {

    ChangeQueryResultFilter changeQueryResultFilter;

    @Before
    public void init() {
        changeQueryResultFilter = new ChangeQueryResultFilter(new QueryResultSerde(new ObjectMapper()));
    }

    @Test
    public void test1() {
        Report lastReport = new Report();
        Report currentReport = new Report();

        lastReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\",\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");
        currentReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\",\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");

        boolean test = changeQueryResultFilter.test(ReportModel.builder()
                .lastReport(lastReport)
                .currentReport(currentReport)
                .notification(NotificationFactory.createNotification("", NotificationStatus.ACTIVE, "", "shopId,currency"))
                .build());

        assertFalse(test);

        lastReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\",\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");
        currentReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\",\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}," +
                "{\"t\":\"2019-12-05\",\"metric\":\"100\",\"currency\":\"USD\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");

        test = changeQueryResultFilter.test(ReportModel.builder()
                .lastReport(lastReport)
                .currentReport(currentReport)
                .notification(NotificationFactory.createNotification("", NotificationStatus.ACTIVE, "", "shopId,currency"))
                .build());

        assertTrue(test);
    }
}