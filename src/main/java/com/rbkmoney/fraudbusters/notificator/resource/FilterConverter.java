package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.damsel.fraudbusters_notificator.Filter;
import com.rbkmoney.damsel.fraudbusters_notificator.Page;
import com.rbkmoney.fraudbusters.notificator.service.dto.FilterDto;
import org.springframework.stereotype.Component;

@Component
public class FilterConverter {

    public FilterDto convert(Page page, Filter filter) {
        FilterDto filterDto = new FilterDto();
        filterDto.setSearchFiled(filter.getSearchField());
        if (page.getSize() > 0) {
            filterDto.setSize(page.getSize());
        }
        try {
            filterDto.setContinuationId(Long.parseLong(page.getContinuationId()));
        } catch (NumberFormatException e) {
            filterDto.setContinuationString(page.getContinuationId());
        }
        return filterDto;
    }
}
