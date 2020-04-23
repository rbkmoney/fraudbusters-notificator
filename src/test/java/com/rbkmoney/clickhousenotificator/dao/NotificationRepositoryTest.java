package com.rbkmoney.clickhousenotificator.dao;

import com.rbkmoney.clickhousenotificator.config.CustomHikariConfig;
import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDaoImpl;
import com.rbkmoney.clickhousenotificator.util.NotificationFactory;
import com.rbkmoney.clickhousenotificator.util.TestChQuery;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@ContextConfiguration(classes = {NotificationDaoImpl.class, CustomHikariConfig.class})
public class NotificationRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    NotificationDao notificationDao;

    @Test
    public void findOne() {
        //create
        Notification notification = NotificationFactory.createNotification(TestChQuery.QUERY_METRIC_RECURRENT, Status.CREATED);
        String insert = notificationDao.insert(notification);

        //get by id
        Notification savedNotification = notificationDao.getById(insert);
        Assert.assertEquals("1d", savedNotification.getPeriod());

        //get by status
        List<Notification> notifications = notificationDao.getByStatus(Status.CREATED);
        Assert.assertEquals(1, notifications.size());

        //update
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setName(insert);
        String updateId = notificationDao.insert(notification);
        Assert.assertEquals(insert, updateId);

        savedNotification = notificationDao.getById(insert);
        Assert.assertNotEquals(savedNotification.getCreatedAt(), savedNotification.getUpdatedAt());

        //remove by id
        notificationDao.remove(insert);
        savedNotification = notificationDao.getById(insert);
        Assert.assertNull(savedNotification);
    }

}