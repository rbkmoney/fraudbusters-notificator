package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.clickhousenotificator.dao.pg.ChannelDao;
import com.rbkmoney.clickhousenotificator.domain.Message;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailFactory {

    public static final String NOTIFICATION_SERVICE_RBKMONEY_COM = "NotificationService@rbkmoney.com";

    private final ChannelDao channelDao;

    public Optional<Message> create(ReportModel reportModel) {
        String alertchanel = reportModel.getNotification().getAlertchanel();
        Channel channel = channelDao.getByName(alertchanel);
        if (channel == null) {
            log.warn("Not found channel with name: {}", alertchanel);
            return Optional.empty();
        }
        return Optional.of(Message.builder()
                .content(reportModel.getCurrentReport().getResult())
                .to(channel.getDestination())
                .subject(channel.getSubject())
                .from(NOTIFICATION_SERVICE_RBKMONEY_COM)
                .build());
    }

}
