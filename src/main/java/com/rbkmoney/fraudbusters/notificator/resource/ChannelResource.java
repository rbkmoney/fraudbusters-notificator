package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;

import java.util.List;

public interface ChannelResource {

    Channel createOrUpdate(Channel channel);

    void delete(String name);

    List<Channel> getAll();

}
