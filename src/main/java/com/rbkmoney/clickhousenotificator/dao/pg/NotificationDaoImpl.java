package com.rbkmoney.clickhousenotificator.dao.pg;

import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jetbrains.annotations.NotNull;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.clickhousenotificator.dao.domain.Tables.NOTIFICATION;

@Component
public class NotificationDaoImpl extends AbstractDao implements NotificationDao {

    private final RowMapper<Notification> listRecordRowMapper;

    public NotificationDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(NOTIFICATION, Notification.class);
    }

    @Override
    public String insert(Notification notification) {
        Query query = getDslContext()
                .insertInto(NOTIFICATION)
                .set(getDslContext().newRecord(NOTIFICATION, notification))
                .onConflict(NOTIFICATION.NAME)
                .doUpdate()
                .set(getDslContext().newRecord(NOTIFICATION, notification));
        execute(query);
        return notification.getName();
    }

    @Override
    public void remove(String name) {
        DeleteConditionStep<NotificationRecord> where = getDslContext()
                .delete(NOTIFICATION)
                .where(NOTIFICATION.NAME.eq(name));
        execute(where);
    }

    @Override
    public void remove(Notification notification) {
        DeleteConditionStep<NotificationRecord> where = getDslContext()
                .delete(NOTIFICATION)
                .where(NOTIFICATION.NAME.eq(notification.getName()));
        execute(where);
    }

    @Override
    public Notification getByName(String name) {
        SelectConditionStep<NotificationRecord> where = getDslContext()
                .selectFrom(NOTIFICATION)
                .where(NOTIFICATION.NAME.eq(name));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<Notification> getList() {
        SelectWhereStep<NotificationRecord> notificationRecords = getDslContext()
                .selectFrom(NOTIFICATION);
        return fetch(notificationRecords, listRecordRowMapper);
    }

    @Override
    public List<Notification> getByStatus(@NotNull NotificationStatus status) {
        SelectConditionStep<NotificationRecord> where = getDslContext()
                .selectFrom(NOTIFICATION)
                .where(NOTIFICATION.STATUS.eq(status));
        return fetch(where, listRecordRowMapper);
    }

}
