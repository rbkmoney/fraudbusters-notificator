package com.rbkmoney.clickhousenotificator.service;

import com.rbkmoney.clickhousenotificator.domain.Message;
import com.rbkmoney.clickhousenotificator.service.iface.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;

    public boolean send(Message message) {
        try {
            log.info("Trying to send message to mail, partyId={}, claimId={}, email={}", message.getPartyId(),
                    message.getClaimId(), message.getTo());
            MimeMessage mimeMessage = getMimeMessage(message);
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException | MailException ex) {
            throw new MailSendException(String.format("Received exception while sending message to mail, partyId=%s, claimId=%s, email=%s",
                    message.getPartyId(), message.getClaimId(), message.getTo()), ex);
        }
    }

    private MimeMessage getMimeMessage(Message message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setFrom(message.getFrom());
        helper.setTo(message.getTo());
        helper.setSubject(message.getSubject());
        helper.setText(message.getContent(), false);
        return mimeMessage;
    }

}
