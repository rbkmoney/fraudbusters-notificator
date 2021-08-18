package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;

import java.util.List;

public interface NotificationDao {

    String insert(Notification listRecord);

    void remove(String id);

    void remove(Notification listRecord);

    Notification getByName(String name);

    List<Notification> getList();

    List<Notification> getByStatus(NotificationStatus status);

}
