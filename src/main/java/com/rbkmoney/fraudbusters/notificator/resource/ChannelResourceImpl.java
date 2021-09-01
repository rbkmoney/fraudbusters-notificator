package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.ChannelDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChannelResourceImpl implements ChannelResource {

    private final ChannelDao channelDao;

    @Override
    @PostMapping(value = "/channels")
    public Channel createOrUpdate(@Validated @RequestBody Channel channel) {
        Channel savedChannel = channelDao.insert(channel);
        log.info("ChannelResourceImpl create channel: {}", savedChannel);
        return savedChannel;
    }

    @Override
    @DeleteMapping(value = "/channels/{name}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@Validated @PathVariable String name) {
        channelDao.remove(name);
        log.info("ChannelResourceImpl delete channel with name: {}", name);
    }

    @Override
    @GetMapping(value = "/channels")
    public List<Channel> getAll() {
        List<Channel> all = channelDao.getAll();
        log.info("ChannelResourceImpl get all channels: {}", all);
        return all;
    }
}
