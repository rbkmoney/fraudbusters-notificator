package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplate;
import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplateListResponse;
import com.rbkmoney.damsel.fraudbusters_notificator.NotificationTemplateServiceSrv;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationTemplateDao;
import com.rbkmoney.fraudbusters.notificator.resource.converter.NotificationTemplateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationTemplateHandler implements NotificationTemplateServiceSrv.Iface {

    private final NotificationTemplateDao notificationTemplateDao;
    private final NotificationTemplateConverter notificationTemplateConverter;

    @Override
    public NotificationTemplateListResponse getAll() {
        var notificationTemplates = notificationTemplateDao.getAll();
        log.info("NotificationTemplateHandler get all templates: {}", notificationTemplates);
        List<NotificationTemplate> result = notificationTemplates.stream()
                .map(notificationTemplateConverter::convert)
                .collect(Collectors.toList());
        return new NotificationTemplateListResponse()
                .setNotificationTemplates(result);
    }
}
