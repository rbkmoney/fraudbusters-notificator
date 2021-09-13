package com.rbkmoney.fraudbusters.notificator.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageDto {

    private Long size;
    private Long continuationId;

}
