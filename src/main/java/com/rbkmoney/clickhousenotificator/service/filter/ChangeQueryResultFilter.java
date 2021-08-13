package com.rbkmoney.clickhousenotificator.service.filter;

import com.rbkmoney.clickhousenotificator.dao.domain.tables.pojos.Report;
import com.rbkmoney.clickhousenotificator.domain.QueryResult;
import com.rbkmoney.clickhousenotificator.domain.ReportModel;
import com.rbkmoney.clickhousenotificator.serializer.QueryResultSerde;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeQueryResultFilter implements Predicate<ReportModel> {

    private final QueryResultSerde queryResultSerde;

    @Override
    public boolean test(ReportModel reportModel) {
        Report oldReport = reportModel.getLastReport();
        if (oldReport != null) {
            Optional<QueryResult> oldReportResult = queryResultSerde.deserialize(oldReport.getResult());
            Optional<QueryResult> newReportResult =
                    queryResultSerde.deserialize(reportModel.getCurrentReport().getResult());
            if (newReportResult.isPresent()) {
                if (oldReportResult.isEmpty()) {
                    return true;
                }

                String groupParams = reportModel.getNotification().getGroupbyparams();
                List<String> keys = List.of(groupParams.split(","));

                List<String> groupedKeys = parseGroupedKeys(oldReportResult, keys);

                return newReportResult.get().getResults().stream()
                        .map(map -> concatConcreteValues(keys, map))
                        .anyMatch(key -> !groupedKeys.contains(key));
            }
        }
        return true;
    }

    private List<String> parseGroupedKeys(Optional<QueryResult> oldReportResult, List<String> keys) {
        return oldReportResult
                .map(queryResult -> queryResult.getResults().stream()
                        .map(map -> concatConcreteValues(keys, map))
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    private String concatConcreteValues(List<String> keys, Map<String, String> map) {
        return map.entrySet().stream()
                .filter(entry -> keys.contains(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.joining("-"));
    }

}
