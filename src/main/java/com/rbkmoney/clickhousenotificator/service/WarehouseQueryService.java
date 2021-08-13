package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.fraudbusters.warehouse.Query;
import com.rbkmoney.fraudbusters.warehouse.Result;

public interface WarehouseQueryService {

    Result execute(Query query);

}
