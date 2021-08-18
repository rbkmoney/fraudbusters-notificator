package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.dao.ReportNotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("ch-manager")
public class ReportResourceImpl implements ReportResource {

    private final ReportNotificationDao reportNotificationDao;

    @Override
    @GetMapping(value = "/report/{status}/{from}")
    public List<Report> findReportsByStatusAndFromTime(@Validated @PathVariable ReportStatus status,
                                                       @Validated @PathVariable LocalDateTime from) {
        List<Report> reports = reportNotificationDao.getNotificationByStatusAndFromTime(status, from);
        log.info("ReportResourceImpl findReportsByStatusAndFromTime reports: {}", reports);
        return reports;
    }

}
