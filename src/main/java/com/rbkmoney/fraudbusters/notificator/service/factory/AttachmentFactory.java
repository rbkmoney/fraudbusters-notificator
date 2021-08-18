package com.rbkmoney.fraudbusters.notificator.service.factory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AttachmentFactory {

    String create(List<Map<String, String>> list);

    String createNameOfAttachment(String name);

}
