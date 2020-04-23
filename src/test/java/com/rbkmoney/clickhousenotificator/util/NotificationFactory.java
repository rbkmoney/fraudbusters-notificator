package com.rbkmoney.clickhousenotificator.util;

import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.constant.TemplateType;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class NotificationFactory {

    @NotNull
    public static Notification createNotification(String name, String select, Status status, String channel) {
        Notification notification = new Notification();
        notification.setFrequency("1s");
        LocalDateTime now = LocalDateTime.now();
        notification.setName(name);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setParameter("[]");
        notification.setAlertchanel(channel);
        notification.setPeriod("1d");
        notification.setQueryText(select);
        notification.setStatus(status.name());
        notification.setTemplateType(TemplateType.MAIL_FORM.name());
        notification.setTemplateValue("<>");
        return notification;
    }

    @NotNull
    public static Notification createNotification(String select, Status status, String channel) {
        return createNotification("test", select, status, channel);
    }

}
