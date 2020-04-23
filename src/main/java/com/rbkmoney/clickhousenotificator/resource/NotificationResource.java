package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.domain.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateResponse;

public interface NotificationResource {

    Notification create(Notification notification);

    Notification delete(String id);

    void setStatus(String id, Status status);

    ValidateResponse validate(Notification notification);

}
