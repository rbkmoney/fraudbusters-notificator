package com.rbkmoney.clickhousenotificator.dao.pg;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.records.ReportRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.clickhousenotificator.dao.domain.Tables.REPORT;

@Component
public class ReportNotificationDaoImpl extends AbstractDao implements ReportNotificationDao {

    private final RowMapper<Report> listRecordRowMapper;

    public ReportNotificationDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(REPORT, Report.class);
    }

    @Override
    public Optional<Long> insert(Report notification) {
        Query query = getDslContext()
                .insertInto(REPORT)
                .set(getDslContext().newRecord(REPORT, notification))
                .onConflict(REPORT.ID)
                .doUpdate()
                .set(getDslContext().newRecord(REPORT, notification))
                .returning(REPORT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public void remove(Long id) {
        DeleteConditionStep<ReportRecord> deleteConditionStep = getDslContext()
                .delete(REPORT)
                .where(REPORT.ID.eq(id));
        execute(deleteConditionStep);
    }

    @Override
    public void remove(Report report) {
        DeleteConditionStep<ReportRecord> deleteConditionStep = getDslContext()
                .delete(REPORT)
                .where(REPORT.ID.eq(report.getId()));
        execute(deleteConditionStep);
    }

    @Override
    public Report getLastByNotification(String name) {
        DSLContext dslContext = getDslContext();
        SelectConditionStep<ReportRecord> where = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.eq(
                        dslContext.select(DSL.max(REPORT.ID))
                                .from(REPORT)
                                .where(REPORT.NOTIFICATION_NAME.eq(name))
                                .and(REPORT.STATUS.eq(ReportStatus.send)))
                );
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public Report getLastByNotificationAndStatus(String name, ReportStatus status) {
        DSLContext dslContext = getDslContext();
        SelectConditionStep<ReportRecord> where = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.eq(
                        dslContext.select(DSL.max(REPORT.ID))
                                .from(REPORT)
                                .where(REPORT.NOTIFICATION_NAME.eq(name)
                                        .and(REPORT.STATUS.eq(status)))));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<Report> getNotificationByStatus(ReportStatus status) {
        DSLContext dslContext = getDslContext();
        SelectConditionStep<ReportRecord> where = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.in(dslContext.select(DSL.max(REPORT.ID))
                        .from(REPORT)
                        .where(REPORT.STATUS.eq(status))));
        return fetch(where, listRecordRowMapper);
    }

    @Override
    public List<Report> getNotificationByStatusAndFromTime(ReportStatus status, LocalDateTime from) {
        DSLContext dslContext = getDslContext();
        SelectConditionStep<ReportRecord> where = dslContext
                .selectFrom(REPORT)
                .where(REPORT.ID.in(dslContext.select(DSL.max(REPORT.ID))
                        .from(REPORT)
                        .where(REPORT.STATUS.eq(status)
                                .and(REPORT.CREATED_AT.lt(from)))));
        return fetch(where, listRecordRowMapper);
    }

}
