package com.rbkmoney.clickhousenotificator.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiNotificationToDbNotification implements Converter<Notification, com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification> {

    private final ObjectMapper objectMapper;

    @Override
    public com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification convert(Notification notification) {
        var notificationDb = new com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification();
        notificationDb.setName(notification.getName());
        notificationDb.setTemplateValue(notification.getTemplate().getValue());
        notificationDb.setTemplateType(notification.getTemplate().getType().name());
        notificationDb.setStatus(notification.getStatus().name());
        notificationDb.setQueryText(notification.getSelect());
        notificationDb.setPeriod(notification.getPeriod());
        notificationDb.setFrequency(notification.getFrequency());
        notificationDb.setAlertchanel(notification.getAlertChanel());
        try {
            notificationDb.setParameter(objectMapper.writeValueAsString(notification.getParameters()));
        } catch (JsonProcessingException e) {
            log.warn("ApiNotificationToDbNotification error whene serialize parameters: {}", notification.getParameters());
        }
        return notificationDb;
    }
}
