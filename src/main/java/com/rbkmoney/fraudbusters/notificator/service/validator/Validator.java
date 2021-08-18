package com.rbkmoney.fraudbusters.notificator.service.validator;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationError;

import java.util.List;

public interface Validator {

    List<ValidationError> validate(Notification notification);

}
