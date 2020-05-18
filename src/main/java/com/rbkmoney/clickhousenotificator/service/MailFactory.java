package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.clickhousenotificator.dao.pg.ChannelDao;
import com.rbkmoney.clickhousenotificator.domain.Message;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.exception.UnknownRecipientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
                .to(initRecipient(channel))
                .subject(initSubject(reportModel, channel))
                .from(NOTIFICATION_SERVICE_RBKMONEY_COM)
                .build());
    }

    private String initSubject(ReportModel reportModel, Channel channel) {
        String subject = reportModel.getNotification().getSubject();
        if (StringUtils.isEmpty(subject)) {
            subject = channel.getSubject();
        }
        return subject;
    }

    @NotNull
    private String[] initRecipient(Channel channel) {
        String[] split = channel.getDestination().trim().split("\\s*,\\s*");
        if (split.length == 0) {
            throw new UnknownRecipientException("Unknown recipient or can't parse: " + channel.getDestination());
        }
        return split;
    }

}
