package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationResponse;
import com.rbkmoney.fraudbusters.notificator.exception.WarehouseQueryException;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import org.apache.thrift.TException;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION;
import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@PostgresqlSpringBootITest
class NotificationResourceImplTest {

    @Autowired
    private NotificationResource notificationResource;

    @Autowired
    private DSLContext dslContext;

    @MockBean
    private QueryService queryService;

    @BeforeEach
    void setUp() {
        dslContext.delete(NOTIFICATION);
    }

    @Test
    void createOrUpdate() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification =
                TestObjectsFactory.testNotification(TestObjectsFactory.randomString(),
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        notification.setTemplateId(savedNotificationTemplate.getId());
        Map<String, String> values = new HashMap<>();
        values.put("key", "value");
        List<Map<String, String>> result = List.of(values);
        when(queryService.query(savedNotificationTemplate.getQueryText())).thenReturn(result);

        assertDoesNotThrow(() -> notificationResource.createOrUpdate(notification));

        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);
        assertNotNull(savedNotification.getId());
        assertEquals(notification.getName(), savedNotification.getName());
        assertEquals(notification.getFrequency(), savedNotification.getFrequency());
        assertEquals(notification.getPeriod(), savedNotification.getPeriod());
        assertEquals(notification.getTemplateId(), savedNotification.getTemplateId());
        assertEquals(notification.getChannel(), savedNotification.getChannel());
    }

    @Test
    void delete() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord(
                NotificationStatus.CREATED, TestObjectsFactory.randomString());
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);

        Assertions.assertDoesNotThrow(() -> notificationResource.delete(savedNotification.getId()));

        assertTrue(dslContext.fetch(NOTIFICATION).isEmpty());
    }

    @Test
    void setStatus() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord(
                NotificationStatus.CREATED, "test");
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);
        NotificationStatus newStatus = NotificationStatus.ARCHIVE;

        assertDoesNotThrow(() -> notificationResource.updateStatus(savedNotification.getId(), newStatus));

        NotificationRecord updatedNotification = dslContext.fetchOne(NOTIFICATION);
        assertEquals(newStatus, updatedNotification.getStatus());
    }

    @Test
    void validateWithFieldError() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification(null,
                NotificationStatus.ACTIVE, null);
        notification.setTemplateId(savedNotificationTemplate.getId());

        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertNull(validationResponse.getResult());
        assertEquals(2, validationResponse.getErrors().size());
    }

    @Test
    void validateWithQueryError() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification =
                TestObjectsFactory.testNotification(TestObjectsFactory.randomString(),
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        notification.setTemplateId(savedNotificationTemplate.getId());
        when(queryService.query(savedNotificationTemplate.getQueryText()))
                .thenThrow(new WarehouseQueryException(new TException()));

        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertNull(validationResponse.getResult());
        assertEquals(1, validationResponse.getErrors().size());
        assertThat(validationResponse.getErrors().get(0).getErrorReason(), containsString("Query has error"));
    }

    @Test
    void validateWithAllErrors() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification(null,
                NotificationStatus.ACTIVE, null);
        notification.setTemplateId(savedNotificationTemplate.getId());
        when(queryService.query(anyString())).thenThrow(new WarehouseQueryException(new TException()));

        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertNull(validationResponse.getResult());
        assertEquals(3, validationResponse.getErrors().size());
    }

    @Test
    void validateOk() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification =
                TestObjectsFactory.testNotification(TestObjectsFactory.randomString(),
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        notification.setTemplateId(savedNotificationTemplate.getId());
        Map<String, String> values = new HashMap<>();
        values.put("key", "value");
        List<Map<String, String>> result = List.of(values);
        when(queryService.query(savedNotificationTemplate.getQueryText())).thenReturn(result);

        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertTrue(CollectionUtils.isEmpty(validationResponse.getErrors()));
        assertEquals(String.valueOf(result), validationResponse.getResult());
    }
}
