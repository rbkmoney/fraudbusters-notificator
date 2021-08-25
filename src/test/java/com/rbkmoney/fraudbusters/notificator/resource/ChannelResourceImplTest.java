package com.rbkmoney.fraudbusters.notificator.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ChannelRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.CHANNEL;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@PostgresqlSpringBootITest
class ChannelResourceImplTest {

    @Autowired
    private WebApplicationContext context;

    protected final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    protected MockMvc mockMvc;

    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void createOrUpdate() throws Exception {
        Channel channel = TestObjectsFactory.testChannel();
        MvcResult result = mockMvc.perform(post("/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(channel)))
                .andExpect(status().isOk())
                .andReturn();

        Channel actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);
        assertEquals(channel, actualResponse);

    }

    @Test
    void delete() throws Exception {
        Channel channel = TestObjectsFactory.testChannel();
        dslContext.insertInto(CHANNEL)
                .set(dslContext.newRecord(CHANNEL, channel))
                .execute();

        mockMvc.perform(MockMvcRequestBuilders.delete("/channels/{name}", channel.getName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    void getAll() throws Exception {
        ChannelRecord channel1 = TestObjectsFactory.testChannelRecord();
        ChannelRecord channel2 = TestObjectsFactory.testChannelRecord();
        dslContext.insertInto(CHANNEL)
                .set(channel1)
                .newRecord()
                .set(channel2)
                .execute();

        mockMvc.perform(MockMvcRequestBuilders.get("/channels")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(status().isOk());

    }
}