package com.rbkmoney.fraudbusters.notificator.domain;

import lombok.Data;

import java.util.List;

@Data
public class ValidationResponse {

    private List<ValidationError> errors;
    private String result;

}