package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;

import java.util.List;

public interface NotificationTemplateDao {

    NotificationTemplate getById(Integer id);

    List<NotificationTemplate> getAll();


}
