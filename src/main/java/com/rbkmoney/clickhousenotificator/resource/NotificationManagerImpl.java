package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.domain.Notification;
import com.rbkmoney.clickhousenotificator.domain.ValidateResponse;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationManagerImpl implements NotificationManager {

    @Override
    public Notification create(Notification notification) {
        return null;
    }

    @Override
    public Notification delete(String id) {
        return null;
    }

    @Override
    public boolean enable(String id) {
        return false;
    }

    @Override
    public boolean disable(String id) {
        return false;
    }

    @Override
    public ValidateResponse validate(Notification notification) {
        return null;
    }

}
