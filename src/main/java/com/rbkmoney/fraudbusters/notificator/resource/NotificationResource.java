package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationResponse;

public interface NotificationResource {

    Notification createOrUpdate(Notification notificationDto);

    void delete(Long id);

    void updateStatus(Long id, NotificationStatus status);

    ValidationResponse validate(Notification notificationDto);

}
