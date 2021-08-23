package com.rbkmoney.fraudbusters.notificator.dao;


import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportNotificationDao {

    Optional<Long> insert(Report listRecord);

    void remove(Long id);

    void remove(Report listRecord);

    Report getLastSendById(Long id);

    List<Report> getNotificationByStatus(ReportStatus status);

    List<Report> getNotificationByStatusAndFromTime(ReportStatus status, LocalDateTime from);

}
