package backend.academy.analyzer.table;

import backend.academy.analyzer.enums.RecordFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DrawingTable implements DrawingTableInterface {

    private final RecordFormat recordFormat;

    public DrawingTable(RecordFormat recordFormat) {
        this.recordFormat = recordFormat;
    }

    public void initialized() {
        StringBuilder allContent = new StringBuilder();
        final String filename = "report" + recordFormat.format();

        allContent.append(generateMetricsTable());
        allContent.append(generateResponseCodeTable());
        allContent.append(generateResourceTable());

        writeToFile(filename, allContent.toString());
    }

    @Override
    public String generateResponseCodeTable() {
        return "";
    }

    @Override
    public String generateResourceTable() {
        return "";
    }

    @Override
    public String generateMetricsTable() {
        return "";
    }

    public void writeToFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) { // append mode
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + filename);
        }
    }
}
