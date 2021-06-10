package com.rbkmoney.clickhousenotificator.util;

import com.rbkmoney.clickhousenotificator.constant.TemplateType;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class NotificationFactory {

    @NotNull
    public static Notification createNotification(String name,
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

    @NotNull
    public static Notification createNotification(String select,
                                                  NotificationStatus status,
                                                  String channel,
                                                  String groupParams) {
        return createNotification("test", select, status, channel, groupParams);
    }

}
