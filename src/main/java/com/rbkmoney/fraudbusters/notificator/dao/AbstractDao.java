package com.rbkmoney.fraudbusters.notificator.dao;

import com.rbkmoney.dao.impl.AbstractGenericDao;

import javax.sql.DataSource;

public abstract class AbstractDao extends AbstractGenericDao {

    public AbstractDao(DataSource dataSource) {
        super(dataSource);
    }

}
