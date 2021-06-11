package com.rbkmoney.clickhousenotificator;

import com.rbkmoney.clickhousenotificator.config.ScheduleConfig;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.processor.QueryProcessorImpl;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import com.rbkmoney.clickhousenotificator.service.QueryService;
import com.rbkmoney.clickhousenotificator.service.iface.NotificationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ScheduleConfig.class, QueryProcessorImpl.class})
@SpringBootTest(properties = {"schedule.cron=*/1 * * * * *"})
class ScheduledIntegrationTest {

    @Autowired
    QueryProcessorImpl queryProcessor;

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
    Predicate<ReportModel> readyForNotifyFilter;

    @Test
    void scheduleTest() throws InterruptedException {
        Thread.sleep(2000L);
        when(notificationDao.getByStatus(any())).thenReturn(List.of());
        verify(notificationDao.getByStatus(any()), times(1));
    }
}
