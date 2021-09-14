package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationTemplateDao;
import com.rbkmoney.fraudbusters.notificator.exception.ValidationNotificationException;
import com.rbkmoney.fraudbusters.notificator.resource.converter.NotificationConverter;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.notificator.service.dto.PageDto;
import com.rbkmoney.fraudbusters.notificator.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler implements NotificationServiceSrv.Iface {

    private final NotificationDao notificationDao;
    private final NotificationTemplateDao notificationTemplateDao;
    private final QueryService queryService;
    private final List<Validator> validators;
    private final NotificationConverter notificationConverter;

    @Override
    public Notification create(Notification notification) {
        var validationResult = validate(notification);
        if (validationResult.isSetErrors()) {
            throw new ValidationNotificationException(
                    "Exception when create notification, errors: " + String.join(", ", validationResult.getErrors()));
        }
        var savedNotification = notificationDao.insert(notificationConverter.toTarget(notification));
        log.info("NotificationHandler create notification: {}", savedNotification);
        return notificationConverter.toSource(savedNotification);
    }

    @Override
    public void remove(long id) {
        notificationDao.remove(id);
        log.info("NotificationHandler delete notification with id: {}", id);
    }

    @Override
    public void updateStatus(long id, NotificationStatus status) {
        var notification = notificationDao.getById(id);
        notification.setStatus(
                com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus.valueOf(status.name()));
        notificationDao.insert(notification);
        log.info("NotificationHandler change status notification: {}", notification);
    }

    @Override
    public ValidationResponse validate(Notification notification) {
        List<String> errors = validators.stream()
                .map(validator -> validator.validate(notification))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        var validationResponse = new ValidationResponse();
        if (!CollectionUtils.isEmpty(errors)) {
            validationResponse.setErrors(errors);
            log.info("NotificationHandler notification validation failed with errors: {}", errors);
            return validationResponse;
        }
        var notificationTemplate = notificationTemplateDao.getById((int) notification.getTemplateId());
        List<Map<String, String>> result = queryService.query(notificationTemplate.getQueryText());
        validationResponse.setResult(String.valueOf(result));
        return validationResponse;
    }

    @Override
    public NotificationListResponse getAll(Page page, Filter filter) {
        FilterDto filterDto = FilterDto.builder()
                .searchFiled(filter.getSearchField())
                .page(PageDto.builder()
                        .continuationId(page.getContinuationId())
                        .size(page.getSize())
                        .build())
                .build();
        var notifications = notificationDao.getAll(filterDto);
        log.info("NotificationHandler get all notifications: {}", notifications);
        List<Notification> result = notifications.stream()
                .map(notificationConverter::toSource)
                .collect(Collectors.toList());
        return new NotificationListResponse()
                .setNotifications(result);
    }
}
