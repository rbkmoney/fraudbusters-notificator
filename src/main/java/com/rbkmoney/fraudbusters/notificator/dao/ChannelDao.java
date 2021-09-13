package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ChannelType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;

import java.util.List;

public interface ChannelDao {

    Channel insert(Channel channel);

    void remove(String name);

    Channel getByName(String name);

    List<Channel> getAll(FilterDto filter);

    List<ChannelType> getAllTypes();

}
