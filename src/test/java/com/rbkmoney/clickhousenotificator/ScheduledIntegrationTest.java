package com.rbkmoney.clickhousenotificator;

import com.rbkmoney.clickhousenotificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import com.rbkmoney.clickhousenotificator.service.QueryService;
import com.rbkmoney.clickhousenotificator.service.iface.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@PostgresqlSpringBootITest
public class ScheduledIntegrationTest {

    @MockBean
    NotificationDao notificationDao;
    @MockBean
    ReportNotificationDao reportNotificationDao;
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
