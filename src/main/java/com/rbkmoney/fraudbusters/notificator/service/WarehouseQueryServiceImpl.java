package com.rbkmoney.fraudbusters.notificator.service;

import com.rbkmoney.fraudbusters.notificator.exception.WarehouseQueryException;
import com.rbkmoney.fraudbusters.warehouse.Query;
import com.rbkmoney.fraudbusters.warehouse.QueryServiceSrv;
import com.rbkmoney.fraudbusters.warehouse.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseQueryServiceImpl implements WarehouseQueryService {

    private final QueryServiceSrv.Iface service;

    @Override
    public Result execute(Query query) {
        try {
            return service.execute(query);
        } catch (TException e) {
            log.error("Error call warehouse query service", e);
            throw new WarehouseQueryException(e);
        }
    }
}
