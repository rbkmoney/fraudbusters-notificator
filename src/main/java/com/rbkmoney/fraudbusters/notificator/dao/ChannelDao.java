package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;

import java.util.List;

public interface ChannelDao {

    String insert(Channel channel);

    void remove(String id);

    Channel getByName(String name);

    List<Channel> getAll();

}
