package com.rbkmoney.fraudbusters.notificator.service.iface;

import com.rbkmoney.fraudbusters.notificator.domain.Message;

public interface MailSenderService {

    boolean send(Message message);

}
