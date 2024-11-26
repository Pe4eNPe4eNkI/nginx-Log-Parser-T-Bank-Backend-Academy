package backend.academy.analyzer.metrics;

import backend.academy.analyzer.LogParser;
import backend.academy.analyzer.enums.ResponseCode;
import com.google.common.math.Quantiles;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MetricsFacade {

    public List<Integer> getBytesList() {
        return LogParser.BYTES_LIST();
    }

    public String getFromDate() {
        return LogParser.fromDate() == null ? "-" : LogParser.fromDate().toString();
    }

    public String getToDate() {
        return LogParser.toDate() == null ? "-" : LogParser.toDate().toString();
    }

    public int getTotalCountRequest() {
        return LogParser.RESOURCE_COUNT_MAP().size();
    }

    public String getFrequentlyRequestedResources() {
        Optional<Map.Entry<String, Integer>> mostFrequentResource =
            LogParser.RESOURCE_COUNT_MAP().entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentResource.isPresent()) {
            Map.Entry<String, Integer> entry = mostFrequentResource.get();
            return entry.getKey();
        } else {
            return "Нет данных о запрашиваемых ресурсах.";
        }
    }

    public int getFrequentlyRequestedCode() {
        Optional<Map.Entry<ResponseCode, Integer>> mostFrequentResource =
            LogParser.RESPONSE_CODE_COUNT_MAP().entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentResource.isPresent()) {
            Map.Entry<ResponseCode, Integer> entry = mostFrequentResource.get();
            return entry.getKey().code();
        } else {
            return ResponseCode.UNKNOWN.code();
        }
    }

    public double getAverageSizeServerResponse() {
        int totalBytes = LogParser.BYTES_LIST().stream().mapToInt(Integer::intValue).sum();
        return (double) totalBytes / LogParser.BYTES_LIST().size();
    }

    public double getPercentile() {
        int totalBytes = LogParser.BYTES_LIST().stream().mapToInt(Integer::intValue).sum();
        //CHECKSTYLE:OFF
        return Quantiles.percentiles().index(95).compute(totalBytes);
        //CHECKSTYLE:OFF
    }

    public int getUniqueClientsCount() {
        return LogParser.UNIQUE_CLIENTS().size();
    }

    public String getMostFrequentMethod() {
        Optional<Map.Entry<String, Integer>> mostFrequentMethod =
            LogParser.METHOD_COUNT_MAP().entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentMethod.isPresent()) {
            return mostFrequentMethod.get().getKey();
        } else {
            return "-";
        }
    }

    public Map<ResponseCode, Integer> getResponseCodeCountMap() {
        return LogParser.RESPONSE_CODE_COUNT_MAP();
    }

    public Map<String, Integer> getResourceCountMap() {
        return LogParser.RESOURCE_COUNT_MAP();
    }
}
