package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.domain.ValidateError;
import com.rbkmoney.clickhousenotificator.domain.ValidateResponse;
import com.rbkmoney.clickhousenotificator.service.QueryService;
import com.rbkmoney.clickhousenotificator.service.validator.Validator;
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
public class NotificationResourceImpl implements NotificationResource {

    private final NotificationDao notificationDao;
    private final QueryService queryService;
    private final List<Validator> validators;

    @Override
    @PostMapping(value = "/notification")
    public Notification createOrUpdate(@Validated @RequestBody Notification notification) {
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
    public ValidateResponse validate(@Validated @RequestBody Notification notification) {
        List<ValidateError> errors = validators.stream()
                .map(validator -> validator.validate(notification))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        ValidateResponse validateResponse = new ValidateResponse();

        if (!CollectionUtils.isEmpty(errors)) {
            validateResponse.setErrors(errors);
            return validateResponse;
        }

        List<Map<String, String>> result = queryService.query(notification);
        validateResponse.setResult(String.valueOf(result));
        return validateResponse;
    }

}
