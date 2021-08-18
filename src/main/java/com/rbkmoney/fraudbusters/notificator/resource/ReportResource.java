package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportResource {

    List<Report> findReportsByStatusAndFromTime(ReportStatus status, LocalDateTime from);

}
