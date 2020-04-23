package com.rbkmoney.clickhousenotificator.domain;

import com.rbkmoney.clickhousenotificator.domain.ValidateError;
import lombok.Data;

import java.util.List;

@Data
public class ValidateResponse {

    private List<ValidateError> errors;
    private String result;

}
