package com.rbkmoney.clickhousenotificator.service.validator;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateError;
import com.rbkmoney.clickhousenotificator.parser.PeriodParser;
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
    public List<ValidateError> validate(Notification notification) {
        List<ValidateError> validateErrors = new ArrayList<>();
        if (StringUtils.isEmpty(notification.getPeriod())) {
            log.warn(EMPTY_REQUIRED_FIELD_PERIOD);
            validateErrors.add(new ValidateError(EMPTY_REQUIRED_FIELD_PERIOD));
        } else if (periodParser.parse(notification.getPeriod()) == 0L) {
            log.warn("Unknown period value!");
            validateErrors.add(new ValidateError("Unknown period value: " + notification.getPeriod()));
        }

        validateField(validateErrors, notification.getGroupbyparams(), "Empty group params!", "Empty group params," +
                " we sent many notify message to you," +
                " because we can't find old data without this field!");
        validateField(validateErrors, notification.getQueryText(), "Empty query text!", "Query text is required!");
        validateField(validateErrors, notification.getName(), "Empty name!", "Name is required!");
        validateField(validateErrors, notification.getAlertchanel(), "Empty channel!", "Alertchanel is required!");
        validateField(validateErrors, notification.getFrequency(), "Empty frequency!", "frequency  is required!");

        return validateErrors;
    }

    private void validateField(List<ValidateError> validateErrors, String queryText, String s, String s2) {
        if (StringUtils.isEmpty(queryText)) {
            log.warn(s);
            validateErrors.add(new ValidateError(s2));
        }
    }
}
