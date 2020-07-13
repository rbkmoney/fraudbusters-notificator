package com.rbkmoney.clickhousenotificator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attachment {

    private String fileName;
    private String content;

}
