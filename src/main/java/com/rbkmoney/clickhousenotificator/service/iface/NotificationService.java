package com.rbkmoney.clickhousenotificator.service.iface;

import com.rbkmoney.clickhousenotificator.domain.ReportModel;

public interface NotificationService {

    void sentNotification(ReportModel reportModel);

}
