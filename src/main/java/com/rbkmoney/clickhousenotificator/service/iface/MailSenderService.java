package com.rbkmoney.clickhousenotificator.service.iface;

import com.rbkmoney.clickhousenotificator.domain.Message;

public interface MailSenderService {

    boolean send(Message message);

}
