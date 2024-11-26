package backend.academy;

import backend.academy.analyzer.LogHandler;
import backend.academy.analyzer.enums.Param;
import backend.academy.analyzer.enums.RecordFormat;
import backend.academy.analyzer.table.AdocTable;
import backend.academy.analyzer.table.DrawingTable;
import backend.academy.analyzer.table.MdTable;
import java.io.IOException;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) throws IOException {
        LogHandler logHandler = new LogHandler();
        Map<String, String> options = logHandler.initialize(args);

        String formatOption = options.get(Param.FORMAT.symbol());
        RecordFormat recordFormat = determineRecordFormat(formatOption);

        DrawingTable drawingTable = createDrawingTable(recordFormat);
        drawingTable.initialized();
    }

    private static RecordFormat determineRecordFormat(String formatOption) {
        if (RecordFormat.ASCIIDOC.value().equals(formatOption)) {
            return RecordFormat.ASCIIDOC;
        } else {
            return RecordFormat.MARKDOWN;
        }
    }

    private static DrawingTable createDrawingTable(RecordFormat recordFormat) {
        return switch (recordFormat) {
            case ASCIIDOC -> new AdocTable(recordFormat);
            case MARKDOWN -> new MdTable(recordFormat);
            default -> new MdTable(recordFormat);
        };
    }
}
