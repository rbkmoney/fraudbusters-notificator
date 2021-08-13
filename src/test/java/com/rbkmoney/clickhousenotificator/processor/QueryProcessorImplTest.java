package com.rbkmoney.clickhousenotificator.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.TestObjectsFactory;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.pg.ChannelDaoImpl;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.QueryResult;
import com.rbkmoney.clickhousenotificator.resource.NotificationResourceImpl;
import com.rbkmoney.clickhousenotificator.service.MailSenderServiceImpl;
import com.rbkmoney.clickhousenotificator.util.TestChQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Disabled("Надо доделать")
class QueryProcessorImplTest {

    @Autowired
    NotificationDao notificationDao;
    @Autowired
    NotificationResourceImpl notificationResource;
    @Autowired
    ChannelDaoImpl channelDao;
    @Autowired
    ReportNotificationDao reportNotificationDao;
    @Autowired
    QueryProcessorImpl queryProcessor;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    MailSenderServiceImpl mailSenderServiceImpl;


    @BeforeEach
    public void init() {
        channelDao.insert(TestObjectsFactory.createChannel());
        Notification successNotify =
                TestObjectsFactory.createNotification("successNotify", TestChQuery.QUERY_METRIC_RECURRENT,
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL, "shopId,currency");
        notificationResource.createOrUpdate(successNotify);
        notificationResource
                .createOrUpdate(
                        TestObjectsFactory.createNotification("failedName", "select * from analytic.events_sink_refund",
                                NotificationStatus.ACTIVE, "errorChannel", "test"));

    }

    @Test
    void process() throws Exception {
        queryProcessor.process();

        List<Report> notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.send);

        String result = notificationByStatus.get(0).getResult();
        QueryResult queryResult = objectMapper.readValue(result, QueryResult.class);
        assertEquals("ad8b7bfd-0760-4781-a400-51903ee8e504", queryResult.getResults().get(0).get("shopId"));


        queryProcessor.process();

        notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.created);
        assertEquals(0L, notificationByStatus.size());

        Thread.sleep(1000L);

        queryProcessor.process();
        notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.skipped);
        assertEquals(0L, notificationByStatus.size());

        verify(mailSenderServiceImpl, times(1)).send(any());

    }

}