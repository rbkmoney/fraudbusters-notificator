package com.rbkmoney.clickhousenotificator;

import com.rbkmoney.clickhousenotificator.constant.TemplateType;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.ChannelType;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.warehouse.Row;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class TestObjectsFactory {

    public static final String CHANNEL = "channel";

    public static Row testRow() {
        Map<String, String> rowValues = new HashMap<>();
        rowValues.put(randomString(), randomString());
        rowValues.put(randomString(), randomString());
        rowValues.put(randomString(), randomString());
        Row row = new Row();
        row.setValues(rowValues);
        return row;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static Notification testNotification(String name,
                                                String select,
                                                NotificationStatus status,
                                                String channel,
                                                String groupParams) {
        Notification notification = new Notification();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(name);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setGroupbyparams(groupParams);
        notification.setAlertchanel(channel);
        notification.setPeriod("1d");
        notification.setQueryText(select);
        notification.setStatus(status);
        notification.setTemplateType(TemplateType.MAIL_FORM.name());
        notification.setTemplateValue("<>");
        notification.setSubject("Тестирование сообщений");
        return notification;
    }

    public static Notification testNotification(String select,
                                                NotificationStatus status,
                                                String channel,
                                                String groupParams) {
        return testNotification("test", select, status, channel, groupParams);
    }

    public static Channel testChannel() {
        Channel channel = new Channel();
        channel.setName(CHANNEL);
        channel.setDestination(" test@mail.ru, two@test.ru");
        channel.setSubject("Тесты");
        channel.setCreatedAt(LocalDateTime.now());
        channel.setType(ChannelType.mail);
        return channel;
    }

}
