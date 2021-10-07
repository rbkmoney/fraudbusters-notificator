package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.damsel.fraudbusters_notificator.*;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.CHANNEL;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@PostgresqlSpringBootITest
class ChannelHandlerTest {

    @Autowired
    private ChannelHandler channelHandler;

    @Autowired
    private DSLContext dslContext;

    @Test
    void createOrUpdate() {
        Channel channel = TestObjectsFactory.testChannel();

        Channel resultChannel = channelHandler.create(channel);

        assertFalse(dslContext.fetch(CHANNEL).isEmpty());
        assertEquals(channel, resultChannel);

    }

    @Test
    void delete() {
        ChannelRecord channel = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel)
                .execute();

        channelHandler.remove(channel.getName());

        assertTrue(dslContext.fetch(CHANNEL).isEmpty());
    }

    @Test
    void getAll() {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        ChannelRecord channel2 = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .newRecord()
                .set(channel2)
                .execute();

        ChannelListResponse result = channelHandler.getAll(new Page(), new Filter());

        assertEquals(2, result.getChannelsSize());
        assertTrue(result.getChannels().stream().map(Channel::getName).anyMatch(s -> s.equals(channel1.getName())));
        assertTrue(result.getChannels().stream().map(Channel::getName).anyMatch(s -> s.equals(channel2.getName())));
    }

    @Test
    void getAllWithFilter() {
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

        Page page = new Page();
        page.setSize(2L);
        ChannelListResponse result = channelHandler.getAll(page, new Filter());

        assertEquals(2, result.getChannelsSize());
        assertTrue(result.getChannels().stream().map(Channel::getName).anyMatch(s -> s.equals(channel1.getName())));
        assertTrue(result.getChannels().stream().map(Channel::getName).anyMatch(s -> s.equals(channel2.getName())));
        assertEquals(channel2.getName(), result.getContinuationId());
    }

    @Test
    void getAllTypes() {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .execute();
        ChannelTypeListResponse result = channelHandler.getAllTypes();

        assertEquals(1, result.getChannelTypesSize());
        assertEquals(channel1.getType().getLiteral(), result.getChannelTypes().get(0));
    }

    @Test
    void getByID() {
        ChannelRecord channel = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel)
                .execute();

        Channel result = channelHandler.getById(channel.getName());

        assertEquals(channel.getName(), result.getName());
        assertEquals(channel.getDestination(), result.getDestination());
    }
}