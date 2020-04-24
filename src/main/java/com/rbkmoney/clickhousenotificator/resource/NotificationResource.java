package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateResponse;

public interface NotificationResource {

    Notification createOrUpdate(Notification notificationDto);

    Notification delete(String id);

    void setStatus(String id, Status status);

    ValidateResponse validate(Notification notificationDto);

}
