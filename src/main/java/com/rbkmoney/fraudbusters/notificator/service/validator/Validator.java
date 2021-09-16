package com.rbkmoney.fraudbusters.notificator.service.validator;

import com.rbkmoney.damsel.fraudbusters_notificator.Notification;

import java.util.List;

public interface Validator {

    List<String> validate(Notification notification);

}
