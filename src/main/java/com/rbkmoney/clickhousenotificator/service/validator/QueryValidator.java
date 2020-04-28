package com.rbkmoney.clickhousenotificator.service.validator;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateError;
import com.rbkmoney.clickhousenotificator.service.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryValidator implements Validator {

    private final QueryService queryService;

    @Override
    public List<ValidateError> validate(Notification notification) {
        List<ValidateError> validateErrors = new ArrayList<>();
        try {
            queryService.query(notification);
        } catch (Exception e) {
            log.warn("Error when validate query to DB: {} e:", notification.getQueryText(), e);
            validateErrors.add(new ValidateError("Query has error! e: " + e.getCause()));
        }
        return validateErrors;
    }

}
