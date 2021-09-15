package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.CHANNEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ActiveProfiles("test")
@PostgresqlSpringBootITest
class ChannelDaoImplTest {

    @Autowired
    DSLContext dslContext;

    @Autowired
    ChannelDao channelDao;

    @Test
    void getAll() {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        ChannelRecord channel2 = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .newRecord()
                .set(channel2)
                .execute();

        List<Channel> all = channelDao.getAll(new FilterDto());

        assertEquals(2, all.size());
        assertIterableEquals(List.of(channel1.getName(), channel2.getName()),
                all.stream()
                        .map(Channel::getName)
                        .collect(Collectors.toList()));
    }

    @Test
    void getAllWithFilterContinuationId() {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        channel1.setName("a");
        ChannelRecord channel2 = TestObjectsFactory.testChannelRecord();
        channel2.setName("b");
        ChannelRecord channel3 = TestObjectsFactory.testChannelRecord();
        channel3.setName("c");
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .newRecord()
                .set(channel2)
                .newRecord()
                .set(channel3)
                .execute();

        FilterDto filter = new FilterDto();
        filter.setContinuationString(channel1.getName());
        List<Channel> all = channelDao.getAll(filter);

        assertEquals(2, all.size());
        assertIterableEquals(List.of(channel2.getName(), channel3.getName()),
                all.stream()
                        .map(Channel::getName)
                        .collect(Collectors.toList()));
    }

    @Test
    void getAllWithFilterSearchField() {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        ChannelRecord channel2 = TestObjectsFactory.testChannelRecord();
        ChannelRecord channel3 = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .newRecord()
                .set(channel2)
                .newRecord()
                .set(channel3)
                .execute();

        FilterDto filter = new FilterDto();
        filter.setSearchFiled(channel1.getName());
        List<Channel> all = channelDao.getAll(filter);

        assertEquals(1, all.size());
        assertEquals(channel1.getDestination(), all.get(0).getDestination());
    }

    @Test
    void getAllWithFilterSize() {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        channel1.setName("a");
        ChannelRecord channel2 = TestObjectsFactory.testChannelRecord();
        channel2.setName("b");
        ChannelRecord channel3 = TestObjectsFactory.testChannelRecord();
        channel3.setName("c");
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .newRecord()
                .set(channel2)
                .newRecord()
                .set(channel3)
                .execute();

        FilterDto filter = new FilterDto();
        filter.setSize(2L);
        List<Channel> all = channelDao.getAll(filter);

        assertEquals(2, all.size());
        assertIterableEquals(List.of(channel1.getName(), channel2.getName()),
                all.stream()
                        .map(Channel::getName)
                        .collect(Collectors.toList()));
    }
}