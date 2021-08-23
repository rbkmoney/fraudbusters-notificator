package com.rbkmoney.fraudbusters.notificator;

import com.rbkmoney.fraudbusters.notificator.constant.TemplateType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ChannelType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
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
                                                NotificationStatus status,
                                                String channel) {
        Notification notification = new Notification();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(name);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setChannel(channel);
        notification.setPeriod("1d");
        notification.setStatus(status);
        notification.setSubject("Тестирование сообщений");
        return notification;
    }

    public static Notification testNotification(NotificationStatus status,
                                                String channel) {
        return testNotification("test", status, channel);
    }

    public static NotificationTemplate testNotificationTemplate(String select,
                                                                String groupParams) {
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        LocalDateTime now = LocalDateTime.now();
        notificationTemplate.setName(randomString());
        notificationTemplate.setCreatedAt(now);
        notificationTemplate.setUpdatedAt(now);
        notificationTemplate.setBasicParams(groupParams);
        notificationTemplate.setQueryText(select);
        notificationTemplate.setType(TemplateType.MAIL_FORM.name());
        notificationTemplate.setSkeleton("<>");
        return notificationTemplate;
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
