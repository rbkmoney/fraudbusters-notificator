package com.rbkmoney.fraudbusters.notificator.service.factory;

import com.rbkmoney.fraudbusters.notificator.dao.ChannelDao;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.pojos.Channel;
import com.rbkmoney.fraudbusters.notificator.domain.Attachment;
import com.rbkmoney.fraudbusters.notificator.domain.Message;
import com.rbkmoney.fraudbusters.notificator.domain.ReportModel;
import com.rbkmoney.fraudbusters.notificator.exception.UnknownRecipientException;
import com.rbkmoney.fraudbusters.notificator.serializer.QueryResultSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailFactory {

    private final ChannelDao channelDao;
    private final AttachmentFactory attachmentFactory;
    private final QueryResultSerde queryResultSerde;
    @Value("${mail.smtp.from-address}")
    public String fromAddress;

    public Optional<Message> create(ReportModel reportModel) {
        String alertChannel = reportModel.getNotification().getAlertchanel();
        Channel channel = channelDao.getByName(alertChannel);
        if (channel == null) {
            log.warn("Not found channel with name: {}", alertChannel);
            return Optional.empty();
        }
        String subject = initSubject(reportModel, channel);
        return Optional.of(Message.builder()
                .content(reportModel.getNotification().getTemplateValue())
                .to(initRecipient(channel))
                .subject(subject)
                .from(fromAddress)
                .attachment(initAttachment(reportModel, subject))
                .build());
    }

    private String initSubject(ReportModel reportModel, Channel channel) {
        String subject = reportModel.getNotification().getSubject();
        if (!StringUtils.hasLength(subject)) {
            subject = channel.getSubject();
        }
        return subject;
    }

    private Attachment initAttachment(ReportModel reportModel, String subject) {
        return queryResultSerde.deserialize(reportModel.getCurrentReport().getResult())
                .map(queryResult ->
                        Attachment.builder()
                                .content(attachmentFactory.create(queryResult.getResults()))
                                .fileName(attachmentFactory.createNameOfAttachment(subject))
                                .build())
                .orElse(null);
    }

    private String[] initRecipient(Channel channel) {
        String[] split = channel.getDestination().trim().split("\\s*,\\s*");
        if (split.length == 0) {
            throw new UnknownRecipientException("Unknown recipient or can't parse: " + channel.getDestination());
        }
        return split;
    }

}
