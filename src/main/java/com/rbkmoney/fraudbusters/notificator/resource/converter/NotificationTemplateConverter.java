package com.rbkmoney.fraudbusters.notificator.resource.converter;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class NotificationTemplateConverter
        implements
        Converter<NotificationTemplate, com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate> {

    @Override
    public com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate convert(
            NotificationTemplate notificationTemplate) {
        var result = new com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate();
        result.setId(notificationTemplate.getId());
        result.setName(notificationTemplate.getName());
        result.setBasicParams(notificationTemplate.getBasicParams());
        result.setQueryText(notificationTemplate.getQueryText());
        result.setSkeleton(notificationTemplate.getSkeleton());
        result.setType(notificationTemplate.getType());
        if (Objects.nonNull(notificationTemplate.getUpdatedAt())) {
            result.setUpdatedAt(notificationTemplate.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        }
        if (Objects.nonNull(notificationTemplate.getCreatedAt())) {
            result.setCreatedAt(notificationTemplate.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        }
        return result;
    }
}
