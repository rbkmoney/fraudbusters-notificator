package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.SelectConditionStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION;

@Component
public class NotificationDaoImpl extends AbstractDao implements NotificationDao {

    private final RowMapper<Notification> listRecordRowMapper;

    public NotificationDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(NOTIFICATION, Notification.class);
    }

    @Override
    public Notification insert(Notification notification) {
        Query query = getDslContext()
                .insertInto(NOTIFICATION)
                .set(getDslContext().newRecord(NOTIFICATION, notification))
                .onConflict(NOTIFICATION.ID)
                .doUpdate()
                .set(getDslContext().newRecord(NOTIFICATION, notification));
        execute(query);
        return notification;
    }

    @Override
    public void remove(Long id) {
        DeleteConditionStep<NotificationRecord> where = getDslContext()
                .delete(NOTIFICATION)
                .where(NOTIFICATION.ID.eq(id));
        execute(where);
    }

    @Override
    public Notification getById(Long id) {
        SelectConditionStep<NotificationRecord> where = getDslContext()
                .selectFrom(NOTIFICATION)
                .where(NOTIFICATION.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<Notification> getByStatus(NotificationStatus status) {
        SelectConditionStep<NotificationRecord> where = getDslContext()
                .selectFrom(NOTIFICATION)
                .where(NOTIFICATION.STATUS.eq(status));
        return fetch(where, listRecordRowMapper);
    }

}
