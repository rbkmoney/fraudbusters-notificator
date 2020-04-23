package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.domain.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateResponse;

public interface NotificationManager {

    Notification create(Notification notification);

    Notification delete(String id);

    boolean enable(String id);

    boolean disable(String id);

    ValidateResponse validate(Notification notification);

}
