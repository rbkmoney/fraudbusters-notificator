package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.clickhousenotificator.dao.pg.ChannelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("ch-manager")
public class ChannelResourceImpl implements ChannelResource {

    private final ChannelDao channelDao;

    @Override
    @PostMapping(value = "/channel")
    public Channel createOrUpdate(@Validated @RequestBody Channel channel) {
        channelDao.insert(channel);
        log.info("ChannelResourceImpl created channel: {}", channel);
        return channel;
    }

    @Override
    @DeleteMapping(value = "/channel/{name}")
    public Channel delete(@Validated @PathVariable String name) {
        Channel channel = channelDao.getByName(name);
        channelDao.remove(name);
        log.info("ChannelResourceImpl deleted channel: {}", channel);
        return channel;
    }

    @Override
    @GetMapping(value = "/channel")
    public List<Channel> getAll() {
        List<Channel> all = channelDao.getAll();
        log.info("ChannelResourceImpl all channel: {}", all);
        return all;
    }
}
