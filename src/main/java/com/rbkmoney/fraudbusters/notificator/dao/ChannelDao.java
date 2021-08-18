package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;

import java.util.List;

public interface ChannelDao {

    String insert(Channel listRecord);

    void remove(String id);

    void remove(Channel listRecord);

    Channel getByName(String name);

    List<Channel> getAll();

}
