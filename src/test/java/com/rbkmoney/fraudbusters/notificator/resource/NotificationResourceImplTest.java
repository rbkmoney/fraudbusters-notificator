package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationResponse;
import com.rbkmoney.fraudbusters.notificator.exception.WarehouseQueryException;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private NotificationDao notificationDao;

    @MockBean
    private QueryService queryService;

    private static final String QUERY = "select * from db";

    @Test
    void createOrUpdate() {
        Notification notification =
                TestObjectsFactory.testNotification("successNotify",
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        Map<String, String> values = new HashMap<>();
        values.put("key", "value");
        List<Map<String, String>> result = List.of(values);
        when(queryService.query(QUERY)).thenReturn(result);

        Assertions.assertDoesNotThrow(() -> notificationResource.createOrUpdate(notification));

        List<Notification> savedNotifications = notificationDao.getList();
        assertEquals(1, savedNotifications.size());
        assertEquals(notification, savedNotifications.get(0));
    }

    @Test
    void delete() {
        Notification notification =
                TestObjectsFactory.testNotification("successNotify",
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        notificationDao.insert(notification);

        Assertions.assertDoesNotThrow(() -> notificationResource.delete(notification.getName()));

        List<Notification> savedNotifications = notificationDao.getList();
        assertTrue(savedNotifications.isEmpty());

    }

    @Test
    void setStatus() {
        Notification notification =
                TestObjectsFactory.testNotification("successNotify",
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        notificationResource.createOrUpdate(notification);
        NotificationStatus newStatus = NotificationStatus.ARCHIVE;

        assertDoesNotThrow(() -> notificationResource.setStatus(notification.getName(), newStatus));

        List<Notification> savedNotifications = notificationDao.getList();
        assertEquals(1, savedNotifications.size());
        assertEquals(newStatus, savedNotifications.get(0).getStatus());
    }

    @Test
    void validateWithFieldError() {
        Notification notification = TestObjectsFactory.testNotification(null,
                NotificationStatus.ACTIVE, null);

        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertNull(validationResponse.getResult());
        assertEquals(4, validationResponse.getErrors().size());

    }

    @Test
    void validateWithQueryError() {
        Notification notification =
                TestObjectsFactory.testNotification("validationResponse",
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        when(queryService.query(QUERY)).thenThrow(new WarehouseQueryException(new TException()));


        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertNull(validationResponse.getResult());
        assertEquals(1, validationResponse.getErrors().size());
        assertThat(validationResponse.getErrors().get(0).getErrorReason(), containsString("Query has error"));

    }

    @Test
    void validateWithAllErrors() {
        Notification notification = TestObjectsFactory.testNotification(null,
                NotificationStatus.ACTIVE, null);
        when(queryService.query(anyString())).thenThrow(new WarehouseQueryException(new TException()));

        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertNull(validationResponse.getResult());
        assertEquals(5, validationResponse.getErrors().size());

    }

    @Test
    void validateOk() {
        Notification notification =
                TestObjectsFactory.testNotification("validationResponse",
                        NotificationStatus.ACTIVE, TestObjectsFactory.CHANNEL);
        Map<String, String> values = new HashMap<>();
        values.put("key", "value");
        List<Map<String, String>> result = List.of(values);
        when(queryService.query(QUERY)).thenReturn(result);


        ValidationResponse validationResponse = notificationResource.validate(notification);

        assertTrue(CollectionUtils.isEmpty(validationResponse.getErrors()));
        assertEquals(String.valueOf(result), validationResponse.getResult());

    }
}