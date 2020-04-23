package com.rbkmoney.clickhousenotificator.domain;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportModel {

    private Notification notification;
    private Report lastReport;
    private Report currentReport;

}
