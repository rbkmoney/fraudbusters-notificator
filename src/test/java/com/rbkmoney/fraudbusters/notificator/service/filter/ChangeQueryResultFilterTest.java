package com.rbkmoney.fraudbusters.notificator.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;
import com.rbkmoney.fraudbusters.notificator.serializer.QueryResultSerde;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChangeQueryResultFilterTest {

    ChangeQueryResultFilter changeQueryResultFilter =
            new ChangeQueryResultFilter(new QueryResultSerde(new ObjectMapper()));

    @Test
    void testFilterFalse() {
        Report lastReport = new Report();
        Report currentReport = new Report();

        lastReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\"," +
                "\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");
        currentReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\"," +
                "\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");

        boolean test = changeQueryResultFilter.test(ReportModel.builder()
                .lastReport(lastReport)
                .currentReport(currentReport)
                .notification(TestObjectsFactory.testNotification())
                .notificationTemplate(TestObjectsFactory.testNotificationTemplate("shopId,currency"))
                .build());

        assertFalse(test);
    }

    @Test
    void testFilterTrue() {
        Report lastReport = new Report();
        Report currentReport = new Report();

        lastReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\"," +
                "\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");
        currentReport.setResult("{\"results\":[{\"t\":\"2019-12-05\",\"metric\":\"166.66666666666666\"," +
                "\"currency\":\"RUB\",\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}," +
                "{\"t\":\"2019-12-05\",\"metric\":\"100\",\"currency\":\"USD\"," +
                "\"shopId\":\"ad8b7bfd-0760-4781-a400-51903ee8e504\"}]}");

        boolean test = changeQueryResultFilter.test(ReportModel.builder()
                .lastReport(lastReport)
                .currentReport(currentReport)
                .notification(TestObjectsFactory.testNotification())
                .notificationTemplate(TestObjectsFactory.testNotificationTemplate("shopId,currency"))
                .build());

        assertTrue(test);
    }
}
