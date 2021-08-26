package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationTemplateDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationError;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationResponse;
import com.rbkmoney.fraudbusters.notificator.exception.ValidationNotificationException;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import com.rbkmoney.fraudbusters.notificator.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationResourceImpl implements NotificationResource {

    private final NotificationDao notificationDao;
    private final NotificationTemplateDao notificationTemplateDao;
    private final QueryService queryService;
    private final List<Validator> validators;

    @Override
    @PostMapping(value = "/notifications")
    public Notification createOrUpdate(@Validated @RequestBody Notification notification) {
        ValidationResponse validate = validate(notification);
        if (!CollectionUtils.isEmpty(validate.getErrors())) {
            throw new ValidationNotificationException("Exception when create errors: " + validate);
        }
        Notification savedNotification = notificationDao.insert(notification);
        log.info("NotificationResourceImpl create notification: {}", savedNotification);
        return savedNotification;
    }

    @Override
    @DeleteMapping(value = "/notifications/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@Validated @PathVariable Long id) {
        notificationDao.remove(id);
        log.info("NotificationResourceImpl delete notification with id: {}", id);
    }

    @Override
    @GetMapping(value = "/notifications/{id}/statuses")
    public NotificationStatus updateStatus(@Validated @PathVariable Long id,
                                           @Validated @RequestBody NotificationStatus status) {
        var notification = notificationDao.getById(id);
        notification.setStatus(status);
        notificationDao.insert(notification);
        log.info("NotificationResourceImpl change status notification: {}", notification);
        return status;
    }

    // TODO возможно отсюда это можно убрать и реализовать на уровне fb-mngmnt
    @Override
    @PostMapping(value = "/notifications/validating")
    public ValidationResponse validate(@Validated @RequestBody Notification notification) {
        List<ValidationError> errors = validators.stream()
                .map(validator -> validator.validate(notification))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        ValidationResponse validationResponse = new ValidationResponse();

        if (!CollectionUtils.isEmpty(errors)) {
            validationResponse.setErrors(errors);
            return validationResponse;
        }
        // TODO нужен ли в ответе валидации результат?
        NotificationTemplate notificationTemplate = notificationTemplateDao.getById(notification.getTemplateId());
        List<Map<String, String>> result = queryService.query(notificationTemplate.getQueryText());
        validationResponse.setResult(String.valueOf(result));
        return validationResponse;
    }

    @Override
    @GetMapping(value = "/notifications")
    public List<Notification> getAll() {
        List<Notification> all = notificationDao.getAll();
        log.info("NotificationResourceImpl get all notifications: {}", all);
        return all;
    }

}
