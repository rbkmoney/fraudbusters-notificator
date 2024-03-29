package com.rbkmoney.fraudbusters.notificator;

import com.rbkmoney.fraudbusters.notificator.dao.NotificationDao;
import com.rbkmoney.fraudbusters.notificator.dao.NotificationTemplateDao;
import com.rbkmoney.fraudbusters.notificator.dao.ReportNotificationDao;
import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;
import com.rbkmoney.fraudbusters.notificator.serializer.QueryResultSerde;
import com.rbkmoney.fraudbusters.notificator.service.QueryService;
import com.rbkmoney.fraudbusters.notificator.service.iface.NotificationService;
import com.rbkmoney.testcontainers.annotations.DefaultSpringBootTest;
import com.rbkmoney.testcontainers.annotations.postgresql.PostgresqlTestcontainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@PostgresqlTestcontainer
@DefaultSpringBootTest
public class ScheduledIntegrationTest {

    @MockBean
    NotificationDao notificationDao;
    @MockBean
    ReportNotificationDao reportNotificationDao;
    @MockBean
    NotificationTemplateDao notificationTemplateDao;
    @MockBean
    QueryService queryService;
    @MockBean
    QueryResultSerde queryResultSerde;
    @MockBean
    NotificationService notificationService;
    @MockBean
    @Qualifier("readyForNotifyFilter")
    Predicate<ReportModel> readyForNotifyFilter;

    @Test
    void scheduleTest() throws InterruptedException {
        Thread.sleep(1000L);

        when(notificationDao.getByStatus(any())).thenReturn(List.of());
        verify(notificationDao, times(1)).getByStatus(any());
    }
}
