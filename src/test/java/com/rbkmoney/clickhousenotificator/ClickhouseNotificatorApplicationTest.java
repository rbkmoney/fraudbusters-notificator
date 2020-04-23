package com.rbkmoney.clickhousenotificator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.constant.Status;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.QueryResult;
import com.rbkmoney.clickhousenotificator.processor.QueryProcessorImpl;
import com.rbkmoney.clickhousenotificator.service.MailSenderServiceImpl;
import com.rbkmoney.clickhousenotificator.service.NotificationServiceImpl;
import com.rbkmoney.clickhousenotificator.util.ChInitiator;
import com.rbkmoney.clickhousenotificator.util.NotificationFactory;
import com.rbkmoney.clickhousenotificator.util.TestChQuery;
import com.rbkmoney.damsel.schedule.SchedulatorSrv;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClickhouseNotificatorApplication.class)
@ContextConfiguration(initializers = ClickhouseNotificatorApplicationTest.Initializer.class)
public class ClickhouseNotificatorApplicationTest {

    @Autowired
    NotificationDao notificationDao;
    @Autowired
    ReportNotificationDao reportNotificationDao;
    @Autowired
    QueryProcessorImpl queryProcessor;
    @Autowired
    NotificationServiceImpl notificationProcessor;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MailSenderServiceImpl mailSenderServiceImpl;
    @MockBean
    SchedulatorSrv.Iface schedulatorClient;

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    @ClassRule
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
            .withStartupTimeout(Duration.ofMinutes(5));

    @Before
    public void init() throws SQLException {
        ChInitiator.initChDB(clickHouseContainer);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword(),
                    "spring.datasource.hikari.jdbc-url=" + postgres.getJdbcUrl(),
                    "spring.datasource.hikari.username=" + postgres.getUsername(),
                    "spring.datasource.hikari.password=" + postgres.getPassword(),
                    "spring.flyway.url=" + postgres.getJdbcUrl(),
                    "spring.flyway.user=" + postgres.getUsername(),
                    "spring.flyway.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }

    @Test
    public void contextLoads() throws JsonProcessingException, InterruptedException {
        //create
        Notification notification = NotificationFactory.createNotification(TestChQuery.QUERY_METRIC_RECURRENT, Status.ACTIVE);
        notificationDao.insert(notification);

        queryProcessor.process();

        List<Report> notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.send);

        String result = notificationByStatus.get(0).getResult();
        QueryResult queryResult = objectMapper.readValue(result, QueryResult.class);
        Assert.assertEquals("ad8b7bfd-0760-4781-a400-51903ee8e504", queryResult.getResults().get(0).get("shopId"));

        queryProcessor.process();

        notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.created);
        Assert.assertEquals(0L, notificationByStatus.size());

        Thread.sleep(1000L);

        queryProcessor.process();
        notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.skipped);
        Assert.assertEquals(1L, notificationByStatus.size());
    }

}
