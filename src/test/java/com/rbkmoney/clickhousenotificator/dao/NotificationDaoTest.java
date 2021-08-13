package com.rbkmoney.clickhousenotificator.dao;

import com.rbkmoney.clickhousenotificator.TestObjectsFactory;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDaoImpl;
import com.rbkmoney.clickhousenotificator.util.TestChQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@ContextConfiguration(classes = {NotificationDaoImpl.class})
public class NotificationDaoTest extends AbstractPostgresIntegrationTest {

    @Autowired
    NotificationDao notificationDao;

    @Test
    public void findOne() {
        //create
        Notification notification = TestObjectsFactory.createNotification(TestChQuery.QUERY_METRIC_RECURRENT,
                NotificationStatus.CREATED, "test", "shopId,currency");
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