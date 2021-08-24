package com.rbkmoney.fraudbusters.notificator;

import com.rbkmoney.fraudbusters.notificator.constant.TemplateType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ChannelType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
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

    public static Notification testNotification(NotificationRecord notificationRecord) {
        Notification notification = new Notification();
        notification.setUpdatedAt(notificationRecord.getUpdatedAt());
        notification.setId(notificationRecord.getId());
        notification.setName(notificationRecord.getName());
        notification.setChannel(notificationRecord.getChannel());
        notification.setCreatedAt(notificationRecord.getCreatedAt());
        notification.setStatus(notificationRecord.getStatus());
        notification.setFrequency(notificationRecord.getFrequency());
        notification.setPeriod(notificationRecord.getPeriod());
        notification.setSubject(notificationRecord.getSubject());
        notification.setTemplateId(notificationRecord.getTemplateId());
        return notification;
    }

    public static NotificationRecord testNotificationRecord(NotificationStatus status,
                                                            String channel) {
        NotificationRecord notification = new NotificationRecord();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(randomString());
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setChannel(channel);
        notification.setPeriod("1d");
        notification.setStatus(status);
        notification.setSubject("Тестирование сообщений");
        return notification;
    }

    public static NotificationTemplate testNotificationTemplate(String groupParams) {
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        LocalDateTime now = LocalDateTime.now();
        notificationTemplate.setName(randomString());
        notificationTemplate.setCreatedAt(now);
        notificationTemplate.setUpdatedAt(now);
        notificationTemplate.setBasicParams(groupParams);
        notificationTemplate.setQueryText(randomString());
        notificationTemplate.setType(TemplateType.MAIL_FORM.name());
        notificationTemplate.setSkeleton("<>");
        return notificationTemplate;
    }

    public static NotificationTemplateRecord testNotificationTemplateRecord() {
        NotificationTemplateRecord notificationTemplateRecord = new NotificationTemplateRecord();
        LocalDateTime now = LocalDateTime.now();
        notificationTemplateRecord.setName(randomString());
        notificationTemplateRecord.setCreatedAt(now);
        notificationTemplateRecord.setUpdatedAt(now);
        notificationTemplateRecord.setBasicParams(randomString());
        notificationTemplateRecord.setQueryText(randomString());
        notificationTemplateRecord.setType(TemplateType.MAIL_FORM.name());
        notificationTemplateRecord.setSkeleton("<>");
        return notificationTemplateRecord;
    }

    public static ChannelRecord testChannelRecord() {
        ChannelRecord channelRecord = new ChannelRecord();
        channelRecord.setName(CHANNEL);
        channelRecord.setDestination(" test@mail.ru, two@test.ru");
        channelRecord.setSubject(randomString());
        channelRecord.setCreatedAt(LocalDateTime.now());
        channelRecord.setType(ChannelType.mail);
        return channelRecord;
    }

}
