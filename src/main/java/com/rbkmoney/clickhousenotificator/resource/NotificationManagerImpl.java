package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.converter.ApiNotificationToDbNotification;
import com.rbkmoney.clickhousenotificator.converter.DbNotificationToApiNotification;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.domain.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationManagerImpl implements NotificationManager {

    private final NotificationDao notificationDao;
    private final ApiNotificationToDbNotification apiNotificationToDbNotification;
    private final DbNotificationToApiNotification notificationToApiNotification;

    @Override
    @PostMapping(value = "/notification")
    public Notification create(@Validated @RequestBody Notification notification) {
        var convert = apiNotificationToDbNotification.convert(notification);
        notificationDao.insert(convert);
        return notification;
    }

    @Override
    @DeleteMapping(value = "/notification/{name}")
    public Notification delete(@Validated @PathVariable String name) {
        var notification = notificationDao.getByName(name);
        notificationDao.remove(name);
        return notificationToApiNotification.convert(notification);
    }

    @Override
    @PostMapping(value = "/notification/{name}")
    public void setStatus(@Validated @PathVariable String name,
                          @Validated @RequestBody Status status) {
        var notification = notificationDao.getByName(name);
        notification.setStatus(status.name());
        notificationDao.insert(notification);
    }

    @Override
    @PostMapping(value = "/notification/vallidate")
    public ValidateResponse validate(@Validated @RequestBody Notification notification) {
        return null;
    }

}
