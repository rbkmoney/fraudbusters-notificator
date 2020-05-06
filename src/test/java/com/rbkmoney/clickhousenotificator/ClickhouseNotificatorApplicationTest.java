package com.rbkmoney.clickhousenotificator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.ChannelType;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.NotificationStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.dao.pg.ChannelDaoImpl;
import com.rbkmoney.clickhousenotificator.dao.pg.NotificationDao;
import com.rbkmoney.clickhousenotificator.dao.pg.ReportNotificationDao;
import com.rbkmoney.clickhousenotificator.domain.QueryResult;
import com.rbkmoney.clickhousenotificator.domain.ValidationResponse;
import com.rbkmoney.clickhousenotificator.processor.QueryProcessorImpl;
import com.rbkmoney.clickhousenotificator.resource.NotificationResourceImpl;
import com.rbkmoney.clickhousenotificator.service.MailSenderServiceImpl;
import com.rbkmoney.clickhousenotificator.service.NotificationServiceImpl;
import com.rbkmoney.clickhousenotificator.util.ChInitiator;
import com.rbkmoney.clickhousenotificator.util.TestChQuery;
import com.rbkmoney.damsel.schedule.SchedulatorSrv;
import org.apache.thrift.TException;
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
import org.springframework.util.CollectionUtils;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.clickhousenotificator.util.NotificationFactory.createNotification;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClickhouseNotificatorApplication.class)
@ContextConfiguration(initializers = ClickhouseNotificatorApplicationTest.Initializer.class)
public class ClickhouseNotificatorApplicationTest {

    public static final String CHANNEL = "channel";
    @Autowired
    NotificationDao notificationDao;
    @Autowired
    NotificationResourceImpl notificationResource;
    @Autowired
    ChannelDaoImpl channelDao;
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

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword(),
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.flyway.url=" + postgres.getJdbcUrl(),
                    "spring.flyway.user=" + postgres.getUsername(),
                    "spring.flyway.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }

    @Before
    public void init() throws SQLException, JsonProcessingException {
        ChInitiator.initChDB(clickHouseContainer);
        Channel channel = new Channel();
        channel.setName(CHANNEL);
        channel.setDestination("test@mail.ru");
        channel.setSubject("Тесты");
        channel.setCreatedAt(LocalDateTime.now());
        channel.setType(ChannelType.mail);
        channelDao.insert(channel);

        //create
        Notification successNotify = createNotification("successNotify", TestChQuery.QUERY_METRIC_RECURRENT,
                NotificationStatus.ACTIVE, CHANNEL, "shopId,currency");
        notificationResource.createOrUpdate(successNotify);

        //create
        notificationResource.createOrUpdate(createNotification("failedName", "select * from analytic.events_sink_refund",
                NotificationStatus.ACTIVE, "errorChannel", "test"));
    }

    @Test
    public void contextLoads() throws JsonProcessingException, InterruptedException, TException {
        queryProcessor.executeJob(null);

        List<Report> notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.send);

        String result = notificationByStatus.get(0).getResult();
        QueryResult queryResult = objectMapper.readValue(result, QueryResult.class);
        assertEquals("ad8b7bfd-0760-4781-a400-51903ee8e504", queryResult.getResults().get(0).get("shopId"));

        queryProcessor.executeJob(null);

        notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.created);
        assertEquals(0L, notificationByStatus.size());

        Thread.sleep(1000L);

        queryProcessor.executeJob(null);
        notificationByStatus = reportNotificationDao.getNotificationByStatus(ReportStatus.skipped);
        assertEquals(1L, notificationByStatus.size());

        verify(mailSenderServiceImpl, times(1)).send(any());
    }

    @Test
    public void validateTest() {
        Notification notify = createNotification("successNotify", TestChQuery.QUERY_METRIC_RECURRENT,
                NotificationStatus.ACTIVE, CHANNEL, "shopId,currency");
        ValidationResponse successNotify = notificationResource.validate(notify);

        Assert.assertTrue(CollectionUtils.isEmpty(successNotify.getErrors()));

        notify.setQueryText("SELECT test from analytic.events_sink_refund");
        successNotify = notificationResource.validate(notify);

        System.out.println(successNotify);
        Assert.assertEquals(1, successNotify.getErrors().size());

        successNotify = notificationResource.validate(createNotification(null, "",
                NotificationStatus.ACTIVE, null, null));

        System.out.println(successNotify);
        Assert.assertEquals(5, successNotify.getErrors().size());
    }

}
