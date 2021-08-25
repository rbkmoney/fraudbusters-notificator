package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;

import java.util.List;

public interface ChannelDao {

    Channel insert(Channel channel);

    void remove(String name);

    Channel getByName(String name);

    List<Channel> getAll();

}
