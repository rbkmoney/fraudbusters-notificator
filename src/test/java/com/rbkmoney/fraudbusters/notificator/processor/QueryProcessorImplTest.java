package com.rbkmoney.fraudbusters.notificator.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ReportRecord;
import com.rbkmoney.fraudbusters.notificator.domain.QueryResult;
import com.rbkmoney.fraudbusters.notificator.service.MailSenderServiceImpl;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@PostgresqlSpringBootITest
public class QueryProcessorImplTest {

    @Autowired
    QueryProcessorImpl queryProcessor;

    @Autowired
    private DSLContext dslContext;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    MailSenderServiceImpl mailSenderServiceImpl;

    @MockBean
    QueryService queryService;


    @BeforeEach
    void setUp() {
        dslContext.delete(NOTIFICATION).execute();
    }

    @Test
    void process() throws Exception {
        ChannelRecord channel = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel)
                .execute();
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord successNotification = TestObjectsFactory.testNotificationRecord();
        successNotification.setChannel(channel.getName());
        successNotification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(successNotification)
                .execute();
        NotificationRecord errorNotification = TestObjectsFactory.testNotificationRecord();
        errorNotification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(errorNotification)
                .execute();
        String shopId = TestObjectsFactory.randomString();
        when(queryService.query(anyString()))
                .thenReturn(List.of(Map.of("shopId", shopId)));

        queryProcessor.process();

        List<ReportRecord> notificationByStatus = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.in(dslContext.select(DSL.max(REPORT.ID))
                        .from(REPORT)
                        .where(REPORT.STATUS.eq(ReportStatus.send)))).fetch();

        String result = notificationByStatus.get(0).getResult();
        QueryResult queryResult = objectMapper.readValue(result, QueryResult.class);
        assertEquals(shopId, queryResult.getResults().get(0).get("shopId"));

        queryProcessor.process();

        notificationByStatus = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.in(dslContext.select(DSL.max(REPORT.ID))
                        .from(REPORT)
                        .where(REPORT.STATUS.eq(ReportStatus.created)))).fetch();
        assertEquals(0L, notificationByStatus.size());

        Thread.sleep(1000L);

        queryProcessor.process();

        notificationByStatus = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.in(dslContext.select(DSL.max(REPORT.ID))
                        .from(REPORT)
                        .where(REPORT.STATUS.eq(ReportStatus.skipped)))).fetch();
        assertEquals(0L, notificationByStatus.size());
        verify(mailSenderServiceImpl, times(1)).send(any());
    }
}