package com.rbkmoney.clickhousenotificator.util;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.ChannelType;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;

import java.time.LocalDateTime;

public class ChannelFactory {

    public static final String CHANNEL = "channel";

    public static Channel createChannel() {
        Channel channel = new Channel();
        channel.setName(CHANNEL);
        channel.setDestination(" test@mail.ru, two@test.ru");
        channel.setSubject("Тесты");
        channel.setCreatedAt(LocalDateTime.now());
        channel.setType(ChannelType.mail);
        return channel;
    }

}
