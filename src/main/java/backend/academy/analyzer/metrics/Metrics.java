package backend.academy.analyzer.metrics;

import backend.academy.analyzer.enums.ResponseCode;
import com.google.common.math.Quantiles;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Metrics {

    private Metrics() {
    }

    public static ResponseCode getStatusCodeOrDefault(int code) {
        for (ResponseCode status : ResponseCode.values()) {
            if (status.code() == code) {
                return status;
            }
        }
        return ResponseCode.UNKNOWN;
    }

    public static String getFromDate(MetricsFacade metricsFacade) {
        return metricsFacade.getFromDate() == null ? "-" : metricsFacade.getFromDate();
    }

    public static String getToDate(MetricsFacade metricsFacade) {
        return metricsFacade.getToDate() == null ? "-" : metricsFacade.getToDate();
    }

    public static int getTotalCountRequest(MetricsFacade metricsFacade) {
        return metricsFacade.getResourceCountMap().size();
    }

    public static String getFrequentlyRequestedResources(MetricsFacade metricsFacade) {
        Optional<Map.Entry<String, Integer>> mostFrequentResource =
            metricsFacade.getResourceCountMap().entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentResource.isPresent()) {
            Map.Entry<String, Integer> entry = mostFrequentResource.get();
            return entry.getKey();
        } else {
            return "Нет данных о запрашиваемых ресурсах.";
        }
    }

    public static int getFrequentlyRequestedCode(MetricsFacade metricsFacade) {
        Optional<Map.Entry<ResponseCode, Integer>> mostFrequentResource =
            metricsFacade.getResponseCodeCountMap().entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostFrequentResource.isPresent()) {
            Map.Entry<ResponseCode, Integer> entry = mostFrequentResource.get();
            return entry.getKey().code();
        } else {
            return ResponseCode.UNKNOWN.code();
        }
    }

    public static double getAverageSizeServerResponse(MetricsFacade metricsFacade) {
        int totalBytes = metricsFacade.getBytesList().stream().mapToInt(Integer::intValue).sum();
        return (double) totalBytes / metricsFacade.getBytesList().size();
    }

    public static double getPercentile(MetricsFacade metricsFacade) {
        List<Integer> bytesList = metricsFacade.getBytesList();
        if (bytesList.isEmpty()) {
            return 0;
        }
        //CHECKSTYLE:OFF
        return Quantiles.percentiles().index(95).compute(bytesList);
        //CHECKSTYLE:ON
    }

    public static int getUniqueClientsCount(MetricsFacade metricsFacade) {
        return metricsFacade.getUniqueClientsCount();
    }

    public static String getMostFrequentMethod(MetricsFacade metricsFacade) {
        return metricsFacade.getMostFrequentMethod();
    }
}
