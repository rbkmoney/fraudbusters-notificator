package com.rbkmoney.fraudbusters.notificator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attachment {

    private String fileName;
    private String content;

}
