package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.NotificationTemplate;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.SelectConditionStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION_TEMPLATE;

@Component
public class NotificationTemplateDaoImpl extends AbstractDao implements NotificationTemplateDao {

    private final RowMapper<NotificationTemplate> listRecordRowMapper;

    public NotificationTemplateDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(NOTIFICATION_TEMPLATE, NotificationTemplate.class);
    }

    @Override
    public NotificationTemplate getById(Integer id) {
        SelectConditionStep<NotificationTemplateRecord> where = getDslContext()
                .selectFrom(NOTIFICATION_TEMPLATE)
                .where(NOTIFICATION_TEMPLATE.ID.eq(id));
        return fetchOne(where, listRecordRowMapper);
    }
}
