package com.rbkmoney.clickhousenotificator.domain;

import com.rbkmoney.clickhousenotificator.constant.TemplateType;
import lombok.Data;

@Data
public class Template {

    private TemplateType type;
    private String value;

}
