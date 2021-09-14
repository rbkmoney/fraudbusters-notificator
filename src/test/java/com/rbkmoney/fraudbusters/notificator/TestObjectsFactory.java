package com.rbkmoney.fraudbusters.notificator;

import com.rbkmoney.damsel.fraudbusters_notificator.Channel;
import com.rbkmoney.damsel.fraudbusters_notificator.Notification;
import com.rbkmoney.fraudbusters.notificator.constant.TemplateType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ChannelType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ReportRecord;
import com.rbkmoney.fraudbusters.warehouse.Row;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public abstract class TestObjectsFactory {

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

    public static Notification testNotification() {
        Notification notification = new Notification();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(randomString());
        notification.setCreatedAt(now.toString());
        notification.setUpdatedAt(now.toString());
        notification.setChannel(randomString());
        notification.setPeriod("1d");
        notification.setStatus(com.rbkmoney.damsel.fraudbusters_notificator.NotificationStatus.ACTIVE);
        notification.setSubject(randomString());
        return notification;
    }

    public static com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification testTableNotification() {
        var notification = new com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(randomString());
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setChannel(randomString());
        notification.setPeriod("1d");
        notification.setStatus(NotificationStatus.ACTIVE);
        notification.setSubject(randomString());
        return notification;
    }

    public static NotificationRecord testNotificationRecord() {
        NotificationRecord notification = new NotificationRecord();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(randomString());
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setChannel(randomString());
        notification.setPeriod("1d");
        notification.setStatus(NotificationStatus.ACTIVE);
        notification.setSubject(randomString());
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
        notificationTemplate.setSkeleton(randomString());
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
        notificationTemplateRecord.setSkeleton(randomString());
        return notificationTemplateRecord;
    }

    public static ChannelRecord testChannelRecord() {
        ChannelRecord channelRecord = new ChannelRecord();
        channelRecord.setName(randomString());
        channelRecord.setDestination(" test@mail.ru, two@test.ru");
        channelRecord.setCreatedAt(LocalDateTime.now());
        channelRecord.setType(ChannelType.mail);
        return channelRecord;
    }

    public static Channel testChannel() {
        Channel channel = new Channel();
        channel.setName(randomString());
        channel.setDestination(" test@mail.ru, two@test.ru");
        channel.setCreatedAt(LocalDateTime.now().toString());
        channel.setType(com.rbkmoney.damsel.fraudbusters_notificator.ChannelType.mail);
        return channel;
    }

    public static ReportRecord testReportRecord() {
        ReportRecord report = new ReportRecord();
        report.setNotificationId(new Random().nextLong());
        report.setStatus(ReportStatus.send);
        report.setResult(randomString());
        report.setCreatedAt(LocalDateTime.now());
        return report;
    }

}
