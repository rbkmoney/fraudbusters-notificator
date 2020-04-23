package com.rbkmoney.clickhousenotificator.dao.pg;


import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationDao {

    String insert(Notification listRecord);

    void remove(String id);

    void remove(Notification listRecord);

    Notification getById(String id);

    List<Notification> getList();

    List<Notification> getByStatus(Status status);
}
