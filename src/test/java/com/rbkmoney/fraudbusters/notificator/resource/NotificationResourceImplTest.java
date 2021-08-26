package com.rbkmoney.fraudbusters.notificator.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationRecord;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.NotificationTemplateRecord;
import com.rbkmoney.fraudbusters.notificator.domain.ValidationResponse;
import com.rbkmoney.fraudbusters.notificator.exception.WarehouseQueryException;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import org.apache.thrift.TException;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION;
import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.NOTIFICATION_TEMPLATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@PostgresqlSpringBootITest
class NotificationResourceImplTest {

    @Autowired
    private WebApplicationContext context;

    protected final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    protected MockMvc mockMvc;

    @Autowired
    private DSLContext dslContext;

    @MockBean
    private QueryService queryService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void createOrUpdate() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification();
        notification.setTemplateId(savedNotificationTemplate.getId());
        Map<String, String> values = new HashMap<>();
        values.put("key", "value");
        List<Map<String, String>> queryResult = List.of(values);
        when(queryService.query(savedNotificationTemplate.getQueryText())).thenReturn(queryResult);

        MvcResult result = mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        Notification createdNotification =
                objectMapper.readValue(result.getResponse().getContentAsString(), Notification.class);

        assertFalse(dslContext.fetch(NOTIFICATION).isEmpty());
        assertNotNull(createdNotification.getId());
        assertEquals(notification.getName(), createdNotification.getName());
        assertEquals(notification.getFrequency(), createdNotification.getFrequency());
        assertEquals(notification.getPeriod(), createdNotification.getPeriod());
        assertEquals(notification.getTemplateId(), createdNotification.getTemplateId());
        assertEquals(notification.getChannel(), createdNotification.getChannel());
    }

    @Test
    void delete() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord();
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);

        mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/{id}", savedNotification.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(dslContext.fetch(NOTIFICATION).isEmpty());
    }

    @Test
    void setStatus() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        NotificationRecord notification = TestObjectsFactory.testNotificationRecord();
        notification.setTemplateId(savedNotificationTemplate.getId());
        dslContext.insertInto(NOTIFICATION)
                .set(notification)
                .execute();
        NotificationRecord savedNotification = dslContext.fetchOne(NOTIFICATION);
        NotificationStatus newStatus = NotificationStatus.ARCHIVE;

        MvcResult result = mockMvc.perform(put("/notifications/{id}/statuses", savedNotification.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStatus)))
                .andExpect(status().isOk())
                .andReturn();

        NotificationStatus actualStatus =
                objectMapper.readValue(result.getResponse().getContentAsString(), NotificationStatus.class);
        NotificationRecord updatedNotification = dslContext.fetchOne(NOTIFICATION);
        assertEquals(newStatus, updatedNotification.getStatus());
        assertEquals(newStatus, actualStatus);
    }

    @Test
    void validateWithFieldError() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification();
        notification.setTemplateId(savedNotificationTemplate.getId());
        notification.setName(null);
        notification.setChannel(null);

        MvcResult result = mockMvc.perform(post("/notifications/validating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        ValidationResponse validationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), ValidationResponse.class);

        assertNull(validationResponse.getResult());
        assertEquals(2, validationResponse.getErrors().size());
    }

    @Test
    void validateWithQueryError() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification();
        notification.setTemplateId(savedNotificationTemplate.getId());
        when(queryService.query(savedNotificationTemplate.getQueryText()))
                .thenThrow(new WarehouseQueryException(new TException()));

        MvcResult result = mockMvc.perform(post("/notifications/validating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        ValidationResponse validationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), ValidationResponse.class);

        assertNull(validationResponse.getResult());
        assertEquals(1, validationResponse.getErrors().size());
        assertThat(validationResponse.getErrors().get(0).getErrorReason(), containsString("Query has error"));
    }

    @Test
    void validateWithAllErrors() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification();
        notification.setTemplateId(savedNotificationTemplate.getId());
        notification.setName(null);
        notification.setChannel(null);
        when(queryService.query(anyString())).thenThrow(new WarehouseQueryException(new TException()));

        MvcResult result = mockMvc.perform(post("/notifications/validating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        ValidationResponse validationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), ValidationResponse.class);

        assertNull(validationResponse.getResult());
        assertEquals(3, validationResponse.getErrors().size());
    }

    @Test
    void validateOk() throws Exception {
        dslContext.insertInto(NOTIFICATION_TEMPLATE)
                .set(TestObjectsFactory.testNotificationTemplateRecord())
                .execute();
        NotificationTemplateRecord savedNotificationTemplate = dslContext.fetchAny(NOTIFICATION_TEMPLATE);
        Notification notification = TestObjectsFactory.testNotification();
        notification.setTemplateId(savedNotificationTemplate.getId());
        Map<String, String> values = new HashMap<>();
        values.put("key", "value");
        List<Map<String, String>> queryResult = List.of(values);
        when(queryService.query(savedNotificationTemplate.getQueryText())).thenReturn(queryResult);

        MvcResult result = mockMvc.perform(post("/notifications/validating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andReturn();

        ValidationResponse validationResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), ValidationResponse.class);

        assertTrue(CollectionUtils.isEmpty(validationResponse.getErrors()));
        assertEquals(String.valueOf(queryResult), validationResponse.getResult());
    }
}
