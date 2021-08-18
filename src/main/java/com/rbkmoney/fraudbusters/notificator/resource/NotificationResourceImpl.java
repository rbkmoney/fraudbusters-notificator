package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationError;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationResponse;
import com.rbkmoney.fraudbusters.notificator.exception.ValidationNotificationException;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import com.rbkmoney.fraudbusters.notificator.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("ch-manager")
public class NotificationResourceImpl implements NotificationResource {

    private final NotificationDao notificationDao;
    private final QueryService queryService;
    private final List<Validator> validators;

    @Override
    @PostMapping(value = "/notification")
    public Notification createOrUpdate(@Validated @RequestBody Notification notification) {
        ValidationResponse validate = validate(notification);
        if (!CollectionUtils.isEmpty(validate.getErrors())) {
            throw new ValidationNotificationException("Exception when create errors: " + validate);
        }
        notificationDao.insert(notification);
        log.info("NotificationResourceImpl created notification: {}", notification);
        return notification;
    }

    @Override
    @DeleteMapping(value = "/notification/{name}")
    public Notification delete(@Validated @PathVariable String name) {
        var notification = notificationDao.getByName(name);
        notificationDao.remove(name);
        log.info("NotificationResourceImpl deleted notification: {}", notification);
        return notification;
    }

    @Override
    @GetMapping(value = "/notification/{name}/{status}")
    public void setStatus(@Validated @PathVariable String name,
                          @Validated @PathVariable NotificationStatus status) {
        var notification = notificationDao.getByName(name);
        notification.setStatus(status);
        notificationDao.insert(notification);
        log.info("NotificationResourceImpl changed status notification: {}", notification);
    }

    @Override
    @PostMapping(value = "/notification/validate")
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

        List<Map<String, String>> result = queryService.query(notification.getQueryText());
        validationResponse.setResult(String.valueOf(result));
        return validationResponse;
    }

}
