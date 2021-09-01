package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;

import java.util.List;

public interface NotificationDao {

    Notification insert(Notification notification);

    void remove(Long id);

    Notification getById(Long id);

    List<Notification> getByStatus(NotificationStatus status);

    List<Notification> getAll();

}
