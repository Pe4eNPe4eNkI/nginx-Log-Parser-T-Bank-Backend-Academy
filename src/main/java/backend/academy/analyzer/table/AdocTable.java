package backend.academy.analyzer.table;

import backend.academy.analyzer.LogParser;
import backend.academy.analyzer.enums.RecordFormat;
import backend.academy.analyzer.enums.ResponseCode;
import backend.academy.analyzer.metrics.MetricsFacade;
import java.util.Map;

public class AdocTable extends DrawingTable {

    private final MetricsFacade metricsFacade;

    public AdocTable(RecordFormat recordFormat) {
        super(recordFormat);
        this.metricsFacade = new MetricsFacade();
    }

    @Override
    public String generateMetricsTable() {
        StringBuilder table = new StringBuilder();
        //CHECKSTYLE:OFF
        table.append("==== Общая информация\n\n")
            .append("|===\n")
            .append("| Файл(-ы) | ").append(String.join("\n", LogParser.PROCESSED_LOG_FILES())).append(" |\n")
            .append("| Начальная дата | ").append(metricsFacade.getFromDate()).append(" |\n")
            .append("| Конечная дата | ").append(metricsFacade.getToDate()).append(" |\n")
            .append("| Метрика | Значение\n")
            .append("| Количество запросов | ").append(metricsFacade.getTotalCountRequest()).append("\n")
            .append("| Наиболее часто встречающийся ресурс | ").append(metricsFacade.getFrequentlyRequestedResources())
            .append("\n")
            .append("| Наиболее часто встречающийся код ответа | ").append(metricsFacade.getFrequentlyRequestedCode())
            .append("\n")
            .append("| Средний размер ответа | ").append(metricsFacade.getAverageSizeServerResponse()).append("\n")
            .append("| 95p размера ответа | ").append(metricsFacade.getPercentile()).append("\n")
            .append("| Кол-во уникальных ip | ").append(metricsFacade.getUniqueClientsCount()).append(" |\n")
            .append("| Наиболее часто используемый HTTP метод | ").append(metricsFacade.getMostFrequentMethod())
            .append(" |\n")
            .append("|===\n\n");
        //CHECKSTYLE:ON
        return table.toString();
    }

    @Override
    public String generateResponseCodeTable() {
        StringBuilder table = new StringBuilder();
        //CHECKSTYLE:OFF
        table.append("==== Коды ответа\n\n")
            .append("|===\n")
            .append("| Код | Имя | Количество\n");

        for (Map.Entry<ResponseCode, Integer> entry : LogParser.RESPONSE_CODE_COUNT_MAP().entrySet()) {
            ResponseCode statusCode = entry.getKey();
            int code = statusCode.code();
            String name = statusCode.description();
            int count = entry.getValue();
            table.append(String.format("| %d | %-20s | %10d\n", code, name, count));
        }

        table.append("|===\n\n");
        //CHECKSTYLE:OFF
        return table.toString();
    }

    @Override
    public String generateResourceTable() {
        StringBuilder table = new StringBuilder();
        //CHECKSTYLE:OFF
        table.append("==== Запрашиваемые ресурсы\n\n")
            .append("|===\n")
            .append("| Ресурс | Количество\n");

        for (Map.Entry<String, Integer> entry : LogParser.RESOURCE_COUNT_MAP().entrySet()) {
            String resource = entry.getKey();
            int count = entry.getValue();
            table.append(String.format("| %-15s | %10d\n", resource, count));
        }

        table.append("|===\n\n");
        //CHECKSTYLE:OFF
        return table.toString();
    }
}
