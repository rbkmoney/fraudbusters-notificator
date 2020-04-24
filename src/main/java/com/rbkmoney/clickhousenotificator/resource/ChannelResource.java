package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;

import java.util.List;

public interface ChannelResource {

    Channel createOrUpdate(Channel channel);

    Channel delete(String id);

    List<Channel> getAll();

}
