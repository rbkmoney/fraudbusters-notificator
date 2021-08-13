package com.rbkmoney.clickhousenotificator.service.validator;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidationError;
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
    public List<ValidationError> validate(Notification notification) {
        List<ValidationError> validationErrors = new ArrayList<>();
        try {
            queryService.query(notification.getQueryText());
        } catch (Exception e) {
            log.warn("Error when validate query to DB: {} e:", notification.getQueryText(), e);
            validationErrors.add(new ValidationError("Query has error! e: " + e.getCause()));
        }
        return validationErrors;
    }

}
