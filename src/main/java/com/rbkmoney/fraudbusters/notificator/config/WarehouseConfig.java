package com.rbkmoney.fraudbusters.notificator.config;

import com.rbkmoney.fraudbusters.warehouse.QueryServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class WarehouseConfig {

    @Bean
    public QueryServiceSrv.Iface bouncerClient(@Value("${warehouse.url}") Resource resource,
                                               @Value("${warehouse.networkTimeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withNetworkTimeout(networkTimeout)
                .withAddress(resource.getURI())
                .build(QueryServiceSrv.Iface.class);
    }

}
