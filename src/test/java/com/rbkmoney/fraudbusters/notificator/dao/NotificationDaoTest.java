package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION;
import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION_TEMPLATE;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@PostgresqlSpringBootITest
public class NotificationDaoTest {

    @Autowired
    DSLContext dslContext;

    @Autowired
    NotificationDao notificationDao;

    @BeforeEach
    void setUp() {
        dslContext.deleteFrom(NOTIFICATION).execute();
    }

    @Test
    void testCreate() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testTableNotification();
        notification.setTemplateId(savedNotificationTemplate.getId());

        Notification savedNotification = notificationDao.insert(notification);

        assertNotNull(savedNotification.getId());
        assertEquals(notification.getPeriod(), savedNotification.getPeriod());
        assertEquals(notification.getName(), savedNotification.getName());

    }

    @Test
    void testGetById() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord();
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);

        Notification actualNotification = notificationDao.getById(savedNotification.getId());

        assertEquals(notification.getPeriod(), actualNotification.getPeriod());
        assertEquals(notification.getName(), actualNotification.getName());
        assertEquals(notification.getChannel(), actualNotification.getChannel());
        assertEquals(notification.getSubject(), actualNotification.getSubject());
    }

    @Test
    void testGetByStatus() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord();
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();

        List<Notification> notifications = notificationDao.getByStatus(notification.getStatus());

        assertEquals(1, notifications.size());
        assertEquals(notification.getName(), notifications.get(0).getName());
    }

    @Test
    void testUpdate() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord();
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);
        String newName = TestObjectsFactory.randomString();
        Notification notificationToUpdate = savedNotification.into(Notification.class);
        notificationToUpdate.setName(newName);
        notificationToUpdate.setUpdatedAt(LocalDateTime.now());

        Notification updatedNotification = notificationDao.insert(notificationToUpdate);

        assertEquals(newName, updatedNotification.getName());
        assertNotEquals(savedNotification.getUpdatedAt(), updatedNotification.getUpdatedAt());
    }

    @Test
    void testRemove() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord();
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);

        notificationDao.remove(savedNotification.getId());

        Result<NotificationRecord> notifications = dslContext.fetch(NOTIFICATION);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testGetAll() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification1 = TestObjectsFactory.testNotificationRecord();
        notification1.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification2 = TestObjectsFactory.testNotificationRecord();
        notification2.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification1)
                .newRecord()
                .set(notification2)
                .execute();

        List<Notification> all = notificationDao.getAll(new FilterDto());

        assertEquals(2, all.size());
        assertIterableEquals(List.of(notification1.getName(), notification2.getName()),
                all.stream()
                        .map(Notification::getName)
                        .collect(Collectors.toList()));

    }

    @Test
    void testGetAllWithFilterContinuationId() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification1 = TestObjectsFactory.testNotificationRecord();
        notification1.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification2 = TestObjectsFactory.testNotificationRecord();
        notification2.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification3 = TestObjectsFactory.testNotificationRecord();
        notification3.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification1)
                .newRecord()
                .set(notification2)
                .newRecord()
                .set(notification3)
                .execute();

        FilterDto filter = new FilterDto();
        filter.setContinuationId(1L);
        List<Notification> all = notificationDao.getAll(filter);

        assertEquals(2, all.size());
        assertIterableEquals(List.of(notification2.getName(), notification3.getName()),
                all.stream()
                        .map(Notification::getName)
                        .collect(Collectors.toList()));

    }

    @Test
    void testGetAllWithFilterSearchField() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification1 = TestObjectsFactory.testNotificationRecord();
        notification1.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification2 = TestObjectsFactory.testNotificationRecord();
        notification2.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification3 = TestObjectsFactory.testNotificationRecord();
        notification3.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification1)
                .newRecord()
                .set(notification2)
                .newRecord()
                .set(notification3)
                .execute();

        FilterDto filter = new FilterDto();
        filter.setSearchFiled(notification2.getName());
        List<Notification> all = notificationDao.getAll(filter);

        assertEquals(1, all.size());
        assertEquals(notification2.getSubject(), all.get(0).getSubject());

    }

    @Test
    void testGetAllWithFilterSize() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification1 = TestObjectsFactory.testNotificationRecord();
        notification1.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification2 = TestObjectsFactory.testNotificationRecord();
        notification2.setTemplateId(savedNotificationTemplate.getId());
        NotificationRecord notification3 = TestObjectsFactory.testNotificationRecord();
        notification3.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification1)
                .newRecord()
                .set(notification2)
                .newRecord()
                .set(notification3)
                .execute();

        FilterDto filter = new FilterDto();
        filter.setSize(2L);
        List<Notification> all = notificationDao.getAll(filter);

        assertEquals(2, all.size());
        assertIterableEquals(List.of(notification1.getName(), notification2.getName()),
                all.stream()
                        .map(Notification::getName)
                        .collect(Collectors.toList()));

    }
}
