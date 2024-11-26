package backend.academy.analyzer.table;

public interface DrawingTableInterface {

    void initialized();

    String generateResponseCodeTable();

    String generateResourceTable();

    String generateMetricsTable();

    void writeToFile(String filename, String content);
}
