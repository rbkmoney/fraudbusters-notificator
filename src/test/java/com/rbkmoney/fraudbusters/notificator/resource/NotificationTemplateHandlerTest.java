package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplateListResponse;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION;
import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@PostgresqlSpringBootITest
class NotificationTemplateHandlerTest {

    @Autowired
    private NotificationTemplateHandler notificationTemplateHandler;

    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    void setUp() {
        dslContext.deleteFrom(NOTIFICATION).execute();
        dslContext.deleteFrom(NOTIFICATION_TEMPLATE).execute();
    }

    @Test
    void getAll() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .newRecord()
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateListResponse result = notificationTemplateHandler.getAll();

        assertEquals(2, result.getNotificationTemplatesSize());

    }
}