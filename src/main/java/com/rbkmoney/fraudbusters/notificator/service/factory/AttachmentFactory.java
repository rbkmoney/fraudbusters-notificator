package com.rbkmoney.fraudbusters.notificator.service.factory;

import java.util.List;
import java.util.Map;

public interface AttachmentFactory {

    String create(List<Map<String, String>> list);

    String createNameOfAttachment(String name);

}
