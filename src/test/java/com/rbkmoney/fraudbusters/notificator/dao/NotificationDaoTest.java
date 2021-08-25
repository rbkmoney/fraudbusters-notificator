package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION;
import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION_TEMPLATE;
import static org.junit.jupiter.api.Assertions.*;

@PostgresqlSpringBootITest
public class NotificationDaoTest {

    @Autowired
    DSLContext dslContext;

    @Autowired
    NotificationDao notificationDao;

    @BeforeEach
    void setUp() {
        dslContext.delete(NOTIFICATION);
    }

    @Test
    void testCreate() {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification();
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
        Notification updatedNotification = savedNotification.into(Notification.class);
        updatedNotification.setName(newName);
        updatedNotification.setUpdatedAt(LocalDateTime.now());

        notificationDao.insert(updatedNotification);

        NotificationRecord actualNotification = dslContext.fetchOne(NOTIFICATION);
        assertEquals(newName, actualNotification.getName());
        assertNotEquals(savedNotification.getUpdatedAt(), actualNotification.getUpdatedAt());
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

        List<Notification> all = notificationDao.getAll();

        assertEquals(2, all.size());
        assertIterableEquals(List.of(notification1.getName(), notification2.getName()),
                all.stream()
                        .map(Notification::getName)
                        .collect(Collectors.toList()));

    }
}
