package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ChannelType;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;
import com.rbkmoney.mapper.RecordRowMapper;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.CHANNEL;

@Component
public class ChannelDaoImpl extends AbstractDao implements ChannelDao {

    private final RowMapper<Channel> listRecordRowMapper;

    public ChannelDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(CHANNEL, Channel.class);
    }

    @Override
    public Channel insert(Channel channel) {
        Query query = getDslContext()
                .insertInto(CHANNEL)
                .set(getDslContext().newRecord(CHANNEL, channel))
                .onConflict(CHANNEL.NAME)
                .doUpdate()
                .set(getDslContext().newRecord(CHANNEL, channel));
        execute(query);
        return channel;
    }

    @Override
    public void remove(String name) {
        DeleteConditionStep<ChannelRecord> where = getDslContext()
                .delete(CHANNEL)
                .where(CHANNEL.NAME.eq(name));
        execute(where);
    }

    @Override
    public Channel getByName(String name) {
        SelectConditionStep<ChannelRecord> where = getDslContext()
                .selectFrom(CHANNEL)
                .where(CHANNEL.NAME.eq(name));
        return fetchOne(where, listRecordRowMapper);
    }

    @Override
    public List<Channel> getAll(FilterDto filter) {
        return fetch(getDslContext()
                .selectFrom(CHANNEL), listRecordRowMapper);
    }

    @Override
    public List<ChannelType> getAllTypes() {
        return getDslContext().select(CHANNEL.TYPE)
                .from(CHANNEL)
                .fetch()
                .stream()
                .map(Record1::value1)
                .collect(Collectors.toList());
    }

}
