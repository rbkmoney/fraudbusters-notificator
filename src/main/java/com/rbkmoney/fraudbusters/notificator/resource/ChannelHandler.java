package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.notificator.dao.ChannelDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ChannelType;
import com.rbkmoney.fraudbusters.notificator.resource.converter.ChannelConverter;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.notificator.service.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ChannelHandler implements ChannelServiceSrv.Iface {

    private final ChannelDao channelDao;
    private final ChannelConverter channelConverter;

    @Override
    public Channel create(Channel channel) {
        var savedChannel = channelDao.insert(channelConverter.toTarget(channel));
        log.info("ChannelHandler create channel: {}", savedChannel);
        return channelConverter.toSource(savedChannel);
    }

    @Override
    public void remove(String name) {
        channelDao.remove(name);
        log.info("ChannelHandler delete channel with name: {}", name);
    }

    @Override
    public ChannelListResponse getAll(Page page, Filter filter) {
        FilterDto filterDto = FilterDto.builder()
                .searchFiled(filter.getSearchField())
                .page(PageDto.builder()
                        .continuationId(page.getContinuationId())
                        .size(page.getSize())
                        .build())
                .build();
        var channels = channelDao.getAll(filterDto);
        log.info("ChannelHandler get all channels: {}", channels);
        List<com.rbkmoney.damsel.fraudbusters_notificator.Channel> result = channels.stream()
                .map(channelConverter::toSource)
                .collect(Collectors.toList());
        return new ChannelListResponse()
                .setChannels(result);

    }

    @Override
    public ChannelTypeListResponse getAllTypes() {
        List<ChannelType> types = channelDao.getAllTypes();
        List<String> result = types.stream()
                .map(ChannelType::getLiteral)
                .collect(Collectors.toList());
        log.info("ChannelHandler get all channel types: {}", result);
        return new ChannelTypeListResponse()
                .setChannelTypes(result);
    }
}
