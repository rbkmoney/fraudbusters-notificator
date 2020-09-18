package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Notification;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QueryPreparationServiceTest {

    QueryPreparationService queryPrepareService = new QueryPreparationService();

    @Test
    public void prepare() {
        Notification notification = new Notification();
        notification.setQueryText("select * from analytic.events_sink_refund where $currentMonth <= toMoth(timestamp) " +
                "and $currentDate <= timestamp " +
                "and $currentYear <= toYear(timestamp) ");
        String prepare = queryPrepareService.prepare(notification);

        LocalDateTime now = LocalDateTime.now();
        Assert.assertEquals("select * from analytic.events_sink_refund where " + now.getMonth().getValue() + " <= toMoth(timestamp) " +
                "and '" + now.format(DateTimeFormatter.ISO_DATE) + "' <= timestamp " +
                "and " + now.getYear() + " <= toYear(timestamp) ", prepare);
    }
}