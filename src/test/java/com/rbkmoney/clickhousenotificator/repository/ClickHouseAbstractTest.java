package com.rbkmoney.clickhousenotificator.repository;

import com.rbkmoney.clickhousenotificator.config.ClickhouseConfig;
import com.rbkmoney.clickhousenotificator.util.ChInitiator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.ClickHouseContainer;

import java.sql.SQLException;

@Slf4j
@ContextConfiguration(initializers = ClickHouseAbstractTest.Initializer.class,
        classes = {JdbcTemplateAutoConfiguration.class, ClickhouseConfig.class})
public class ClickHouseAbstractTest {

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Before
    public void init() throws SQLException {
        ChInitiator.initChDB(clickHouseContainer);
    }

}
