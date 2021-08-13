package com.rbkmoney.clickhousenotificator;

import com.rbkmoney.clickhousenotificator.config.ScheduleConfig;
import com.rbkmoney.clickhousenotificator.dao.AbstractPostgresIntegrationTest;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.processor.QueryProcessorImpl;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import com.rbkmoney.clickhousenotificator.service.QueryService;
import com.rbkmoney.clickhousenotificator.service.iface.NotificationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import java.util.List;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ScheduleConfig.class, QueryProcessorImpl.class, DataSource.class})
@SpringBootTest(properties = {"fixedDelay.in.milliseconds=1000"})
@Disabled("Что тут проверяется?")
public class ScheduledIntegrationTest extends AbstractPostgresIntegrationTest {

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
    public void scheduleTest() throws InterruptedException {
        Thread.sleep(1000L);

        when(notificationDao.getByStatus(any())).thenReturn(List.of());
        verify(notificationDao, times(1)).getByStatus(any());
    }
}
