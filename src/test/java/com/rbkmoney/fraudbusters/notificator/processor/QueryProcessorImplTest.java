package com.rbkmoney.fraudbusters.notificator.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.ChannelDaoImpl;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.ReportNotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.fraudbusters.notificator.domain.QueryResult;
import com.rbkmoney.fraudbusters.notificator.service.MailSenderServiceImpl;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@PostgresqlSpringBootITest
public class QueryProcessorImplTest {

    @Autowired
    NotificationDao notificationDao;
    @Autowired
    ChannelDaoImpl channelDao;
    @Autowired
    ReportNotificationDao reportNotificationDao;
    @Autowired
    QueryProcessorImpl queryProcessor;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    MailSenderServiceImpl mailSenderServiceImpl;

    @MockBean
    QueryService queryService;

    @Test
    void process() throws Exception {
        channelDao.insert(TestObjectsFactory.testChannel());
        Notification successNotification =
                TestObjectsFactory.testNotification("successNotify",
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        notificationDao.insert(successNotification);
        Notification errorNotification =
                TestObjectsFactory.testNotification("failedName",
                        NotificationStatus.ACTIVE, "errorChannel");
        notificationDao.insert(errorNotification);
        when(queryService.query(anyString()))
                .thenReturn(List.of(Map.of("shopId", "ad8b7bfd-0760-4781-a400-51903ee8e504")));
        notificationDao.getList();

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