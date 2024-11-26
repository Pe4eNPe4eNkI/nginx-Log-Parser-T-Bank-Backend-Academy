package backend.academy.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LogHandlerTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParseArguments() {
        String[] args = {"--path", "logs.txt", "--from", "2023-01-01", "--to", "2023-01-31", "--format", "json"};
        Map<String, String> options = LogHandler.parseArguments(args);

        assertEquals("logs.txt", options.get("--path"));
        assertEquals("2023-01-01", options.get("--from"));
        assertEquals("2023-01-31", options.get("--to"));
        assertEquals("json", options.get("--format"));
    }

    @Test
    void testIsURL() {
        assertTrue(LogHandler.isURL("https://edu.tinkoff.ru//logs"));
        assertTrue(LogHandler.isURL("https://edu.tinkoff.ru//logs"));
        assertFalse(LogHandler.isURL("logs.txt"));
        assertFalse(LogHandler.isURL("/path/to/logs.txt"));
    }

    @Test
    void testInitializeWithMockParser() throws IOException {
        LogParser mockLogParser = mock(LogParser.class);

        TestableLogHandler logHandler = new TestableLogHandler(mockLogParser);

        String tempFilePath = "logs1.txt";
        Path path = Paths.get(tempFilePath);
        Files.createFile(path);

        try {
            String[] args = {"--path", tempFilePath, "--from", "2023-01-01", "--to", "2023-01-31"};

            doNothing().when(mockLogParser).initializeTimeFilters();
            doNothing().when(mockLogParser).processLogsFromFile();

            Map<String, String> result = logHandler.initialize(args);

            verify(mockLogParser).initializeTimeFilters();
            verify(mockLogParser).processLogsFromFile();

            // Проверяем результат
            assertEquals(tempFilePath, result.get("--path"));
            assertEquals("2023-01-01", result.get("--from"));
            assertEquals("2023-01-31", result.get("--to"));
        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void testInitializeWithRealData() throws IOException {
        LogHandler handler = new LogHandler();

        String[] args = {"--path", "https://example.com/logs", "--format", "markdown"};
        Map<String, String> result = handler.initialize(args);

        assertEquals("https://example.com/logs", result.get("--path"));
        assertEquals("markdown", result.get("--format"));
    }
}
