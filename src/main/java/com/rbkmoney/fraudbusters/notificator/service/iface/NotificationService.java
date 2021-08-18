package com.rbkmoney.fraudbusters.notificator.service.iface;

import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;

public interface NotificationService {

    void send(ReportModel reportModel);

}
