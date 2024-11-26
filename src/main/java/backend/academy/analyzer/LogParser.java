package backend.academy.analyzer;

import backend.academy.analyzer.enums.Param;
import backend.academy.analyzer.enums.ResponseCode;
import backend.academy.analyzer.metrics.Metrics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogParser {

    private final Map<String, String> options;
    private final String fieldOption;
    private final String valueOption;

    LogParser(Map<String, String> options) {
        this.options = options;
        this.fieldOption = options.get(Param.FILTER_FIELD.symbol());
        this.valueOption = options.get(Param.FILTER_VALUE.symbol());
    }

    //CHECKSTYLE:OFF
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(?<ip>[\\d.:a-fA-F]+) - - \\[(?<time>[^\\]]+)] \"(?<method>\\S+) (?<url>\\S+) (?<protocol>[^\"]+)\"" +
            " (?<status>\\d{3}) (?<bytes>\\d+) \"(?<referer>[^\"]*)\" \"(?<userAgent>[^\"]*)\""
    );
    //CHECKSTYLE:ON

    @Getter
    private static final List<String> PROCESSED_LOG_FILES = new ArrayList<>();
    @Getter
    private static final Map<ResponseCode, Integer> RESPONSE_CODE_COUNT_MAP = new EnumMap<>(ResponseCode.class);
    @Getter
    private static final Map<String, Integer> RESOURCE_COUNT_MAP = new HashMap<>();
    @Getter
    private static final ArrayList<Integer> BYTES_LIST = new ArrayList<>();
    @Getter
    private static final Set<String> UNIQUE_CLIENTS = new HashSet<>();
    @Getter
    private static final Map<String, Integer> METHOD_COUNT_MAP = new HashMap<>();
    @Getter
    private static LocalDateTime fromDate;
    @Getter
    private static LocalDateTime toDate;
    @Getter
    private static final String METHOD_NAME = "method";
    @Getter
    private static final String USER_AGENT = "userAgent";

    public void initializeTimeFilters() {
        String fromOption = options.get(Param.FROM.symbol());
        String toOption = options.get(Param.TO.symbol());

        if (fromOption != null) {
            fromDate = LocalDateTime.parse(fromOption + "T00:00:00");
        } else {
            fromDate = null;
        }

        if (toOption != null) {
            toDate = LocalDateTime.parse(toOption + "T23:59:59");
        } else {
            toDate = null;
        }
    }

    public void processLogsFromURL() {
        try {
            String logUrl = options.get(Param.PATH.symbol());
            URL url = new URL(logUrl);
            PROCESSED_LOG_FILES.add(logUrl);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLogLine(line);
                }
            }
        } catch (IOException e) {
            //CHECKSTYLE:OFF
            log.error("exception in processLogsFromURL: ", e);
            //CHECKSTYLE:ON
        }
    }

    public void processLogsFromFile() throws IOException {
        String filePathPattern = options.get(Param.PATH.symbol());
        String baseDirString = filePathPattern;
        String filePattern;
        if (filePathPattern.contains("**")) {
            baseDirString = filePathPattern.substring(0, filePathPattern.indexOf("**") - 1);
            filePattern = filePathPattern.substring(filePathPattern.lastIndexOf("\\") + 1);
        } else if (filePathPattern.contains("*")) {
            baseDirString = filePathPattern.substring(0, filePathPattern.lastIndexOf("\\"));
            filePattern = filePathPattern.substring(
                filePathPattern.lastIndexOf("\\") + 1);  // Шаблон файла после последнего \\
        } else {
            filePattern = "*";
        }

        Path baseDir = Paths.get(baseDirString);

        // Если шаблон содержит **, ищем рекурсивно, иначе только в указанной директории
        try (Stream<Path> files = Files.walk(baseDir)) {
            files.filter(file -> {
                    String relativePath = baseDir.relativize(file).toString();
                    if (filePathPattern.contains("**")) {
                        return relativePath.matches(filePattern.replace("*", ".*"));
                    } else {
                        return file.getFileName().toString().matches(filePattern.replace("*", ".*"));
                    }
                })
                //CHECKSTYLE:OFF
                .forEach(file -> {
                    PROCESSED_LOG_FILES.add(file.toString());
                    try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            processLogLine(line);
                        }
                    } catch (IOException e) {
                        //CHECKSTYLE:OFF
                        log.error("exception in processLogsFromFile: ", e);
                        //CHECKSTYLE:ON
                    }
                });
            //CHECKSTYLE:ON
        }
    }

    public void processLogLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);

        boolean shouldProcessLine = false;

        if (matcher.matches()) {
            try {
                String method = matcher.group(METHOD_NAME());
                String userAgent = matcher.group(USER_AGENT());

                if (!shouldSkipLine(method, userAgent)) {
                    String time = matcher.group("time");
                    LocalDateTime localLogTime = parseLogTime(time);

                    if (isWithinTimeRange(localLogTime)) {
                        shouldProcessLine = true;
                        processMatchedLog(matcher, method);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to parse log line: {}. Error: {}", line, e.getMessage());
            }
        } else {
            log.error("Log line didn't match pattern: {}", line);
        }

        if (!shouldProcessLine) {
            return;
        }
    }

    private boolean shouldSkipLine(String method, String userAgent) {
        if (fieldOption != null && valueOption != null) {
            if (METHOD_NAME().equals(fieldOption) && !method.equals(valueOption)) {
                return true;
            }
            return USER_AGENT().equals(fieldOption) && !userAgent.contains(valueOption);
        }
        return false;
    }

    private LocalDateTime parseLogTime(String time) {
        ZonedDateTime logTime =
            ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH));
        return logTime.toLocalDateTime();
    }

    private void processMatchedLog(Matcher matcher, String method) {
        String ip = matcher.group("ip");
        int bytes = Integer.parseInt(matcher.group("bytes"));
        int status = Integer.parseInt(matcher.group("status"));
        String resource = matcher.group("url");

        UNIQUE_CLIENTS.add(ip);
        BYTES_LIST.add(bytes);
        METHOD_COUNT_MAP.put(method, METHOD_COUNT_MAP.getOrDefault(method, 0) + 1);

        ResponseCode statusCode = Metrics.getStatusCodeOrDefault(status);
        RESPONSE_CODE_COUNT_MAP.put(statusCode, RESPONSE_CODE_COUNT_MAP.getOrDefault(statusCode, 0) + 1);
        RESOURCE_COUNT_MAP.put(resource, RESOURCE_COUNT_MAP.getOrDefault(resource, 0) + 1);
    }

    boolean isWithinTimeRange(LocalDateTime logTime) {
        if (fromDate != null && logTime.isBefore(fromDate)) {
            return false;
        }
        return toDate == null || !logTime.isAfter(toDate);
    }
}
