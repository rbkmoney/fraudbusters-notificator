package com.rbkmoney.clickhousenotificator.resource;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportResource {

    List<Report> findReportsByStatusAndFromTime(ReportStatus status, LocalDateTime from);

}
