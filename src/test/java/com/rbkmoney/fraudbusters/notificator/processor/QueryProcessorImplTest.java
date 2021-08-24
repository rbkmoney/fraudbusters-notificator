package com.rbkmoney.fraudbusters.notificator.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.ReportNotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.fraudbusters.notificator.domain.QueryResult;
import com.rbkmoney.fraudbusters.notificator.service.MailSenderServiceImpl;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@PostgresqlSpringBootITest
public class QueryProcessorImplTest {

    @Autowired
    ReportNotificationDao reportNotificationDao;
    @Autowired
    QueryProcessorImpl queryProcessor;
    @Autowired
    private DSLContext dslContext;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    MailSenderServiceImpl mailSenderServiceImpl;

    @MockBean
    QueryService queryService;

    @Test
    void process() throws Exception {
        dslContext.insertInto(CHANNEL)
                .set(TestObjectsFactory.testChannelRecord())
                .execute();
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord successNotification =
                TestObjectsFactory.testNotificationRecord(
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        successNotification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(successNotification)
                .execute();
        NotificationRecord errorNotification =
                TestObjectsFactory.testNotificationRecord(
                        NotificationStatus.ACTIVE, TestObjectsFactory.randomString());
        errorNotification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(errorNotification)
                .execute();
        String shopId = TestObjectsFactory.randomString();
        when(queryService.query(anyString()))
                .thenReturn(List.of(Map.of("shopId", shopId)));

        queryProcessor.process();

        List<Report> notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.send);

        String result = notificationByStatus.get(0).getResult();
        QueryResult queryResult = objectMapper.readValue(result, QueryResult.class);
        assertEquals(shopId, queryResult.getResults().get(0).get("shopId"));

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