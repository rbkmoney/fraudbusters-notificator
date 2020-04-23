package com.rbkmoney.clickhousenotificator.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.constant.TemplateType;
import com.rbkmoney.clickhousenotificator.domain.Notification;
import com.rbkmoney.clickhousenotificator.domain.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbNotificationToApiNotification implements Converter<com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification, Notification> {

    private final ObjectMapper objectMapper;

    @Override
    public Notification convert(com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification notification) {
        var notificationDb = new Notification();
        notificationDb.setName(notification.getName());
        Template template = new Template();
        template.setType(TemplateType.valueOf(notification.getTemplateType()));
        template.setValue(notification.getTemplateValue());
        notificationDb.setTemplate(template);
        notificationDb.setStatus(Status.valueOf(notification.getStatus()));
        notificationDb.setSelect(notification.getQueryText());
        notificationDb.setPeriod(notification.getPeriod());
        notificationDb.setFrequency(notification.getFrequency());
        notificationDb.setAlertChanel(notification.getAlertchanel());
        try {
            notificationDb.setParameters(objectMapper.readValue(notification.getParameter(), new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            log.warn("ApiNotificationToDbNotification error whene serialize parameters: {}", notification.getParameter());
        }
        return notificationDb;
    }
}
