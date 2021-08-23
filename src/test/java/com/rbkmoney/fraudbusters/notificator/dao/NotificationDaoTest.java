package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@PostgresqlSpringBootITest
//@ContextConfiguration(classes = {NotificationDaoImpl.class})
public class NotificationDaoTest {

    @Autowired
    NotificationDao notificationDao;

    @Test
    void findOne() {
        //create
        Notification notification = TestObjectsFactory.testNotification(
                NotificationStatus.CREATED, "test");
        String insert = notificationDao.insert(notification);

        //get by id
        Notification savedNotification = notificationDao.getByName(insert);
        Assertions.assertEquals("1d", savedNotification.getPeriod());

        //get by status
        List<Notification> notifications = notificationDao.getByStatus(NotificationStatus.CREATED);
        Assertions.assertEquals(1, notifications.size());

        //update
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setName(insert);
        String updateId = notificationDao.insert(notification);
        Assertions.assertEquals(insert, updateId);

        savedNotification = notificationDao.getByName(insert);
        Assertions.assertNotEquals(savedNotification.getCreatedAt(), savedNotification.getUpdatedAt());

        //remove by id
        notificationDao.remove(insert);
        savedNotification = notificationDao.getByName(insert);
        Assertions.assertNull(savedNotification);
    }

}