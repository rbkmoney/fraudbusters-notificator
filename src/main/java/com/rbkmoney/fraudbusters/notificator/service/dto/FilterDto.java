package com.rbkmoney.fraudbusters.notificator.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterDto {

    private String searchFiled;
    private PageDto page;

}
