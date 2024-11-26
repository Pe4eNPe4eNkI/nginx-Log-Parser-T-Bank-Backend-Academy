package backend.academy.analyzer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import backend.academy.analyzer.enums.Param;
import backend.academy.analyzer.enums.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class LogParserTest {

    private LogParser logParser;
    private Map<String, String> options;

    @BeforeEach
    void setUp() {
        options = new HashMap<>();
        logParser = new LogParser(options);
    }

    @Test
    void testInitializeTimeFilters() {
        options.put(Param.FROM.symbol(), "2023-01-01");
        options.put(Param.TO.symbol(), "2023-01-31");

        logParser.initializeTimeFilters();

        assertEquals(LocalDateTime.parse("2023-01-01T00:00:00"), LogParser.fromDate());
        assertEquals(LocalDateTime.parse("2023-01-31T23:59:59"), LogParser.toDate());
    }

    @Test
    void testInitializeTimeFilters_NoFilters() {
        logParser.initializeTimeFilters();

        assertNull(LogParser.fromDate());
        assertNull(LogParser.toDate());
    }

    @Test
    void testProcessLogLine_InvalidLog() {
        String invalidLogLine = "INVALID LOG LINE";

        logParser.processLogLine(invalidLogLine);

        assertTrue(LogParser.RESPONSE_CODE_COUNT_MAP().isEmpty());
        assertTrue(LogParser.RESOURCE_COUNT_MAP().isEmpty());
        assertTrue(LogParser.BYTES_LIST().isEmpty());
    }

    @Test
    void testProcessLogsFromFile() throws IOException {
        String tempFilePath = "test_log.txt";
        Path path = Paths.get(tempFilePath);
        String logLine = """
            192.168.1.1 - - [01/Jan/2023:10:00:00 +0000] "GET /home HTTP/1.1" 200 123 "-" "Mozilla/5.0"
            """;
        Files.writeString(path, logLine);

        options.put(Param.PATH.symbol(), tempFilePath);

        logParser.processLogsFromFile();

        assertEquals(1, LogParser.RESPONSE_CODE_COUNT_MAP().size());
        assertEquals(1, LogParser.RESOURCE_COUNT_MAP().size());
        assertEquals(1, LogParser.BYTES_LIST().size());
        assertEquals(1, LogParser.RESPONSE_CODE_COUNT_MAP().get(ResponseCode.OK).intValue());
        assertEquals(123, LogParser.BYTES_LIST().getFirst().intValue());
        assertEquals(1, LogParser.RESOURCE_COUNT_MAP().get("/home").intValue());

        Files.deleteIfExists(path);
    }

    @Test
    void testProcessLogsFromURL() {
        String logLine = """
            192.168.1.1 - - [01/Jan/2023:10:00:00 +0000] "GET /home HTTP/1.1" 200 123 "-" "Mozilla/5.0"
            """;
        String mockUrl = "http://example.com/logs";

        options.put(Param.PATH.symbol(), mockUrl);

        try (MockedConstruction<URL> mockedUrl = Mockito.mockConstruction(URL.class,
            (mock, context) -> when(mock.openStream()).thenReturn(
                new ByteArrayInputStream(logLine.getBytes())))) {

            logParser.processLogsFromURL();

            assertEquals(1, LogParser.RESPONSE_CODE_COUNT_MAP().size());
            assertEquals(1, LogParser.RESOURCE_COUNT_MAP().size());
            assertEquals(2, LogParser.BYTES_LIST().size());
            assertEquals(2, LogParser.RESPONSE_CODE_COUNT_MAP().get(ResponseCode.OK).intValue());
            assertEquals(123, LogParser.BYTES_LIST().getFirst().intValue());
            assertEquals(2, LogParser.RESOURCE_COUNT_MAP().get("/home").intValue());
        }
    }

    @Test
    void testIsWithinTimeRange() {
        options.put(Param.FROM.symbol(), "2023-01-01");
        options.put(Param.TO.symbol(), "2023-01-31");
        logParser.initializeTimeFilters();

        LocalDateTime validTime = LocalDateTime.parse("2023-01-15T12:00:00");
        LocalDateTime beforeTime = LocalDateTime.parse("2022-12-31T23:59:59");
        LocalDateTime afterTime = LocalDateTime.parse("2023-02-01T00:00:00");

        assertTrue(logParser.isWithinTimeRange(validTime));
        assertFalse(logParser.isWithinTimeRange(beforeTime));
        assertFalse(logParser.isWithinTimeRange(afterTime));
    }
}
