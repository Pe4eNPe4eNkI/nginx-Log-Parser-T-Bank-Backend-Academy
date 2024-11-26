package backend.academy.analyzer.enums;

import lombok.Getter;

@Getter
public enum RecordFormat {
    ASCIIDOC("adoc", ".adoc"),
    MARKDOWN("markdown", ".md");

    private final String value;
    private final String format;

    RecordFormat(
        String value,
        String format
    ) {
        this.value = value;
        this.format = format;
    }
}
