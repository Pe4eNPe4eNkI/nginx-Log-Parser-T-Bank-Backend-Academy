package backend.academy.analyzer;

import backend.academy.analyzer.enums.Param;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogHandler {

    public Map<String, String> initialize(String[] args) throws IOException {
        Map<String, String> options = parseArguments(args);
        String path = options.get(Param.PATH.symbol());

        LogParser logParser = createLogParser(options);

        logParser.initializeTimeFilters();

        if (isURL(path)) {
            logParser.processLogsFromURL();
        } else {
            logParser.processLogsFromFile();
        }

        return options;
    }

    static Map<String, String> parseArguments(String[] args) {
        Map<String, String> options = new HashMap<>();

        Map<String, String> paramMap = Map.of(
            Param.PATH.symbol(), Param.PATH.symbol(),
            Param.FROM.symbol(), Param.FROM.symbol(),
            Param.TO.symbol(), Param.TO.symbol(),
            Param.FORMAT.symbol(), Param.FORMAT.symbol(),
            Param.FILTER_FIELD.symbol(), Param.FILTER_FIELD.symbol(),
            Param.FILTER_VALUE.symbol(), Param.FILTER_VALUE.symbol()
        );

        for (int i = 0; i < args.length - 1; i++) {
            String key = args[i];
            String value = args[i + 1];

            if (paramMap.containsKey(key)) {
                options.put(key, value);
            }
        }

        return options;
    }

    static boolean isURL(String path) {
        return path.contains("http://") || path.contains("https://");
    }

    protected LogParser createLogParser(Map<String, String> options) {
        return new LogParser(options);
    }
}
