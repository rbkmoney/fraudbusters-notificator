package com.rbkmoney.clickhousenotificator.util;

import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.SQLException;

public class ChInitiator {

    public static void initChDB(ClickHouseContainer clickHouseContainer) throws SQLException {
        try (Connection connection = getSystemConn(clickHouseContainer)) {
            String sql = FileUtil.getFile("sql/db_init.sql");
            String[] split = sql.split(";");
            for (String exec : split) {
                connection.createStatement().execute(exec);
            }

            sql = FileUtil.getFile("sql/test.data/inserts_event_sink.sql");
            split = sql.split(";");
            for (String exec : split) {
                connection.createStatement().execute(exec);
            }
        }
    }

    protected static Connection getSystemConn(ClickHouseContainer clickHouseContainer) throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }


}
