package com.rbkmoney.clickhousenotificator.dao.pg;


import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportNotificationDao {

    Optional<Long> insert(Report listRecord);

    void remove(Long id);

    void remove(Report listRecord);

    Report getLastByNotification(String name);

    Report getLastByNotificationAndStatus(String name, ReportStatus status);

    List<Report> getNotificationByStatus(ReportStatus status);

    List<Report> getNotificationByStatusAndFromTime(ReportStatus status, LocalDateTime from);

}
