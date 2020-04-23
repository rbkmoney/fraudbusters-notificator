package com.rbkmoney.clickhousenotificator.dao.pg;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.records.ChannelRecord;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.SelectConditionStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.clickhousenotificator.dao.domain.Tables.CHANNEL;

@Component
public class ChannelDaoImpl extends AbstractDao implements ChannelDao {

    private final RowMapper<Channel> listRecordRowMapper;

    public ChannelDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(CHANNEL, Channel.class);
    }

    @Override
    public String insert(Channel channel) {
        Query query = getDslContext()
                .insertInto(CHANNEL)
                .set(getDslContext().newRecord(CHANNEL, channel))
                .onConflict(CHANNEL.NAME)
                .doUpdate()
                .set(getDslContext().newRecord(CHANNEL, channel));
        execute(query);
        return channel.getName();
    }

    @Override
    public void remove(String name) {
        DeleteConditionStep<ChannelRecord> where = getDslContext()
                .delete(CHANNEL)
                .where(CHANNEL.NAME.eq(name));
        execute(where);
    }

    @Override
    public void remove(Channel channel) {
        DeleteConditionStep<ChannelRecord> where = getDslContext()
                .delete(CHANNEL)
                .where(CHANNEL.NAME.eq(channel.getName()));
        execute(where);
    }

    @Override
    public Channel getByName(String name) {
        SelectConditionStep<ChannelRecord> where = getDslContext()
                .selectFrom(CHANNEL)
                .where(CHANNEL.NAME.eq(name));
        return fetchOne(where, listRecordRowMapper);
    }

}
