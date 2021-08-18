package com.rbkmoney.fraudbusters.notificator.service.validator;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationError;
import com.rbkmoney.fraudbusters.notificator.parser.PeriodParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FieldValidator implements Validator {

    public static final String EMPTY_REQUIRED_FIELD_PERIOD = "Empty required field period!";
    private final PeriodParser periodParser;

    @Override
    public List<ValidationError> validate(Notification notification) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (!StringUtils.hasLength(notification.getPeriod())) {
            log.warn(EMPTY_REQUIRED_FIELD_PERIOD);
            validationErrors.add(new ValidationError(EMPTY_REQUIRED_FIELD_PERIOD));
        } else if (periodParser.parse(notification.getPeriod()) == 0L) {
            log.warn("Unknown period value!");
            validationErrors.add(new ValidationError("Unknown period value: " + notification.getPeriod()));
        }

        validateField(validationErrors, notification.getGroupbyparams(), "Empty group params!", "Empty group params," +
                " we sent many notify message to you," +
                " because we can't find old data without this field!");
        validateField(validationErrors, notification.getQueryText(), "Empty query text!", "Query text is required!");
        validateField(validationErrors, notification.getName(), "Empty name!", "Name is required!");
        validateField(validationErrors, notification.getAlertchanel(), "Empty channel!", "Alertchanel is required!");
        validateField(validationErrors, notification.getFrequency(), "Empty frequency!", "frequency  is required!");

        return validationErrors;
    }

    private void validateField(List<ValidationError> validationErrors,
                               String field,
                               String log,
                               String errorMessage) {
        if (!StringUtils.hasLength(field)) {
            FieldValidator.log.warn(log);
            validationErrors.add(new ValidationError(errorMessage));
        }
    }
}
