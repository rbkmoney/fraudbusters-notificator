package com.rbkmoney.fraudbusters.notificator.domain;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportModel {

    private Notification notification;
    private NotificationTemplate notificationTemplate;
    private Report lastReport;
    private Report currentReport;

}
