package backend.academy.analyzer;

import java.util.Map;

public class TestableLogHandler extends LogHandler {

    private final LogParser mockLogParser;

    public TestableLogHandler(LogParser mockLogParser) {
        this.mockLogParser = mockLogParser;
    }

    @Override
    protected LogParser createLogParser(Map<String, String> options) {
        return mockLogParser;
    }
}

