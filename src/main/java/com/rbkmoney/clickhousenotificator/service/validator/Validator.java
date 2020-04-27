package com.rbkmoney.clickhousenotificator.service.validator;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateError;

import java.util.List;

public interface Validator {

    List<ValidateError> validate(Notification notification);

}
