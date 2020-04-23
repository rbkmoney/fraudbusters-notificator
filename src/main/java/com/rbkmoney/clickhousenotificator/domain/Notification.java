package com.rbkmoney.clickhousenotificator.domain;

import com.rbkmoney.clickhousenotificator.constant.Status;
import lombok.Data;

import java.util.List;

@Data
public class Notification {

    private String id;

    private String select;
    private String period;

    private List<Parameter> parameters;

    private Template template;

    private String frequency;
    private String alertChanel;

    private Status enabled;

}
